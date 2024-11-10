package store;

public class Promotion {
    private final String name;
    private final int requiredQuantity;
    private final int freeQuantity;

    public Promotion(String name, int requiredQuantity, int freeQuantity) {
        this.name = name;
        this.requiredQuantity = requiredQuantity;
        this.freeQuantity = freeQuantity;
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

}
