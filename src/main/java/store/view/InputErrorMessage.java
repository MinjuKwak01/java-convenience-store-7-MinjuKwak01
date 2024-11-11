package store.view;

public enum InputErrorMessage {

    INVALID_Y_OR_N("[ERROR] 입력은 Y 와 N 중 하나를 입력해야 합니다.");

    private final String message;

    InputErrorMessage(String message) {
        this.message = message;
    }

    public String getValue() {
        return message;
    }
}
