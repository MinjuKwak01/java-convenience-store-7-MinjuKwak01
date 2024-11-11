package store.promotion;

import static store.promotion.PromotionErrorMessage.NO_PROMOTION_EXISTS;

import java.util.Map;

public class PromotionList {
    private final Map<String, Promotion> promotions;

    public PromotionList(Map<String, Promotion> promotions) {
        this.promotions = promotions;
    }

    public Promotion findPromotionByName(String promotionName) {
        if (!promotions.containsKey(promotionName)) {
            throw new IllegalArgumentException(
                    String.format(NO_PROMOTION_EXISTS.getValue(), promotionName)
            );
        }
        return promotions.get(promotionName);
    }
}
