package store;

public class OrderResult {
    private final OrderItem orderItem;
    private final int freeQuantity;
    private final int totalPrice;
    private final int promotionDiscount;
    private final int membershipDiscount;

    public OrderResult(OrderItem orderItem, int freeQuantity, int totalPrice, int promotionDiscount) {
        this(orderItem, freeQuantity, totalPrice, promotionDiscount, 0);
    }

    public OrderResult(OrderItem orderItem, int freeQuantity, int totalPrice,
                       int promotionDiscount, int membershipDiscount) {
        this.orderItem = orderItem;
        this.freeQuantity = freeQuantity;
        this.totalPrice = totalPrice;
        this.promotionDiscount = promotionDiscount;
        this.membershipDiscount = membershipDiscount;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public int getFreeQuantity() {
        return freeQuantity;
    }

    public int getPromotionDiscount() {
        return promotionDiscount;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public int getMembershipDiscount() {
        return membershipDiscount;
    }
}
