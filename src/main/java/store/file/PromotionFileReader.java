package store.file;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import store.promotion.Promotion;
import store.promotion.PromotionList;

public class PromotionFileReader {
    private static final String FILE_PATH = "src/main/resources/promotions.md";

    public PromotionList loadPromotions() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line = reader.readLine();
            Map<String, Promotion> promotionMap = new HashMap<>();
            readFile(line, reader, promotionMap);
            return new PromotionList(promotionMap);
        } catch (IOException e) {
            throw new IllegalStateException("[ERROR] 프로모션 파일을 읽는데 실패했습니다.", e);
        }
    }

    private void readFile(String line, BufferedReader reader, Map<String, Promotion> promotionMap) throws IOException {
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                continue;
            }
            processPromotionLine(line, promotionMap);
        }
    }

    private void processPromotionLine(String line, Map<String, Promotion> promotionMap) {
        String[] values = line.split(",");
        validateValues(values);
        String name = values[0];
        int requiredQuantity = Integer.parseInt(values[1]);
        int freeQuantity = Integer.parseInt(values[2]);

        Promotion promotion = new Promotion(name, requiredQuantity, freeQuantity);
        promotionMap.put(name, promotion);
    }

    private void validateValues(String[] values) {
        if (values.length != 5) {
            throw new IllegalStateException("[ERROR] 프로모션 정보 형식이 올바르지 않습니다.");
        }
    }
}
