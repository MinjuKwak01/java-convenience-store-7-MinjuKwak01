package store.product;

import java.util.Optional;
import store.promotion.ProductType;
import store.promotion.Promotion;

public class Product {
    private final String name;
    private final int price;
    private int stock;
    private final ProductType type;  // 상품 타입 추가

    public Product(String name, int price, int stock, ProductType type) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.type = type;
    }

    // 일반 상품 생성
    public static Product createNormalProduct(String name, int price, int stock) {
        return new Product(name, price, stock, ProductType.NORMAL);
    }

    // 프로모션 상품 생성
    public static Product createPromotionalProduct(String name, int price, int stock, Promotion promotion) {
        return new Product(name, price, stock, ProductType.promotional(promotion));
    }

    public boolean isPromotional() {
        return type.isPromotional();
    }

    public ProductType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void reduceStock(int quantity) {
        if (stock < quantity) {
            throw new IllegalStateException(String.format("[ERROR] %s의 재고가 부족합니다.", name));
        }
        this.stock -= quantity;
    }

    public boolean canGetMoreFromPromotion(int orderedQuantity) {
        if (stock == 0) {
            return false;
        }
        Optional<Promotion> promotion = getType().getPromotion();
        return promotion.map(p -> {
            int requiredQuantity = p.getRequiredQuantity();
            int sum = requiredQuantity + p.getFreeQuantity();
            return orderedQuantity % sum == requiredQuantity &&
                    stock >= requiredQuantity && stock >= orderedQuantity;
        }).orElse(false);
    }
}
