package store.order;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private final List<OrderItem> orderItems;

    public Order(List<OrderItem> orderItems) {
        this.orderItems = new ArrayList<>(orderItems);
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
}