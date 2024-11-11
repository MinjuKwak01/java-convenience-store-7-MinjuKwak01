package store.product;

public enum ProductErrorMessage {
    NO_PRODUCT_EXISTS("[ERROR] %s은(는) 존재하지 않는 상품입니다.");

    private final String message;

    ProductErrorMessage(String message) {
        this.message = message;
    }

    public String getValue() {
        return message;
    }
}
