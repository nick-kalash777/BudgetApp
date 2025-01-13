package wallet;

public class ExpenseCategory extends WalletCategory {
    private double budget = 0;
    public ExpenseCategory(String name) {
        super(name);
    }

    public double getBudget() {
        return this.budget;
    }

    public void setBudget(double value) {
        this.budget = value;
    }
    public boolean hasBudget() {
        return this.budget != 0;
    }


}
