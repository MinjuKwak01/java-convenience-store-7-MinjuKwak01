package store.product;

import java.util.List;
import java.util.Map;

public class ProductList {
    private final Map<String, List<Product>> products;

    public ProductList(Map<String, List<Product>> products) {
        this.products = products;
    }

    public Map<String, List<Product>> getProducts() {
        return products;
    }

    public boolean hasProduct(String productName) {
        // 상품이 존재하고 재고가 있는 버전이 하나라도 있는지 확인
        return products.containsKey(productName) &&
                products.get(productName).stream()
                        .anyMatch(product -> product.getStock() > 0);
    }

    public boolean hasEnoughStock(String productName, int quantity) {
        if (!products.containsKey(productName)) {
            return false;
        }
        int totalStock = products.get(productName).stream()
                .mapToInt(Product::getStock)
                .sum();
        return totalStock >= quantity;
    }

    // 상품 존재 여부 검증
    public void validateProductExists(String productName) {
        if (!products.containsKey(productName)) {
            throw new IllegalArgumentException(String.format("[ERROR] %s은(는) 존재하지 않는 상품입니다.", productName));
        }
    }

    // 상품명으로 상품 목록 조회
    public List<Product> getProducts(String productName) {
        validateProductExists(productName);
        return products.get(productName);
    }
}
