package store.order;

import static store.order.OrderErrorMessage.EXCEED_STOCK;
import static store.order.OrderErrorMessage.INVALID_FORMAT;
import static store.order.OrderErrorMessage.INVALID_PRODUCT;
import static store.order.OrderErrorMessage.WRONG_FORMAT;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import store.product.ProductList;

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
            throw new IllegalArgumentException(INVALID_FORMAT.getValue());
        }
    }

    private void validateOrderFormat(String input) {
        if (!input.matches("\\[.*\\](,\\[.*\\])*")) {
            throw new IllegalArgumentException(INVALID_FORMAT.getValue());
        }
    }

    private OrderItem createOrderItem(String item) {
        String[] parts = validateAndSplitOrderInput(item);
        String productName = parts[0];
        int quantity = parseQuantity(parts[1]);
        return new OrderItem(productName, quantity);
    }

    private String[] validateAndSplitOrderInput(String item) {
        String[] parts = item.split("-");
        validateOrderFormat(parts);
        return parts;
    }

    private void validateOrderFormat(String[] parts) {
        if (parts.length != 2) {
            throw new IllegalArgumentException(WRONG_FORMAT.getValue());
        }
    }

    private int parseQuantity(String quantityStr) {
        try {
            return Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(WRONG_FORMAT.getValue());
        }
    }

    private void validateOrderItems(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            validateProduct(item);
            validateStock(item);
        }
    }

    private void validateProduct(OrderItem item) {
        if (!productList.hasProduct(item.getProductName())) {
            throw new IllegalArgumentException(INVALID_PRODUCT.getValue());
        }
    }

    private void validateStock(OrderItem item) {
        if (!productList.hasEnoughStock(item.getProductName(), item.getQuantity())) {
            throw new IllegalArgumentException(EXCEED_STOCK.getValue());
        }
    }
}
