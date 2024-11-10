package store;

import java.util.List;
import java.util.stream.Collectors;

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
                .map(product -> processPromotionalProduct(product, productName, quantity))
                .orElseGet(() -> createNormalOrder(products.getFirst(), quantity)); //일반 상품 주문
    }

    //프로모션중인 상품을 1개 덜 가져온 경우
    private OrderResult processPromotionalProduct(Product product, String productName, int quantity) {
        int promotionalStock = product.getStock();

        if (product.getType().getPromotion().get().actualBuyableQuantity(promotionalStock) < quantity) {
            return handleInsufficientStock(product, productName, quantity, promotionalStock);
        }
        if (product.canGetMoreFromPromotion(quantity) && askForMoreItems(product)) {
            // +1해서 주문 진행
            return createOrderWithPromotion(product, quantity);
        }
        return processPromotionalStockButNoFreeProduct(product, quantity);
    }

    //초과된 프로모션 재고 입력했을때
    private OrderResult handleInsufficientStock(Product product, String productName, int quantity,
                                                int promotionalStock) {
        int nonPromotionalQuantity =
                quantity - product.getType().getPromotion().get().actualBuyableQuantity(promotionalStock);
        if (askForNonPromotionalPurchase(productName, nonPromotionalQuantity)) {
            return createMixedOrder(product,
                    product.getType().getPromotion().get().actualBuyableQuantity(promotionalStock),
                    nonPromotionalQuantity);
        }
        return createOrderWithPromotion(product, promotionalStock);
    }
}