package store.file;

import static store.file.FileErrorMessage.PRODUCT_FILE_ERROR;
import static store.file.FileErrorMessage.PRODUCT_FILE_FORM_ERROR;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import store.product.Product;
import store.product.ProductList;
import store.promotion.Promotion;
import store.promotion.PromotionList;

public class ProductFileReader {
    private static final String FILE_PATH = "src/main/resources/products.md";
    private static final String DELIMITER = ",";
    private static final String NULL_VALUE = "null";
    private static final String NUMBER_REGEX = "[+-]?\\d*(\\.\\d+)?";
    private static final int ZERO_VALUE = 0;
    private static final int ONE_VALUE = 1;
    private static final int TWO_VALUE = 2;
    private static final int THREE_VALUE = 3;
    private static final int FOUR_VALUE = 4;


    public ProductList loadProducts(PromotionList promotionList) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line = reader.readLine();
            Map<String, List<Product>> productMap = new HashMap<>();
            readFile(line, reader, productMap, promotionList);
            addNormalProductIfOnlyPromotional(productMap);
            return new ProductList(productMap);
        } catch (IOException e) {
            throw new IllegalStateException(PRODUCT_FILE_ERROR.getValue());
        }
    }

    private void readFile(String line, BufferedReader reader, Map<String, List<Product>> productMap,
                          PromotionList promotionList) throws IOException {
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                continue;
            }
            processProductLine(line, productMap, promotionList);
        }
    }

    public void addNormalProductIfOnlyPromotional(Map<String, List<Product>> productMap) {
        for (Map.Entry<String, List<Product>> entry : productMap.entrySet()) {
            List<Product> products = entry.getValue();
            hasOnlyPromotionProduct(products);
        }
    }

    private void hasOnlyPromotionProduct(List<Product> products) {
        if (products.size() == 1 && products.getFirst().isPromotional()) {
            Product promotionalProduct = products.getFirst();
            Product normalProduct = Product.createNormalProduct(promotionalProduct.getName(),
                    promotionalProduct.getPrice(), ZERO_VALUE
            );
            products.add(normalProduct);
        }
    }

    private void processProductLine(String line, Map<String, List<Product>> productMap, PromotionList promotionList) {
        String[] values = line.split(DELIMITER);
        validateValues(values);
        String name = values[ZERO_VALUE];
        int price = Integer.parseInt(values[ONE_VALUE]);
        String quantityStr = values[TWO_VALUE];
        String promotionName = getPromotionName(values);

        Product product = createProduct(name, price, quantityStr, promotionName, promotionList);
        addToProductMap(name, product, productMap);
    }

    private String getPromotionName(String[] values) {
        if (values.length > THREE_VALUE) {
            return values[THREE_VALUE];
        }
        return null;
    }

    private Product createProduct(String name, int price, String quantityStr, String promotionName,
                                  PromotionList promotionList) {
        if (!checkIsNumber(quantityStr)) {
            return createProductWithoutQuantity(name, price, quantityStr, promotionList);
        }
        return createProductWithQuantity(name, price, quantityStr, promotionName, promotionList);
    }

    private Product createProductWithoutQuantity(String name, int price, String quantityStr,
                                                 PromotionList promotionList) {
        String promotionName = quantityStr;  // quantityStr 위치의 값을 promotionName으로 사용
        if (promotionName != null && !NULL_VALUE.equals(promotionName)) {
            Promotion promotion = promotionList.findPromotionByName(promotionName);
            return Product.createPromotionalProduct(name, price, ZERO_VALUE, promotion);
        }
        return Product.createNormalProduct(name, price, ZERO_VALUE);
    }

    private Product createProductWithQuantity(String name, int price, String quantityStr,
                                              String promotionName, PromotionList promotionList) {
        int quantity = Integer.parseInt(quantityStr);
        if (promotionName != null && !NULL_VALUE.equals(promotionName)) {
            Promotion promotion = promotionList.findPromotionByName(promotionName);
            return Product.createPromotionalProduct(name, price, quantity, promotion);
        }
        return Product.createNormalProduct(name, price, quantity);
    }

    private void addToProductMap(String name, Product product,
                                 Map<String, List<Product>> productMap) {
        productMap.computeIfAbsent(name, k -> new ArrayList<>()).add(product);
    }

    private boolean checkIsNumber(String quantityStr) {
        return quantityStr.matches(NUMBER_REGEX);
    }


    private void validateValues(String[] values) {
        if (values.length < THREE_VALUE || values.length > FOUR_VALUE) {
            throw new IllegalStateException(PRODUCT_FILE_FORM_ERROR.getValue());
        }
    }
}
