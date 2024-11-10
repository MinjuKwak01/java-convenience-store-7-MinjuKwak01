package store;


public class Application {

    public static void main(String[] args) {
        PromotionList promotionList = loadPromotions();
        ProductList productList = loadProducts(promotionList);

        InputView inputView = new InputView();
        OutputView outputView = new OutputView(productList);
        OrderService orderService = new OrderService(productList, inputView);

        Controller controller = new Controller(inputView, outputView, orderService, productList);
        controller.run();
    }

    private static ProductList loadProducts(PromotionList promotionList) {
        ProductFileReader productFileReader = new ProductFileReader();
        return productFileReader.loadProducts(promotionList);
    }

    private static PromotionList loadPromotions() {
        PromotionFileReader promotionFileReader = new PromotionFileReader();
        return promotionFileReader.loadPromotions();
    }

}
