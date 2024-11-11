package store.order;

import java.util.Optional;
import store.product.Product;
import store.promotion.Promotion;

public class OrderItem {
    private static final int MIN_QUANTITY = 1;

    private final String productName;
    private final int quantity;
    private Product selectedProduct;  // 선택된 상품 버전 (일반 또는 프로모션)

    public OrderItem(String productName, int quantity) {
        validateQuantity(quantity);
        this.productName = productName;
        this.quantity = quantity;
    }

    // 검증 메서드들
    private void validateQuantity(int quantity) {
        if (quantity < MIN_QUANTITY) {
            throw new IllegalArgumentException("[ERROR] 상품의 수량은 1개 이상이어야 합니다.");
        }
    }

    // 상품 정보 설정
    public void setProductInfo(Product product) {
        this.selectedProduct = product;
    }

    public Optional<Promotion> getPromotion() {
        return selectedProduct != null ? selectedProduct.getType().getPromotion() : Optional.empty();
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public Product getSelectedProduct() {
        return selectedProduct;
    }

    @Override
    public String toString() {
        return String.format("%s-%d", productName, quantity);
    }
}