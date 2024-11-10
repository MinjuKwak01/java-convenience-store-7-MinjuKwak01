package store.promotion;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDateTime;

public class Promotion {
    private final String name;
    private final int requiredQuantity;
    private final int freeQuantity;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public Promotion(String name, int requiredQuantity, int freeQuantity, LocalDateTime startDate, LocalDateTime endDate) {
        this.name = name;
        this.requiredQuantity = requiredQuantity;
        this.freeQuantity = freeQuantity;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public int getRequiredQuantity() {
        return requiredQuantity;
    }

    public int getFreeQuantity() {
        return freeQuantity;
    }

    public int calculateFreeQuantity(int purchaseQuantity) {
        return (purchaseQuantity / (requiredQuantity + freeQuantity));
    }

    public int actualBuyableQuantity(int stock) {
        int cycleQuantity = requiredQuantity + freeQuantity;
        int maxCycles = stock / cycleQuantity;
        return cycleQuantity * maxCycles;
    }

    public boolean isValidPeriod(LocalDateTime now) {
        now = DateTimes.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }

}
