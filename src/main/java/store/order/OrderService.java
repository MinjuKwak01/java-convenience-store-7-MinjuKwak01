package store.order;

import java.util.List;
import java.util.stream.Collectors;
import store.product.Product;
import store.product.ProductList;
import store.promotion.ProductType;
import store.promotion.Promotion;
import store.view.InputView;

public class OrderService {
    private final ProductList productList;
    private final InputView inputView;

    public OrderService(ProductList productList, InputView inputView) {
        this.productList = productList;
        this.inputView = inputView;
    }

    public OrderResult processOrder(String productName, int quantity) {
        List<Product> products = productList.getProducts(productName); //프로모션, 일반 상품 2가지 존재 가능하기 때문에 List
        return processInitialOrder(products, productName, quantity);
    }

    private OrderResult processInitialOrder(List<Product> products, String productName, int quantity) {
        return products.stream()
                .filter(Product::isPromotional) //프로모션중인 상품만 필터링
                .findFirst()
                .map(product -> processPromotionalProduct(products, product, product.getType(), productName, quantity))
                .orElseGet(() -> createNormalOrder(products.getFirst(), quantity)); //일반 상품 주문
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

    private OrderResult processPromotionalProduct(List<Product> products, Product product, ProductType productType,
                                                  String productName,
                                                  int quantity) {
        int promotionalStock = product.getStock();
        if (productType.getPromotion().get().actualBuyableQuantity(promotionalStock) < quantity) {
            return handleInsufficientStock(products, product, productName, quantity, promotionalStock);
        }
        if (product.canGetMoreFromPromotion(quantity) && askForMoreItems(product)) {
            // +1해서 주문 진행
            return processPromotionalStockWithFreeProduct(product, quantity);
        }
        return processOrderWithPromotionButNoFreeProduct(product, quantity);
    }

    private OrderResult processPromotionalStockWithFreeProduct(Product product, int originalQuantity) {
        Promotion promotion = product.getType().getPromotion()
                .orElseThrow(() -> new IllegalStateException("[ERROR] 프로모션 정보가 없습니다."));

        // 프로모션 적용을 위한 필요 수량 계산
        int freeQuantity = promotion.getFreeQuantity();

        // 총 주문 수량 (원래 수량 + 추가 수량)
        int totalOrderQuantity = originalQuantity + freeQuantity;

        product.reduceStock(totalOrderQuantity);

        // 프로모션 적용된 주문 생성
        OrderItem orderItem = new OrderItem(product.getName(), totalOrderQuantity);
        orderItem.setProductInfo(product);

        return new OrderResult(
                orderItem,
                freeQuantity,
                calculateTotalPrice(product, totalOrderQuantity),
                calculatePromotionDiscount(product, freeQuantity)
        );
    }

    private OrderResult processOrderWithPromotionButNoFreeProduct(Product product, int originalQuantity) {
        // 프로모션 적용된 주문 생성
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

    //초과된 프로모션 재고 입력했을때
    private OrderResult handleInsufficientStock(List<Product> products, Product product, String productName,
                                                int quantity,
                                                int promotionalStock) {
        int actualBuyablePromotionQuantity = product.getType().getPromotion().get()
                .actualBuyableQuantity(promotionalStock);
        int nonPromotionalQuantity = quantity - actualBuyablePromotionQuantity;
        if (askForNonPromotionalPurchase(productName, nonPromotionalQuantity)) {
            return createMixedOrder(products, product, actualBuyablePromotionQuantity, nonPromotionalQuantity);
        }
        return processPromotionalStockWithFreeProduct(product, promotionalStock);
    }

    private OrderResult createMixedOrder(List<Product> products, Product promotionalProduct,
                                         int promotionalQuantity, int nonPromotionalQuantity) {
        int totalQuantity = promotionalQuantity + nonPromotionalQuantity;

        // 프로모션 상품과 일반 상품 찾기
        Product normalProduct = findNormalProduct(products);

        // 각각의 재고 차감
        promotionalProduct.reduceStock(promotionalQuantity);
        normalProduct.reduceStock(nonPromotionalQuantity);

        Promotion promotion = promotionalProduct.getType().getPromotion()
                .orElseThrow(() -> new IllegalStateException("[ERROR] 프로모션 정보가 없습니다."));

        int freeQuantity = promotion.calculateFreeQuantity(promotionalQuantity);

        OrderItem orderItem = new OrderItem(promotionalProduct.getName(), totalQuantity);
        orderItem.setProductInfo(promotionalProduct);  // 프로모션 상품 정보 설정

        int totalPrice = calculateTotalPrice(promotionalProduct, totalQuantity);
        int promotionDiscount = calculatePromotionDiscount(promotionalProduct, freeQuantity);

        return new OrderResult(
                orderItem,
                freeQuantity,
                totalPrice,
                promotionDiscount
        );
    }

    private Product findNormalProduct(List<Product> products) {
        return products.stream()
                .filter(p -> !p.isPromotional())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("[ERROR] 일반 상품을 찾을 수 없습니다."));
    }

    private boolean askForNonPromotionalPurchase(String productName, int nonPromotionalQuantity) {
        String message = String.format("현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)",
                productName, nonPromotionalQuantity);
        return inputView.askYesNo(message);
    }

    private boolean askForMoreItems(Product product) {
        String message = String.format(
                "현재 %s은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)",
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
        // 프로모션 미적용 금액의 총합 계산
        int totalNonPromotionalPrice = orderResultList.stream()
                .mapToInt(this::calculateNonPromotionalPrice)
                .sum();

        // 전체 비프로모션 금액에 대한 멤버십 할인 계산
        int totalMembershipDiscount = calculateMembershipDiscount(totalNonPromotionalPrice);

        // 각 주문별 멤버십 할인 금액을 비율에 따라 분배
        return orderResultList.stream()
                .map(order -> {
                    // 각 주문의 비프로모션 금액이 전체에서 차지하는 비율 계산
                    double orderRatio = (double) calculateNonPromotionalPrice(order) / totalNonPromotionalPrice;

                    // 해당 주문의 멤버십 할인 금액 계산
                    int orderMembershipDiscount = (int) (totalMembershipDiscount * orderRatio);

                    // 새로운 OrderResult 생성
                    return new OrderResult(
                            order.getOrderItem(),
                            order.getFreeQuantity(),
                            order.getTotalPrice(),
                            order.getPromotionDiscount(),
                            orderMembershipDiscount
                    );
                })
                .collect(Collectors.toList());
    }

    // 프로모션이 적용되지 않은 금액 계산
    private int calculateNonPromotionalPrice(OrderResult order) {
        OrderItem item = order.getOrderItem();
        Product product = item.getSelectedProduct();
        if (product.isPromotional()) {
            // 프로모션 상품의 경우, 프로모션이 적용되지 않은 수량에 대한 금액만 계산
            return 0;
        }
        // 일반 상품의 경우 전체 금액
        return order.getTotalPrice();
    }


    private int calculateMembershipDiscount(int price) {
        int discount = (int) (price * 0.3);
        return Math.min(discount, 8000);
    }
}