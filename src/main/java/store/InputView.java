package store;

import camp.nextstep.edu.missionutils.Console;


public class InputView {

    public Order readOrder(ProductList productList) {
        while (true) {
            try{
                System.out.println();
                System.out.println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
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
                return "Y".equals(input);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    public boolean askMembership() {
        while (true) {
            try {
                System.out.println("멤버십 할인을 받으시겠습니까? (Y/N)");
                String input = Console.readLine().toUpperCase();
                validateYesNoInput(input);
                return "Y".equals(input);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public boolean askAdditionalPurchase() {
        while (true) {
            try {
                System.out.println("감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)");
                String input = Console.readLine().toUpperCase();
                validateYesNoInput(input);
                return "Y".equals(input);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    private void validateYesNoInput(String input) {
        if (!input.equals("Y") && !input.equals("N")) {
            throw new IllegalArgumentException("[ERROR] 입력은 Y 와 N 중 하나를 입력해야 합니다.");
        }
    }

}
