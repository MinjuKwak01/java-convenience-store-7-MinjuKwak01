package store.controller;

import java.util.ArrayList;
import java.util.List;
import store.order.Order;
import store.order.OrderItem;
import store.order.OrderResult;
import store.order.OrderService;
import store.product.ProductList;
import store.view.InputView;
import store.view.OutputView;
import store.view.Receipt;

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
        processOrders();
    }

    private void processOrders() {
        displayProducts();
        List<OrderResult> results = processUserOrders();
        results = applyMembershipIfRequested(results);
        printReceipt(results);
        handleAdditionalPurchase();
    }

    private void displayProducts() {
        outputView.printProducts(productList);
    }

    private List<OrderResult> processUserOrders() {
        Order order = inputView.readOrder(productList);
        return processOrderItems(order.getOrderItems());
    }

    private List<OrderResult> processOrderItems(List<OrderItem> orderItems) {
        List<OrderResult> results = new ArrayList<>();
        for (OrderItem requestItem : orderItems) {
            OrderResult result = orderService.processOrder(
                    requestItem.getProductName(),
                    requestItem.getQuantity()
            );
            results.add(result);
        }
        return results;
    }

    private List<OrderResult> applyMembershipIfRequested(List<OrderResult> results) {
        if (inputView.askMembership()) {
            return orderService.applyMembershipDiscount(results);
        }
        return results;
    }

    private void printReceipt(List<OrderResult> results) {
        Receipt receipt = new Receipt();
        receipt.print(results);
    }

    private void handleAdditionalPurchase() {
        if (inputView.askAdditionalPurchase()) {
            processOrders();
        }
    }

}
