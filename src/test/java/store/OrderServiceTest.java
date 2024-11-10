package store;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class OrderServiceTest {

    InputView inputView = new InputView();
    @Test
    void 재고_차감_성공() {
        // given
        Product product = Product.createNormalProduct("테스트상품", 1000, 10);
        Map<String, List<Product>> products = Map.of("테스트상품", List.of(product));
        OrderService orderService = new OrderService(new ProductList(products), inputView);

        // when
        OrderResult result = orderService.processOrder("테스트상품", 5);

        // then
        assertThat(product.getStock()).isEqualTo(5);
        assertThat(result.getOrderItem().getQuantity()).isEqualTo(5);
    }

    @Test
    void 재고_부족으로_주문_실패() {
        // given
        Product product = Product.createNormalProduct("테스트상품", 1000, 5);
        Map<String, List<Product>> products = Map.of("테스트상품", List.of(product));
        OrderService orderService = new OrderService(new ProductList(products), inputView);

        // when & then
        assertThatThrownBy(() -> orderService.processOrder("테스트상품", 10))
                .isInstanceOf(IllegalStateException.class);
    }
}