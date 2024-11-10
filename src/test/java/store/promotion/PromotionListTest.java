package store.promotion;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

class PromotionListTest {
    private PromotionList promotionList;

    @BeforeEach
    void setUp() {
        Map<String, Promotion> promotions = createTestPromotions();
        promotionList = new PromotionList(promotions);
    }

    private Map<String, Promotion> createTestPromotions() {
        Map<String, Promotion> promotions = new HashMap<>();

        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);

        Promotion promotion = new Promotion(
                "탄산2+1",
                2,  // requiredQuantity
                1,  // freeQuantity
                startDate,
                endDate
        );

        promotions.put("탄산2+1", promotion);
        return promotions;
    }

    @Test
    @DisplayName("존재하는 프로모션 이름으로 프로모션을 찾을 수 있다")
    void 존재하는_프로모션_이름으로_프로모션을_찾을_수_있다() {
        // when
        Promotion foundPromotion = promotionList.findPromotionByName("탄산2+1");

        // then
        assertNotNull(foundPromotion);
        assertEquals("탄산2+1", foundPromotion.getName());
        assertEquals(2, foundPromotion.getRequiredQuantity());
        assertEquals(1, foundPromotion.getFreeQuantity());
    }

    @Test
    @DisplayName("존재하지 않는 프로모션 이름으로 조회시 예외가 발생한다")
    void 존재하지_않는_프로모션_이름으로_조회시_예외가_발생한다() {
        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> promotionList.findPromotionByName("존재하지않는프로모션")
        );

        assertEquals(
                String.format("[ERROR] %s 프로모션이 존재하지 않습니다.", "존재하지않는프로모션"),
                exception.getMessage()
        );
    }
}