package store.promotion;

public enum PromotionErrorMessage {
    NO_PROMOTION_EXISTS("[ERROR] %s 프로모션이 존재하지 않습니다.");

    private final String message;

    PromotionErrorMessage(String message) {
        this.message = message;
    }

    public String getValue() {
        return message;
    }
}
