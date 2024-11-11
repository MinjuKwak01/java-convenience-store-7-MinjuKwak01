package store.product;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import store.promotion.ProductType;
import store.promotion.Promotion;

class ProductListTest {
    private ProductList productList;
    private static final String EXISTING_PRODUCT = "콜라";
    private static final String NON_EXISTING_PRODUCT = "환타";
    private static final String OUT_OF_STOCK_PRODUCT = "물";

    @BeforeEach
    void setUp() {
        productList = new ProductList(createTestProducts());
    }

    private Map<String, List<Product>> createTestProducts() {
        Map<String, List<Product>> products = new HashMap<>();

        // 콜라: 재고 있는 상품 (일반 + 프로모션)
        List<Product> colaProducts = new ArrayList<>();
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);
        Promotion promotion = new Promotion("탄산2+1", 2, 1,
                startDate,endDate);
        colaProducts.add(new Product(EXISTING_PRODUCT, 1000, 10,
                ProductType.promotional(promotion)));
        colaProducts.add(new Product(EXISTING_PRODUCT, 1000, 5, ProductType.NORMAL));
        products.put(EXISTING_PRODUCT, colaProducts);

        // 물: 재고 없는 상품
        List<Product> waterProducts = new ArrayList<>();
        waterProducts.add(new Product(OUT_OF_STOCK_PRODUCT, 500, 0, ProductType.NORMAL));
        products.put(OUT_OF_STOCK_PRODUCT, waterProducts);

        return products;
    }

    @Test
    @DisplayName("존재하고 재고가 있는 상품 확인")
    void 존재하고_재고가_있는_상품_확인() {
        assertTrue(productList.hasProduct(EXISTING_PRODUCT));
    }

    @Test
    @DisplayName("존재하지 않는 상품 확인")
    void 존재하지_않는_상품_확인() {
        assertFalse(productList.hasProduct(NON_EXISTING_PRODUCT));
    }

    @Test
    @DisplayName("재고가 없는 상품 확인")
    void 재고가_없는_상품_확인() {
        assertFalse(productList.hasProduct(OUT_OF_STOCK_PRODUCT));
    }

    @Test
    @DisplayName("충분한 재고 확인")
    void 충분한_재고_확인() {
        assertTrue(productList.hasEnoughStock(EXISTING_PRODUCT, 10));  // 총 재고 15개
    }

    @Test
    @DisplayName("재고 부족 확인")
    void 재고_부족_확인() {
        assertFalse(productList.hasEnoughStock(EXISTING_PRODUCT, 20));  // 총 재고 15개
    }

    @Test
    @DisplayName("존재하지 않는 상품의 재고 확인")
    void 존재하지_않는_상품의_재고_확인() {
        assertFalse(productList.hasEnoughStock(NON_EXISTING_PRODUCT, 1));
    }

    @Test
    @DisplayName("존재하는 상품 조회")
    void 존재하는_상품_조회() {
        List<Product> products = productList.getProducts(EXISTING_PRODUCT);

        assertNotNull(products);
        assertEquals(2, products.size());
        assertEquals(EXISTING_PRODUCT, products.get(0).getName());
    }

    @Test
    @DisplayName("존재하지 않는 상품 조회시 예외 발생")
    void 존재하지_않는_상품_조회시_예외_발생() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productList.getProducts(NON_EXISTING_PRODUCT)
        );

        assertTrue(exception.getMessage().contains(NON_EXISTING_PRODUCT));
    }

    @Test
    @DisplayName("전체 상품 목록 조회")
    void 전체_상품_목록_조회() {
        Map<String, List<Product>> allProducts = productList.getProducts();

        assertNotNull(allProducts);
        assertEquals(2, allProducts.size());  // 콜라, 물
        assertTrue(allProducts.containsKey(EXISTING_PRODUCT));
        assertTrue(allProducts.containsKey(OUT_OF_STOCK_PRODUCT));
    }

    @Test
    @DisplayName("총 재고 계산 확인")
    void 총_재고_계산_확인() {
        // 콜라의 총 재고: 프로모션 10개 + 일반 5개 = 15개
        assertTrue(productList.hasEnoughStock(EXISTING_PRODUCT, 15));
        assertFalse(productList.hasEnoughStock(EXISTING_PRODUCT, 16));
    }

    @Test
    @DisplayName("재고 없는 상품의 총 재고 확인")
    void 재고_없는_상품의_총_재고_확인() {
        assertFalse(productList.hasEnoughStock(OUT_OF_STOCK_PRODUCT, 1));
    }
}