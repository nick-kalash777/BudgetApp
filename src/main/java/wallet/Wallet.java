package wallet;

import budget_app.BudgetApp;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

public class Wallet implements Serializable {
    private UUID uuid;
    private final ArrayList <ExpenseCategory> expenseCategories = new ArrayList<>();
    private final ArrayList <IncomeCategory> incomeCategories = new ArrayList<>();

    public Wallet() {
        this.uuid = UUID.randomUUID();
        BudgetApp.addWallet(this);
    }


    public double getExpensesTotal() {
        double expensesTotal = 0;
        for (ExpenseCategory category : expenseCategories) {
            expensesTotal += category.getValue();
        }
        return expensesTotal;
    }
    public double getIncomeTotal() {
        double incomeTotal = 0;
        for (IncomeCategory category : incomeCategories) {
            incomeTotal += category.getValue();
            return incomeTotal;
        }
        return incomeTotal;
    }
    public double getBudgetTotal() {
        double budgetTotal = 0;
        for (ExpenseCategory category : expenseCategories) {
            if (category.hasBudget()) {
                budgetTotal += category.getBudget() - category.getValue();
            }
        }
        return budgetTotal;
    }

    public ArrayList<ExpenseCategory> getExpenseCategories() {
        return expenseCategories;
    }

    public ArrayList<IncomeCategory> getIncomeCategories() {
        return incomeCategories;
    }

    public void addExpensesCategory(String category) {
        ExpenseCategory newCategory = new ExpenseCategory(category);
        expenseCategories.add(newCategory);

    }

    public void addIncomeCategory(String category) {
        IncomeCategory newCategory = new IncomeCategory(category);
        incomeCategories.add(newCategory);
    }

    public void removeExpensesCategory(int index) {
        expenseCategories.remove(index);
    }

    public void removeIncomeCategory(int index) {
        incomeCategories.remove(index);
    }

    public UUID getUUID() {
        return uuid;
    }

}
