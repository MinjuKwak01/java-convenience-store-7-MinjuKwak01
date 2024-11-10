package store;

import java.util.Map;

public class PromotionList {
    private final Map<String, Promotion> promotions;

    public PromotionList(Map<String, Promotion> promotions) {
        this.promotions = promotions;
    }

    public Promotion findPromotionByName(String promotionName) {
        if (!promotions.containsKey(promotionName)) {
            throw new IllegalArgumentException(
                    String.format("[ERROR] %s 프로모션이 존재하지 않습니다.", promotionName)
            );
        }
        return promotions.get(promotionName);
    }
}
