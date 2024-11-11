package store.order;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import store.product.Product;
import store.promotion.ProductType;
import store.promotion.Promotion;
import store.view.InputView;

class OrderExceedPromotionServiceTest {
    private OrderExceedPromotionService service;
    private TestInputView inputView;
    private List<Product> products;
    private Product promotionalProduct;
    private static final String PRODUCT_NAME = "콜라";

    @BeforeEach
    void setUp() {
        inputView = new TestInputView();
        service = new OrderExceedPromotionService(inputView);
        setupProducts();
    }

    private void setupProducts() {
        products = new ArrayList<>();
        Promotion promotion = new Promotion(
                "탄산2+1",
                2,  // 2개 구매시
                1,  // 1개 증정
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(1)
        );

        promotionalProduct = new Product(PRODUCT_NAME, 1000, 5,
                ProductType.promotional(promotion));
        Product normalProduct = new Product(PRODUCT_NAME, 1000, 10, ProductType.NORMAL);

        products.add(promotionalProduct);
        products.add(normalProduct);
    }

    @Test
    @DisplayName("프로모션 수량을 초과한 주문시 혼합 주문 생성")
    void 프로모션_수량을_초과한_주문시_혼합_주문_생성() {
        // given
        inputView.setNextAnswer(true);  // Y 응답 설정
        int quantity = 8;  // 프로모션 재고(5개) 초과
        int promotionalStock = 5;

        // when
        OrderResult result = service.handleInsufficientStock(
                products, promotionalProduct, PRODUCT_NAME, quantity, promotionalStock);

        // then
        assertEquals(8, result.getOrderItem().getQuantity());  // 총 주문 수량
        assertEquals(1, result.getFreeQuantity());
        assertTrue(result.getPromotionDiscount() > 0);  // 할인이 적용됨
    }

    @Test
    @DisplayName("프로모션 초과 수량 구매 거절시 프로모션 수량만 주문")
    void 프로모션_초과_수량_구매_거절시_프로모션_수량만_주문() {
        // given
        inputView.setNextAnswer(false);  // N 응답 설정
        int quantity = 8;
        int promotionalStock = 5;

        // when
        OrderResult result = service.handleInsufficientStock(
                products, promotionalProduct, PRODUCT_NAME, quantity, promotionalStock);

        // then
        assertEquals(3, result.getOrderItem().getQuantity());  // 프로모션 적용 가능한 수량만
        assertEquals(1, result.getFreeQuantity());  // 프로모션으로 인한 무료 수량
        assertEquals(1000, result.getPromotionDiscount());  // 1개 무료
    }

    @Test
    @DisplayName("가격 계산 검증")
    void 가격_계산_검증() {
        // given
        inputView.setNextAnswer(true);
        int quantity = 8;
        int promotionalStock = 5;

        // when
        OrderResult result = service.handleInsufficientStock(
                products, promotionalProduct, PRODUCT_NAME, quantity, promotionalStock);

        // then
        assertEquals(8000, result.getTotalPrice());  // 8개 * 1000원
        assertEquals(1000, result.getPromotionDiscount());  // 1개 무료
    }

    private static class TestInputView extends InputView {
        private boolean nextAnswer;

        public void setNextAnswer(boolean answer) {
            this.nextAnswer = answer;
        }

        @Override
        public boolean askYesNo(String message) {
            return nextAnswer;
        }
    }
}