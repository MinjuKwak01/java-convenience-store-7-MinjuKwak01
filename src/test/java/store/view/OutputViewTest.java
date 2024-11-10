package store.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import store.product.Product;
import store.product.ProductList;
import store.promotion.ProductType;
import store.promotion.Promotion;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OutputViewTest {
    private OutputView outputView;
    private ProductList productList;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        outputView = new OutputView();
        productList = new TestProductList(createTestProducts());
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    private Map<String, List<Product>> createTestProducts() {
        Map<String, List<Product>> products = new HashMap<>();

        // 프로모션 생성
        Promotion promotion = new Promotion(
                "탄산2+1",
                2,
                1,
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(1)
        );

        // 콜라 - 프로모션 상품과 일반 상품
        List<Product> colaProducts = new ArrayList<>();
        colaProducts.add(new Product("콜라", 1000, 10, ProductType.promotional(promotion)));
        colaProducts.add(new Product("콜라", 1000, 0, ProductType.NORMAL));
        products.put("콜라", colaProducts);

        // 물 - 일반 상품만
        List<Product> waterProducts = new ArrayList<>();
        waterProducts.add(new Product("물", 500, 5, ProductType.NORMAL));
        products.put("물", waterProducts);

        return products;
    }

    @Test
    @DisplayName("상품 목록 출력 테스트")
    void 상품_목록_출력_테스트() {
        // when
        outputView.printProducts(productList);
        String output = outputStreamCaptor.toString();

        // then
        assertTrue(output.contains("안녕하세요. W편의점입니다."));
        assertTrue(output.contains("현재 보유하고 있는 상품입니다."));
        assertTrue(output.contains("- 콜라 1,000원 10개 탄산2+1"));
        assertTrue(output.contains("- 콜라 1,000원 재고 없음"));
        assertTrue(output.contains("- 물 500원 5개"));
    }

    @Test
    @DisplayName("가격 포맷팅 테스트")
    void 가격_포맷팅_테스트() {
        // when
        outputView.printProducts(productList);
        String output = outputStreamCaptor.toString();

        // then
        assertTrue(output.contains("1,000원"));
        assertTrue(output.contains("500원"));
    }

    @Test
    @DisplayName("재고 없음 표시 테스트")
    void 재고_없음_표시_테스트() {
        // when
        outputView.printProducts(productList);
        String output = outputStreamCaptor.toString();

        // then
        assertTrue(output.contains("재고 없음"));
    }

    private static class TestProductList extends ProductList {
        public TestProductList(Map<String, List<Product>> products) {
            super(products);
        }
    }

    @Test
    @DisplayName("빈 상품 목록 테스트")
    void 빈_상품_목록_테스트() {
        // given
        ProductList emptyProductList = new TestProductList(new HashMap<>());

        // when
        outputView.printProducts(emptyProductList);
        String output = outputStreamCaptor.toString();

        // then
        assertTrue(output.contains("안녕하세요. W편의점입니다."));
        assertTrue(output.contains("현재 보유하고 있는 상품입니다."));
    }

}