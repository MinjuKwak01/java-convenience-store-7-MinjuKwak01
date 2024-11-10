package store.view;

import java.util.List;
import store.order.OrderItem;
import store.order.OrderResult;

public class Receipt {
    public void print(List<OrderResult> orderResults) {
        System.out.println("==============W 편의점================");
        printOrderDetails(orderResults);

        // 증정 상품이 하나라도 있는 경우 증정 섹션 출력
        if (hasAnyPromotionalItems(orderResults)) {
            printPromotionalItems(orderResults);
        }

        printPriceDetails(orderResults);
    }

    private void printOrderDetails(List<OrderResult> orderResults) {
        System.out.println("상품명\t\t수량\t금액");
        for (OrderResult result : orderResults) {
            OrderItem item = result.getOrderItem();
            System.out.printf("%s\t\t%d\t%,d\n",
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
        System.out.println("=============증\t정===============");
        orderResults.stream()
                .filter(result -> result.getFreeQuantity() > 0)
                .forEach(result -> System.out.printf("%s\t\t%d\n",
                        result.getOrderItem().getProductName(),
                        result.getFreeQuantity()));
    }

    private void printPriceDetails(List<OrderResult> orderResults) {
        System.out.println("====================================");

        // 총 구매 수량과 금액 계산
        int totalQuantity = calculateTotalQuantity(orderResults);
        int totalPrice = calculateTotalPrice(orderResults);
        int totalPromotionDiscount = calculateTotalPromotionDiscount(orderResults);
        int totalMembershipDiscount = calculateTotalMembershipDiscount(orderResults);
        int finalPrice = totalPrice - totalPromotionDiscount - totalMembershipDiscount;

        System.out.printf("총구매액\t\t%d\t%,d\n", totalQuantity, totalPrice);
        System.out.printf("행사할인\t\t\t-%,d\n", totalPromotionDiscount);
        System.out.printf("멤버십할인\t\t\t-%,d\n", totalMembershipDiscount);
        System.out.printf("내실돈\t\t\t %,d\n", finalPrice);
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