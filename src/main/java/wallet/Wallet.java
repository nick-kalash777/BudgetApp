package wallet;

import budget_app.BudgetApp;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Wallet implements Serializable {
    private UUID uuid;

    private final ArrayList<ExpenseCategory> expenseCategories = new ArrayList<>();
    private final ArrayList<IncomeCategory> incomeCategories = new ArrayList<>();

    private final ExpenseCategory transferSendingCategory = new ExpenseCategory("Переводы");
    private final IncomeCategory transferReceivingCategory = new IncomeCategory("Переводы:");

    private final ArrayList<Operation> operations = new ArrayList<>();

    private final HashMap<WalletCategory, ArrayList<Operation>> operationsWithCategory = new HashMap<>();

    public Wallet() {
        this.uuid = UUID.randomUUID();
        BudgetApp.addWallet(this);
    }

    public ArrayList<Operation> getOperations() {
        return operations;
    }

    public ArrayList<Operation> getOperationsWithCategory(WalletCategory category) {
        return operationsWithCategory.get(category);
    }

    public ArrayList<Operation> getExpenseOperations() {
        ArrayList<Operation> expenseOperations = new ArrayList<>();
        for (WalletCategory category : operationsWithCategory.keySet()) {
            if (category instanceof ExpenseCategory) {
                expenseOperations.addAll(operationsWithCategory.get(category));
            }
        }
        return expenseOperations;
    }

    public ArrayList<Operation> getIncomeOperations() {
        ArrayList<Operation> incomeOperations = new ArrayList<>();
        for (WalletCategory category : operationsWithCategory.keySet()) {
            if (category instanceof IncomeCategory) {
                incomeOperations.addAll(operationsWithCategory.get(category));
            }
        }
        return incomeOperations;
    }

    public void addOperation(WalletCategory category, double amount) {
        Operation newOperation = new Operation(category, amount);
        operations.add(newOperation);
        if (operationsWithCategory.containsKey(category)) {
            operationsWithCategory.get(category).add(newOperation);
        } else {
            ArrayList<Operation> newOperations = new ArrayList<>();
            newOperations.add(newOperation);
            operationsWithCategory.put(category, newOperations);
        }
        if (category instanceof ExpenseCategory) {
            checkLimitNotification((ExpenseCategory) category, amount);
            if (getExpensesTotal() > getIncomeTotal()) {
                System.err.println("ВНИМАНИЕ: вы потратили больше, чем заработали!");
            }
        }
    }

    private void checkLimitNotification(ExpenseCategory expenseCategory, double amount) {
        if (expenseCategory.hasLimit()) {
            expenseCategory.decreaseRemainingLimit(amount);
            if (expenseCategory.getLimit() < 0)
                System.err.println("ВНИМАНИЕ: вы превысили свой бюджет!");
        }
    }

    public void removeOperation(Operation operation) {
        for (WalletCategory category : operationsWithCategory.keySet()) {
            if ((operation.getCategory() instanceof  ExpenseCategory && category instanceof ExpenseCategory)
                    || (operation.getCategory() instanceof  IncomeCategory && category instanceof IncomeCategory)) {
                operationsWithCategory.get(category).remove(operation);
            }
        }
        operations.remove(operation);
    }


    public double getExpensesTotal() {
        double expensesTotal = 0;
        for (Operation operation : operations) {
            if (operation.getCategory() instanceof ExpenseCategory) {
                expensesTotal += operation.getAmount();
            }
        }
        return expensesTotal;
    }

    public double getIncomeTotal() {
        double incomeTotal = 0;
        for (Operation operation : operations) {
            if (operation.getCategory() instanceof IncomeCategory) {
                incomeTotal += operation.getAmount();
            }
        }
        return incomeTotal;
    }

    public double getCategoryTotal(WalletCategory category) {
        double operationsTotal = 0;
        try {
            for (Operation operation : operationsWithCategory.get(category)) {
                operationsTotal += operation.getAmount();
            }
        } catch (NullPointerException _) {}
        return operationsTotal;
    }
//    public double getIncomeTotal() {
//        double incomeTotal = 0;
//        for (IncomeCategory category : incomeCategories) {
//            incomeTotal += category.getTotalValue();
//            return incomeTotal;
//        }
//        return incomeTotal;
//    }
//    public double getBudgetTotal() {
//        double budgetTotal = 0;
//        for (ExpenseCategory category : expenseCategories) {
//            if (category.hasLimit()) {
//                budgetTotal += category.getBudget() - category.getTotalValue();
//            }
//        }
//        return budgetTotal;
//    }

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

    public ExpenseCategory getTransferSendingCategory() {
        return transferSendingCategory;
    }
    public IncomeCategory getTransferReceivingCategory() {
        return transferReceivingCategory;
    }

    public void sendTransfer(double amount) {
        ArrayList<ExpenseCategory> expenseCategories = getExpenseCategories();
        if (!expenseCategories.contains(transferSendingCategory)) expenseCategories.add(transferSendingCategory);
        addOperation(transferSendingCategory, amount);
    }

    public void receieveTransfer(double amount) {
        ArrayList<IncomeCategory> incomeCategories = getIncomeCategories();
        if (!incomeCategories.contains(transferReceivingCategory)) incomeCategories.add(transferReceivingCategory);
        addOperation(transferReceivingCategory, amount);
    }

}
