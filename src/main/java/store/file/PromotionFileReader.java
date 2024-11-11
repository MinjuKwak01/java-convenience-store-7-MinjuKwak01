package store.file;

import static store.file.FileErrorMessage.PROMOTION_FILE_ERROR;
import static store.file.FileErrorMessage.PROMOTION_FILE_FORM_ERROR;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import store.promotion.Promotion;
import store.promotion.PromotionList;

public class PromotionFileReader {
    private static final String FILE_PATH = "src/main/resources/promotions.md";
    private static final String DELIMITER = ",";
    private static final int ZERO_VALUE = 0;
    private static final int ONE_VALUE = 1;
    private static final int TWO_VALUE = 2;
    private static final int THREE_VALUE = 3;
    private static final int FOUR_VALUE = 4;
    private static final int FIVE_VALUE = 5;

    public PromotionList loadPromotions() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line = reader.readLine();
            Map<String, Promotion> promotionMap = new HashMap<>();
            readFile(line, reader, promotionMap);
            return new PromotionList(promotionMap);
        } catch (IOException e) {
            throw new IllegalStateException(PROMOTION_FILE_ERROR.getValue(), e);
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
        String[] values = line.split(DELIMITER);
        validateValues(values);
        String name = values[ZERO_VALUE];
        int requiredQuantity = Integer.parseInt(values[ONE_VALUE]);
        int freeQuantity = Integer.parseInt(values[TWO_VALUE]);
        LocalDateTime startDate = parseDateTime(values[THREE_VALUE]);
        LocalDateTime endDate = parseDateTime(values[FOUR_VALUE]);

        Promotion promotion = new Promotion(name, requiredQuantity, freeQuantity, startDate, endDate);
        promotionMap.put(name, promotion);
    }

    private static LocalDateTime parseDateTime(String dateStr) {
        return LocalDate.parse(dateStr)
                .atStartOfDay();
    }


    private void validateValues(String[] values) {
        if (values.length != FIVE_VALUE) {
            throw new IllegalStateException(PROMOTION_FILE_FORM_ERROR.getValue());
        }
    }
}
