package store;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductFileReader {
    private static final String FILE_PATH = "src/main/resources/products.md";

    public ProductList loadProducts(PromotionList promotionList) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line = reader.readLine();
            Map<String, List<Product>> productMap = new HashMap<>();
            readFile(line, reader, productMap, promotionList);
            addNormalProductIfOnlyPromotional(productMap);
            return new ProductList(productMap);
        } catch (IOException e) {
            throw new IllegalStateException("[ERROR] 상품 파일을 읽는데 실패했습니다.", e);
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
                    promotionalProduct.getPrice(), 0
            );
            products.add(normalProduct);
        }
    }

    private void processProductLine(String line, Map<String, List<Product>> productMap, PromotionList promotionList) {
        String[] values = line.split(",");
        validateValues(values);

        String name = values[0];
        int price = Integer.parseInt(values[1]);
        String quantityStr = values[2];
        String promotionName = getPromotionName(values);

        Product product = createProduct(name, price, quantityStr, promotionName, promotionList);
        addToProductMap(name, product, productMap);
    }

    private String getPromotionName(String[] values) {
        if (values.length > 3) {
            return values[3];
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
        if (promotionName != null && !"null".equals(promotionName)) {
            Promotion promotion = promotionList.findPromotionByName(promotionName);
            return Product.createPromotionalProduct(name, price, 0, promotion);
        }
        return Product.createNormalProduct(name, price, 0);
    }

    private Product createProductWithQuantity(String name, int price, String quantityStr,
                                              String promotionName, PromotionList promotionList) {
        int quantity = Integer.parseInt(quantityStr);
        if (promotionName != null && !"null".equals(promotionName)) {
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
        return quantityStr.matches("[+-]?\\d*(\\.\\d+)?");
    }


    private void validateValues(String[] values) {
        if (values.length < 3 || values.length > 4) {
            throw new IllegalStateException("[ERROR] 상품 정보 형식이 올바르지 않습니다.");
        }
    }
}
