package wallet;

import java.io.Serializable;

public class Operation implements Serializable {
    private double amount;
    private WalletCategory category;

    public Operation(WalletCategory category, double amount) {
        this.amount = amount;
        this.category = category;
    }

    @Override
    public String toString() {
        return category.getName() + ": " + amount;
    }

    public WalletCategory getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }
}
