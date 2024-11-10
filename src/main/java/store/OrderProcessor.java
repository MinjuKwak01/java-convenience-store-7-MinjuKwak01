package store;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OrderProcessor {
    private final ProductList productList;

    public OrderProcessor(ProductList productList) {
        this.productList = productList;
    }

    public Order processOrder(String input) {
        validateOrderFormat(input);
        List<OrderItem> orderItems = parseOrderItems(input);
        validateOrderItems(orderItems);
        return new Order(orderItems);
    }

    private List<OrderItem> parseOrderItems(String input) {
        try {
            String[] items = input.split("\\],\\[");
            return Arrays.stream(items)
                    .map(item -> item.replaceAll("[\\[\\]]", ""))
                    .map(this::createOrderItem)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
        }
    }

    private void validateOrderFormat(String input) {
        if (!input.matches("\\[.*\\](,\\[.*\\])*")) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
        }
    }

    private OrderItem createOrderItem(String item) {
        String[] parts = item.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.");
        }
        String productName = parts[0];
        int quantity;
        try {
            quantity = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.");
        }
        return new OrderItem(productName, quantity);
    }

    private void validateOrderItems(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            validateProduct(item);
            validateStock(item);
        }
    }

    private void validateProduct(OrderItem item) {
        if (!productList.hasProduct(item.getProductName())) {
            throw new IllegalArgumentException("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
        }
    }

    private void validateStock(OrderItem item) {
        if (!productList.hasEnoughStock(item.getProductName(), item.getQuantity())) {
            throw new IllegalArgumentException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }
    }
}
