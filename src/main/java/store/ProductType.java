package store;

import java.util.Optional;

public abstract class ProductType {
    public static final ProductType NORMAL = new NormalProductType();

    public abstract boolean isPromotional();

    public abstract Optional<Promotion> getPromotion();

    public static ProductType promotional(Promotion promotion) {
        return new PromotionalProductType(promotion);
    }
}