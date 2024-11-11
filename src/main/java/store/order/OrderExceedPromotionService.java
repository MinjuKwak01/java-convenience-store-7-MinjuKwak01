package store.order;

import static store.order.OrderErrorMessage.NO_NORMAL_PRODUCT;
import static store.order.OrderErrorMessage.NO_PROMOTION;

import java.util.List;
import store.product.Product;
import store.promotion.Promotion;
import store.view.InputView;

public class OrderExceedPromotionService {

    private static final String ASK_NON_PROMOTIONAL_PURCHASE_MESSAGE = "현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)";
    private final InputView inputView;

    public OrderExceedPromotionService(InputView inputView) {
        this.inputView = inputView;
    }

    //초과된 프로모션 재고 입력했을때
    public OrderResult handleInsufficientStock(List<Product> products, Product product, String productName,
                                               int quantity,
                                               int promotionalStock) {
        int actualBuyablePromotionQuantity = product.getType().getPromotion().get()
                .actualBuyableQuantity(promotionalStock);
        int nonPromotionalQuantity = quantity - actualBuyablePromotionQuantity;
        if (askForNonPromotionalPurchase(productName, nonPromotionalQuantity)) {
            return createMixedOrder(products, product, actualBuyablePromotionQuantity, nonPromotionalQuantity);
        }
        return createOnlyPromotionalOrder(product, actualBuyablePromotionQuantity);
    }


    private boolean askForNonPromotionalPurchase(String productName, int nonPromotionalQuantity) {
        String message = String.format(ASK_NON_PROMOTIONAL_PURCHASE_MESSAGE,
                productName, nonPromotionalQuantity);
        return inputView.askYesNo(message);
    }

    private OrderResult createOnlyPromotionalOrder(Product product, int buyablePromotionQuantity) {
        Promotion promotion = product.getType().getPromotion()
                .orElseThrow(() -> new IllegalStateException(NO_PROMOTION.getValue()));
        product.reduceStock(buyablePromotionQuantity);
        int freeQuantity = promotion.calculateFreeQuantity(buyablePromotionQuantity);
        OrderItem orderItem = new OrderItem(product.getName(), buyablePromotionQuantity);
        orderItem.setProductInfo(product);
        return new OrderResult(orderItem, freeQuantity, calculateTotalPrice(product, buyablePromotionQuantity),
                calculatePromotionDiscount(product, freeQuantity)
        );
    }


    private int calculateTotalPrice(Product product, int quantity) {
        return product.getPrice() * quantity;
    }

    private int calculatePromotionDiscount(Product product, int freeQuantity) {
        return product.getPrice() * freeQuantity;
    }


    private OrderResult createMixedOrder(List<Product> products, Product promotionalProduct,
                                         int promotionalQuantity, int nonPromotionalQuantity) {
        Product normalProduct = findNormalProduct(products);
        reduceStocks(promotionalProduct, normalProduct, promotionalQuantity, nonPromotionalQuantity);
        OrderItem orderItem = createMixedOrderItem(promotionalProduct, promotionalQuantity, nonPromotionalQuantity);
        return createMixedOrderResult(promotionalProduct, orderItem, promotionalQuantity);
    }

    private void reduceStocks(Product promotionalProduct, Product normalProduct, int promotionalQuantity,
                              int nonPromotionalQuantity) {
        promotionalProduct.reduceStock(promotionalQuantity);
        normalProduct.reduceStock(nonPromotionalQuantity);
    }

    private OrderItem createMixedOrderItem(Product product, int promotionalQuantity, int nonPromotionalQuantity) {
        int totalQuantity = promotionalQuantity + nonPromotionalQuantity;
        OrderItem orderItem = new OrderItem(product.getName(), totalQuantity);
        orderItem.setProductInfo(product);
        return orderItem;
    }

    private OrderResult createMixedOrderResult(Product promotionalProduct, OrderItem orderItem,
                                               int promotionalQuantity) {
        Promotion promotion = promotionalProduct.getType().getPromotion()
                .orElseThrow(() -> new IllegalStateException(NO_PROMOTION.getValue()));

        int freeQuantity = promotion.calculateFreeQuantity(promotionalQuantity);
        int totalPrice = calculateTotalPrice(promotionalProduct, orderItem.getQuantity());
        int promotionDiscount = calculatePromotionDiscount(promotionalProduct, freeQuantity);

        return new OrderResult(orderItem, freeQuantity, totalPrice, promotionDiscount);
    }

    private Product findNormalProduct(List<Product> products) {
        return products.stream()
                .filter(p -> !p.isPromotional())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(NO_NORMAL_PRODUCT.getValue()));
    }
}
