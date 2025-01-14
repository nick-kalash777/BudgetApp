package wallet;

public class ExpenseCategory extends WalletCategory {
    private double limit = 0;
    private double remainingLimit = 0;
    public ExpenseCategory(String name) {
        super(name);
    }

    public double getRemainingLimit() {
        return this.remainingLimit;
    }
    public double getLimit() { return this.remainingLimit; }

    public void setLimit(double value) {
        this.limit = value;
        this.remainingLimit = this.limit;
    }
    public boolean hasLimit() {
        return this.limit != 0;
    }
    public void decreaseRemainingLimit(double value) {
        this.remainingLimit -= value;
    }


}
