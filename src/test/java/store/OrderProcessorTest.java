package store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import store.order.Order;
import store.order.OrderItem;
import store.order.OrderProcessor;
import store.product.Product;
import store.product.ProductList;
import store.promotion.ProductType;
import store.promotion.Promotion;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class OrderProcessorTest {
    private OrderProcessor orderProcessor;
    private ProductList productList;

    @BeforeEach
    void setUp() {
        productList = new TestProductList(createTestProducts());
        orderProcessor = new OrderProcessor(productList);
    }

    private Map<String, List<Product>> createTestProducts() {
        Map<String, List<Product>> products = new HashMap<>();

        // 콜라 상품 추가 (일반, 프로모션)
        List<Product> colaProducts = new ArrayList<>();
        colaProducts.add(new Product("콜라", 1000, 10, ProductType.NORMAL));
        colaProducts.add(new Product("콜라", 1000, 10,
                ProductType.promotional(new Promotion("2+1", 2, 1))));
        products.put("콜라", colaProducts);

        // 사이다 상품 추가
        List<Product> ciderProducts = new ArrayList<>();
        ciderProducts.add(new Product("사이다", 1000, 10, ProductType.NORMAL));
        products.put("사이다", ciderProducts);

        // 물 상품 추가
        List<Product> waterProducts = new ArrayList<>();
        waterProducts.add(new Product("물", 500, 10, ProductType.NORMAL));
        products.put("물", waterProducts);

        return products;
    }

    private static class TestProductList extends ProductList {
        public TestProductList(Map<String, List<Product>> products) {
            super(products);
        }
    }

    @Test
    @DisplayName("정상적인 주문을 처리한다.")
    void 정상적인_주문을_처리한다() {
        // given
        String input = "[콜라-2],[사이다-1]";

        // when
        Order order = orderProcessor.processOrder(input);

        // then
        List<OrderItem> items = order.getOrderItems();
        assertEquals(2, items.size());
        assertEquals("콜라", items.get(0).getProductName());
        assertEquals(2, items.get(0).getQuantity());
        assertEquals("사이다", items.get(1).getProductName());
        assertEquals(1, items.get(1).getQuantity());
    }


    @ParameterizedTest
    @DisplayName("잘못된 형식의 입력에 대해 예외를 발생시킨다")
    @ValueSource(strings = {
            "콜라-2",                  // 대괄호 없음
            "[콜라-2],사이다-1]",      // 일부 대괄호 누락
            "[콜라:2]",               // 잘못된 구분자
            "[]",                     // 빈 입력
            "[콜라-]",                // 수량 누락
            "[콜라-a]"                // 잘못된 수량
    })
    void validateInvalidOrderFormat(String input) {
        assertThatThrownBy(() -> orderProcessor.processOrder(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("[ERROR]");
    }

    @Test
    @DisplayName("존재하지 않는 상품 주문시 예외를 발생시킨다.")
    void 존재하지_않는_상품_주문시_예외를_발생시킨다() {
        // given
        String input = "[환타-1]";

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderProcessor.processOrder(input)
        );
        assertEquals("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.", exception.getMessage());
    }

    @Test
    @DisplayName("재고가 부족한 경우 예외를 발생시킨다.")
    void 재고가_부족한_경우_예외를_발생시킨다() {
        // given
        String input = "[콜라-100]";

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderProcessor.processOrder(input)
        );
        assertEquals("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.", exception.getMessage());
    }

    @Test
    @DisplayName("수량이 0이하인 경우 예외를 발생시킨다.")
    void 수량이_0이하인_경우_예외를_발생시킨다() {
        // given
        String input = "[콜라-0]";

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderProcessor.processOrder(input)
        );
        assertTrue(exception.getMessage().startsWith("[ERROR]"));
    }
}