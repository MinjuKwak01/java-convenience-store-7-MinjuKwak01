package store.view;

import static store.view.InputErrorMessage.INVALID_Y_OR_N;

import camp.nextstep.edu.missionutils.Console;
import store.order.Order;
import store.order.OrderProcessor;
import store.product.ProductList;


public class InputView {

    private static final String INPUT_PRODUCT_NAME_QUANTITY = "\n구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])";
    private static final String INPUT_MEMBERSHIP = "멤버십 할인을 받으시겠습니까? (Y/N)";
    private static final String INPUT_ADDITIONAL_PURCHASE = "감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)";
    private static final String YES_INPUT = "Y";
    private static final String NO_INPUT = "N";

    public Order readOrder(ProductList productList) {
        while (true) {
            try{
                System.out.println(INPUT_PRODUCT_NAME_QUANTITY);
                String input = Console.readLine();
                OrderProcessor orderProcessor = new OrderProcessor(productList);
                return orderProcessor.processOrder(input);
            }catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public boolean askYesNo(String message) {
        while (true) {
            try {
                System.out.println(message);
                String input = Console.readLine().toUpperCase();
                validateYesNoInput(input);
                return YES_INPUT.equals(input);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public boolean askMembership() {
        while (true) {
            try {
                System.out.println(INPUT_MEMBERSHIP);
                String input = Console.readLine().toUpperCase();
                validateYesNoInput(input);
                return YES_INPUT.equals(input);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public boolean askAdditionalPurchase() {
        while (true) {
            try {
                System.out.println(INPUT_ADDITIONAL_PURCHASE);
                String input = Console.readLine().toUpperCase();
                validateYesNoInput(input);
                return YES_INPUT.equals(input);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void validateYesNoInput(String input) {
        if (!input.equals(YES_INPUT) && !input.equals(NO_INPUT)) {
            throw new IllegalArgumentException(INVALID_Y_OR_N.getValue());
        }
    }

}
