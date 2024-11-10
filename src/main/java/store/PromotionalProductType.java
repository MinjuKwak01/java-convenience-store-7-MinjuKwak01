package store;

import java.util.Optional;

public class PromotionalProductType extends ProductType {
    private final Promotion promotion;

    public PromotionalProductType(Promotion promotion) {
        this.promotion = promotion;
    }

    @Override
    public boolean isPromotional() {
        return true;
    }

    @Override
    public Optional<Promotion> getPromotion() {
        return Optional.ofNullable(promotion);
    }
}
