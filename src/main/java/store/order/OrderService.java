package store.order;

import static store.order.OrderErrorMessage.NO_PROMOTION;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import store.product.Product;
import store.product.ProductList;
import store.promotion.ProductType;
import store.promotion.Promotion;
import store.view.InputView;

public class OrderService {
    private static final String ASK_NON_PROMOTIONAL_PURCHASE_MESSAGE = "현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)";
    private static final int MEMBERSHIP_DISCOUNT_LIMIT = 8000;
    private static final Double MEMBERSHIP_DISCOUNT_RATE = 0.3;
    private final ProductList productList;
    private final InputView inputView;
    private final OrderExceedPromotionService orderExceedPromotionService;

    public OrderService(ProductList productList, InputView inputView,
                        OrderExceedPromotionService orderExceedPromotionService) {
        this.productList = productList;
        this.inputView = inputView;
        this.orderExceedPromotionService = orderExceedPromotionService;
    }

    public OrderResult processOrder(String productName, int quantity) {
        List<Product> products = productList.getProducts(productName); //프로모션, 일반 상품 2가지 존재 가능하기 때문에 List
        return processInitialOrder(products, productName, quantity);
    }

    private OrderResult processInitialOrder(List<Product> products, String productName, int quantity) {
        return findValidPromotionalProduct(products)
                .map(product -> processPromotionalProduct(products, product, product.getType(), productName, quantity))
                .orElseGet(() -> createNormalOrder(findFirstProduct(products), quantity));
    }

    private Optional<Product> findValidPromotionalProduct(List<Product> products) {
        return products.stream()
                .filter(Product::isPromotional)
                .filter(this::isValidPromotion)
                .findFirst();
    }

    //프로모션중인 상품이 아닌 경우
    private OrderResult createNormalOrder(Product product, int originalQuantity) {
        product.reduceStock(originalQuantity);
        OrderItem orderItem = new OrderItem(product.getName(), originalQuantity);
        orderItem.setProductInfo(product);
        return new OrderResult(
                orderItem,
                0,
                calculateTotalPrice(product, originalQuantity),
                calculatePromotionDiscount(product, 0)
        );
    }

    private Product findFirstProduct(List<Product> products) {
        return products.getFirst();
    }

    private OrderResult processPromotionalProduct(List<Product> products, Product product, ProductType productType,
                                                  String productName,
                                                  int quantity) {
        int promotionalStock = product.getStock();
        if (productType.getPromotion().get().actualBuyableQuantity(promotionalStock) < quantity) {
            return orderExceedPromotionService.handleInsufficientStock(products, product, productName, quantity,
                    promotionalStock);
        }
        if (product.canGetMoreFromPromotion(quantity) && askForMoreItems(product)) {
            return processPromotionalStockWithFreeProduct(product, quantity);
        }
        return processOrderWithPromotionButNoFreeProduct(product, quantity);
    }

    private OrderResult processPromotionalStockWithFreeProduct(Product product, int originalQuantity) {
        Promotion promotion = product.getType().getPromotion()
                .orElseThrow(() -> new IllegalStateException(NO_PROMOTION.getValue()));
        int freeQuantity = promotion.calculateFreeQuantity(originalQuantity + promotion.getFreeQuantity()); //1
        int totalOrderQuantity = originalQuantity + promotion.getFreeQuantity();
        product.reduceStock(totalOrderQuantity);
        OrderItem orderItem = new OrderItem(product.getName(), totalOrderQuantity);
        orderItem.setProductInfo(product);
        return new OrderResult(orderItem, freeQuantity, calculateTotalPrice(product, totalOrderQuantity),
                calculatePromotionDiscount(product, freeQuantity)
        );
    }

    private OrderResult processOrderWithPromotionButNoFreeProduct(Product product, int originalQuantity) {
        Promotion promotion = product.getType().getPromotion()
                .orElseThrow(() -> new IllegalStateException(NO_PROMOTION.getValue()));
        product.reduceStock(originalQuantity);
        int freeQuantity = promotion.calculateFreeQuantity(originalQuantity);
        OrderItem orderItem = new OrderItem(product.getName(), originalQuantity);
        orderItem.setProductInfo(product);
        return new OrderResult(orderItem, freeQuantity, calculateTotalPrice(product, originalQuantity),
                calculatePromotionDiscount(product, freeQuantity)
        );
    }

    private boolean askForMoreItems(Product product) {
        String message = String.format(
                ASK_NON_PROMOTIONAL_PURCHASE_MESSAGE,
                product.getName()
        );
        return inputView.askYesNo(message);
    }


    private int calculateTotalPrice(Product product, int quantity) {
        return product.getPrice() * quantity;
    }

    private int calculatePromotionDiscount(Product product, int freeQuantity) {
        return product.getPrice() * freeQuantity;
    }

    public List<OrderResult> applyMembershipDiscount(List<OrderResult> orderResultList) {
        int totalNonPromotionalPrice = orderResultList.stream().mapToInt(this::calculateNonPromotionalPrice).sum();
        int totalMembershipDiscount = calculateMembershipDiscount(totalNonPromotionalPrice);
        return orderResultList.stream().map(order -> {
            double orderRatio = (double) calculateNonPromotionalPrice(order) / totalNonPromotionalPrice;
            return new OrderResult(order.getOrderItem(), order.getFreeQuantity(), order.getTotalPrice(),
                    order.getPromotionDiscount(),
                    (int) (totalMembershipDiscount * orderRatio)
            );
        }).collect(Collectors.toList());
    }

    // 프로모션이 적용되지 않은 금액 계산
    private int calculateNonPromotionalPrice(OrderResult order) {
        OrderItem item = order.getOrderItem();
        Product product = item.getSelectedProduct();
        if (product.isPromotional()) {
            return 0;
        }
        return order.getTotalPrice();
    }

    private int calculateMembershipDiscount(int price) {
        int discount = (int) (price * MEMBERSHIP_DISCOUNT_RATE);
        return Math.min(discount, MEMBERSHIP_DISCOUNT_LIMIT);
    }

    private boolean isValidPromotion(Product product) {
        LocalDateTime now = DateTimes.now();
        return product.getType().getPromotion()
                .map(promotion -> promotion.isValidPeriod(now))
                .orElse(false);
    }
}