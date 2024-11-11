package store.order;

public enum OrderErrorMessage {

    INVALID_QUANTITY("[ERROR] 상품의 수량은 1개 이상이어야 합니다."),
    NO_PROMOTION("[ERROR] 프로모션 정보가 없습니다."),
    NO_NORMAL_PRODUCT("[ERROR] 일반 상품 정보가 없습니다."),
    INVALID_FORMAT("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요."),
    WRONG_FORMAT("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요."),
    INVALID_PRODUCT("[ERROR] 존재하지 않는 상품입니다."),
    EXCEED_STOCK("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");

    private final String message;

    OrderErrorMessage(String message) {
        this.message = message;
    }

    public String getValue() {
        return message;
    }
}
