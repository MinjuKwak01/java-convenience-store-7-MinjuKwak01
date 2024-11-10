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

}
