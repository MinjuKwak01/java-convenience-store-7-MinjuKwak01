package store.promotion;

import java.util.Optional;

public class NormalProductType extends ProductType {
    @Override
    public boolean isPromotional() {
        return false;
    }

    @Override
    public Optional<Promotion> getPromotion() {
        return Optional.empty();
    }
}
