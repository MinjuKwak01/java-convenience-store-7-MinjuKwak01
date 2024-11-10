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
        StringBuilder productInfo = new StringBuilder(OUTPUT_DASH)
                .append(product.getName())
                .append(SPACE)
                .append(formatPrice(product.getPrice()))
                .append(CURRENCY_UNIT)
                .append(SPACE);

        // 재고 상태 출력
        if (product.isPromotional()) {
            appendPromotionalStock(productInfo, product);
        } else {
            appendNormalStock(productInfo, product);
        }

        System.out.println(productInfo);
    }

    private String formatPrice(int price) {
        return PRICE_FORMATTER.format(price);
    }

    private void appendPromotionalStock(StringBuilder sb, Product product) {
        if (product.getStock() == 0) {
            sb.append(OUT_OF_STOCK);
        } else {
            sb.append(product.getStock()).append("개");
        }

        Optional<Promotion> promotion = product.getType().getPromotion();
        sb.append(SPACE).append(promotion.get().getName());
    }

    private void appendNormalStock(StringBuilder sb, Product product) {
        if (product.getStock() == 0) {
            sb.append(OUT_OF_STOCK);
        } else {
            sb.append(product.getStock()).append("개");
        }
    }
}
