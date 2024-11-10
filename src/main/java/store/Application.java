package store;


import store.controller.Controller;
import store.file.ProductFileReader;
import store.file.PromotionFileReader;
import store.order.OrderService;
import store.product.ProductList;
import store.promotion.PromotionList;
import store.view.InputView;
import store.view.OutputView;

public class Application {

    public static void main(String[] args) {
        PromotionList promotionList = loadPromotions();
        ProductList productList = loadProducts(promotionList);

        InputView inputView = new InputView();
        OutputView outputView = new OutputView();
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
