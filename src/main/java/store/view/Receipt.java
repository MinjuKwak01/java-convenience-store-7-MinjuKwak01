package store.view;

import java.util.List;
import store.order.OrderItem;
import store.order.OrderResult;

public class Receipt {
    private static final String HEADER_DELIMITER = "==============W 편의점================";
    private static final String PROMOTION_DELIMITER = "=============증\t정===============";
    private static final String FOOTER_DELIMITER = "====================================";

    private static final String HEADER_COLUMNS = "상품명\t\t수량\t금액";
    private static final String ORDER_ITEM_FORMAT = "%s\t\t%d\t%,d%n";
    private static final String PROMOTIONAL_ITEM_FORMAT = "%s\t\t%d%n";

    private static final String TOTAL_PURCHASE_FORMAT = "총구매액\t\t%d\t%,d%n";
    private static final String PROMOTION_DISCOUNT_FORMAT = "행사할인\t\t\t-%,d%n";
    private static final String MEMBERSHIP_DISCOUNT_FORMAT = "멤버십할인\t\t\t-%,d%n";
    private static final String FINAL_PRICE_FORMAT = "내실돈\t\t\t %,d%n";

    public void print(List<OrderResult> orderResults) {
        System.out.println(HEADER_DELIMITER);
        printOrderDetails(orderResults);
        if (hasAnyPromotionalItems(orderResults)) {
            printPromotionalItems(orderResults);
        }
        printPriceDetails(orderResults);
    }

    private void printOrderDetails(List<OrderResult> orderResults) {
        System.out.println(HEADER_COLUMNS);
        for (OrderResult result : orderResults) {
            OrderItem item = result.getOrderItem();
            System.out.printf(ORDER_ITEM_FORMAT,
                    item.getProductName(),
                    item.getQuantity(),
                    result.getTotalPrice());
        }
    }

    private boolean hasAnyPromotionalItems(List<OrderResult> orderResults) {
        return orderResults.stream()
                .anyMatch(result -> result.getFreeQuantity() > 0);
    }

    private void printPromotionalItems(List<OrderResult> orderResults) {
        System.out.println(PROMOTION_DELIMITER);
        orderResults.stream()
                .filter(result -> result.getFreeQuantity() > 0)
                .forEach(result -> System.out.printf(PROMOTIONAL_ITEM_FORMAT,
                        result.getOrderItem().getProductName(),
                        result.getFreeQuantity()));
    }

    private void printPriceDetails(List<OrderResult> orderResults) {
        System.out.println(FOOTER_DELIMITER);
        int totalQuantity = calculateTotalQuantity(orderResults);
        int totalPrice = calculateTotalPrice(orderResults);
        int totalPromotionDiscount = calculateTotalPromotionDiscount(orderResults);
        int totalMembershipDiscount = calculateTotalMembershipDiscount(orderResults);
        int finalPrice = totalPrice - totalPromotionDiscount - totalMembershipDiscount;
        System.out.printf(TOTAL_PURCHASE_FORMAT, totalQuantity, totalPrice);
        System.out.printf(PROMOTION_DISCOUNT_FORMAT, totalPromotionDiscount);
        System.out.printf(MEMBERSHIP_DISCOUNT_FORMAT, totalMembershipDiscount);
        System.out.printf(FINAL_PRICE_FORMAT, finalPrice);
    }

    private int calculateTotalQuantity(List<OrderResult> orderResults) {
        return orderResults.stream()
                .mapToInt(result -> result.getOrderItem().getQuantity())
                .sum();
    }

    private int calculateTotalPrice(List<OrderResult> orderResults) {
        return orderResults.stream()
                .mapToInt(OrderResult::getTotalPrice)
                .sum();
    }

    private int calculateTotalPromotionDiscount(List<OrderResult> orderResults) {
        return orderResults.stream()
                .mapToInt(OrderResult::getPromotionDiscount)
                .sum();
    }

    private int calculateTotalMembershipDiscount(List<OrderResult> orderResults) {
        return orderResults.stream()
                .mapToInt(OrderResult::getMembershipDiscount)
                .sum();
    }


}