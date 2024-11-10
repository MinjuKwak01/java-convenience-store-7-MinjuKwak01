package store.view;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import store.promotion.Promotion;
import store.product.Product;
import store.product.ProductList;

public class OutputView {

    private static final String OUTPUT_GREETINGS = "안녕하세요. W편의점입니다.";
    private static final String OUTPUT_CURRENT_PRODUCTS = "현재 보유하고 있는 상품입니다.";
    private static final String OUTPUT_DASH = "- ";
    private static final String CURRENCY_UNIT = "원";
    private static final String OUT_OF_STOCK = "재고 없음";
    private static final String SPACE = " ";
    private static final String COUNT = "개";
    private static final DecimalFormat PRICE_FORMATTER = new DecimalFormat("###,###");

    private void printHeader() {
        System.out.println(OUTPUT_GREETINGS);
        System.out.println(OUTPUT_CURRENT_PRODUCTS);
        System.out.println();
    }

    public void printProducts(ProductList productList) {
        printHeader();
        Map<String, List<Product>> products = productList.getProducts();

        products.values().stream()
                .flatMap(List::stream)
                .forEach(this::printProduct);
    }

    private void printProduct(Product product) {
        String formattedProduct = createFormattedProduct(product);
        System.out.println(formattedProduct);
    }

    private String createFormattedProduct(Product product) {
        StringBuilder productInfo = createBasicProductInfo(product);
        appendStockInfo(productInfo, product);
        appendPromotionInfo(productInfo, product);
        return productInfo.toString();
    }

    private StringBuilder createBasicProductInfo(Product product) {
        return new StringBuilder(OUTPUT_DASH)
                .append(product.getName())
                .append(SPACE)
                .append(formatPrice(product.getPrice()))
                .append(CURRENCY_UNIT)
                .append(SPACE);
    }

    private void appendStockInfo(StringBuilder sb, Product product) {
        String stockInfo = getStockInfo(product);
        sb.append(stockInfo);
    }

    private String getStockInfo(Product product) {
        Map<Boolean, String> stockMessages = Map.of(
                true, OUT_OF_STOCK,
                false, product.getStock() + COUNT
        );
        return stockMessages.get(product.getStock() == 0);
    }

    private void appendPromotionInfo(StringBuilder sb, Product product) {
        getPromotionInfo(product).ifPresent(promotionName ->
                sb.append(SPACE).append(promotionName)
        );
    }

    private Optional<String> getPromotionInfo(Product product) {
        return product.isPromotional() ?
                product.getType().getPromotion().map(Promotion::getName) :
                Optional.empty();
    }

    private String formatPrice(int price) {
        return PRICE_FORMATTER.format(price);
    }
}
