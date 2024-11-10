package store;

import java.util.ArrayList;
import java.util.List;

public class Controller {

    private final InputView inputView;
    private final OutputView outputView;
    private final OrderService orderService;
    private final ProductList productList;

    public Controller(InputView inputView, OutputView outputView, OrderService orderService, ProductList productList) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.orderService = orderService;
        this.productList = productList;
    }

    public void run() {
        try {
            processOrders(productList);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void processOrders(ProductList productList) {
        // 상품 목록 출력
        outputView.printProducts(productList);

        // 주문 입력 받기 (예: [콜라-1],[사이다-2])
        Order order = inputView.readOrder(productList);

        List<OrderItem> orderItems = order.getOrderItems();

        // 각 주문 처리
        List<OrderResult> results = new ArrayList<>();
        for (OrderItem requestItem : orderItems) {
            OrderResult result = orderService.processOrder(
                    requestItem.getProductName(),
                    requestItem.getQuantity()
            );
            results.add(result);
        }

        boolean useMembership = inputView.askMembership();
        if (useMembership) {
            results = orderService.applyMembershipDiscount(results);
        }

        Receipt receipt = new Receipt();
        receipt.print(results);

        // 추가 구매 여부 확인
        if (inputView.askAdditionalPurchase()) {
            processOrders(productList);  // 재귀적으로 추가 주문 처리
        }
    }


}
