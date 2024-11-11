package store.file;

public enum FileErrorMessage {
    FILE_NOT_FOUND("[ERROR] 파일을 찾을 수 없습니다."),
    FILE_READ_ERROR("[ERROR] 파일을 읽는 중 오류가 발생했습니다."),
    PROMOTION_FILE_ERROR("[ERROR] 프로모션 파일을 읽는데 실패했습니다."),
    PROMOTION_FILE_FORM_ERROR("[ERROR] 프로모션 정보 형식이 올바르지 않습니다."),
    PRODUCT_FILE_ERROR("[ERROR] 상품 파일을 읽는데 실패했습니다."),
    PRODUCT_FILE_FORM_ERROR("[ERROR] 상품 정보 형식이 올바르지 않습니다.");

    private final String message;

    FileErrorMessage(String message) {
        this.message = message;
    }

    public String getValue() {
        return message;
    }
}
