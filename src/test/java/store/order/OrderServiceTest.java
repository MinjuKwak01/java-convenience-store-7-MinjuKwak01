package store.order;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.product.Product;
import store.product.ProductList;
import store.view.InputView;

class OrderServiceTest {

    InputView inputView = new InputView();

    @Test
    @DisplayName("재고 차감 성공 테스트")
    void 재고_차감_성공() {
        // given
        Product product = Product.createNormalProduct("테스트상품", 1000, 10);
        Map<String, List<Product>> products = Map.of("테스트상품", List.of(product));
        OrderExceedPromotionService orderExceedPromotionService = new OrderExceedPromotionService(inputView);
        OrderService orderService = new OrderService(new ProductList(products), inputView, orderExceedPromotionService);

        // when
        OrderResult result = orderService.processOrder("테스트상품", 5);

        // then
        assertThat(product.getStock()).isEqualTo(5);
        assertThat(result.getOrderItem().getQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("재고 부족으로 주문 실패 테스트")
    void 재고_부족으로_주문_실패() {
        // given
        Product product = Product.createNormalProduct("테스트상품", 1000, 5);
        Map<String, List<Product>> products = Map.of("테스트상품", List.of(product));
        OrderExceedPromotionService orderExceedPromotionService = new OrderExceedPromotionService(inputView);
        OrderService orderService = new OrderService(new ProductList(products), inputView, orderExceedPromotionService);

        // when & then
        assertThatThrownBy(() -> orderService.processOrder("테스트상품", 10))
                .isInstanceOf(IllegalStateException.class);
    }
}