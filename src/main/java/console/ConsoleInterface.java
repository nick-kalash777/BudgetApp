package console;

import budget_app.*;
import database.DataSaver;
import user.User;
import wallet.ExpenseCategory;
import wallet.IncomeCategory;
import wallet.Wallet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ConsoleInterface {
    private HashMap<String, User> users = BudgetApp.getUsers();
    private Wallet currentWallet;
    private User currentUser;
    private final ConsoleValidator consoleValidator = new ConsoleValidator();
    ArrayList<ExpenseCategory> expenses;
    ArrayList<IncomeCategory> income;

    private void setCurrentWallet(Wallet currentWallet) {
        this.currentWallet = currentWallet;
        this.expenses = currentWallet.getExpenseCategories();
        this.income = currentWallet.getIncomeCategories();
    }

    private void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void authorizeMenu() {
        while (true) {
            System.out.println("1. Авторизироваться.");
            System.out.println("2. Зарегистрироваться.");
            System.out.println("0. Завершить работу.");

            int choice = consoleValidator.getInt();
            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    register();
                    break;
                case 0:
                    System.exit(0);
                default: System.out.println("Invalid input.");
            }
        }
    }

    public void register() {
        String username = null;
        do {
            if (username != null) {
                System.err.println("Этот логин уже используйте. Выберите другой.");
            }
            System.out.println("Введите желаемое имя: ");
            username = consoleValidator.getUsername();
            if (username.equals("0")){
                System.out.println("возвращаемся в главное меню...");
                return;
            }
        } while (users.containsKey(username));
        System.out.println("Введите желаемый пароль: ");
        String password = consoleValidator.getString();
        if (password.equals("0")) {
            System.out.println("возвращаемся в главное меню...");
            return;
        }
        registerUser(username, password);
        System.out.println("Аккаунт был зарегистрирован.");
    }

    private void registerUser(String username, String password) {
        User newUser = new User(username, password);
        Wallet newWallet = new Wallet();
        newUser.addWallet(newWallet);
        DataSaver.saveUser(newUser);
        DataSaver.saveWallet(newWallet);
    }

    public void login() {
        User user;
        System.out.println("Введите логин:");
        String username = consoleValidator.getUsername();
        while (!users.containsKey(username)) {
            System.err.println("Такой пользователь не существует.");
            System.err.println("Введите '0' чтобы вернуться назад или введите другой логин.");
            username = consoleValidator.getUsername();
            if (username.equals("0")) {
                return;
            }
        }
        user = users.get(username);
        System.out.println("Введите ваш пароль:");
        String password = consoleValidator.getString();
        while (!user.login(password)) {
            System.err.println("Неправильный пароль. Введите '0' чтобы вернуться назад или введите другой пароль.");
            password = consoleValidator.getString();
            if (password.equals("0")) {
                return;
            }
        }

        System.out.println("Авторизация успешна.");
        System.out.println("*************************");
        System.out.println("С каким кошельком вы хотите работать?");
        //можно создать только один, но функционал нескольких кошельков существует
        ArrayList<UUID> walletUUIDs = user.getWallets();
        for (int i = 0; i < walletUUIDs.size(); i++) {
            int id = i + 1;
            System.out.println(id + ". " + walletUUIDs.get(i));
        }
        int walletIndex = consoleValidator.getInt()-1;
        UUID walletUUID = walletUUIDs.get(walletIndex);
        setCurrentWallet(BudgetApp.getWallets().get(walletUUIDs.get(walletIndex)));
        setCurrentUser(user);
        manageWallet();
    }

    public void saveUserAndWallet() {
        DataSaver.saveUser(currentUser);
        DataSaver.saveWallet(currentWallet);

    }

    public void manageWallet() {
        while (true) {
            System.out.println();
            System.out.println("*************************");
            System.out.println("УПРАВЛЕНИЕ КОШЕЛЬКОМ");
            System.out.println("*************************");
            System.out.println("РАСХОДЫ " + currentWallet.getExpensesTotal()
                    + " | ДОХОДЫ " + currentWallet.getIncomeTotal()
                    + " | ЛИМИТЫ " + currentWallet.getBudgetTotal());

            System.out.println("1. Расходы.");
            System.out.println("2. Доходы.");
            System.out.println("3. Лимиты.");
            System.out.println("4. Управление категориями.");
            System.out.println("0. Сменить пользователя.");


            int choice = consoleValidator.getInt();
            switch (choice) {
                case 1:
                    manageExpenses();
                    break;
                case 2:
                    manageIncome();
                    break;
                case 3:
                    manageBudgets();
                    break;
                case 4:
                    manageCategories();
                    break;
                case 0:
                    saveUserAndWallet();
                    return;
                default:
                    System.err.println("Неправильный ввод. Попробуйте еще раз.");
            }
        }
    }

    public void manageExpenses() {
        while (true) {
            System.out.println("*************************");
            System.out.println("РАСХОДЫ");
            System.out.println("*************************");
            if (expenses.isEmpty()) {
                System.out.println("Вы еще не создали категории расходов.");
                consoleValidator.pause();
                return;
            } else {
                for (int i = 0; i < expenses.size(); i++) {
                    int id = i + 1;
                    System.out.println(id + ". " + expenses.get(i).getName() + ": " + expenses.get(i).getValue());
                }
            }
            System.out.println("ОБЩИЕ РАСХОДЫ: " + currentWallet.getExpensesTotal());
            System.out.println("*************************");
            System.out.println("1. Добавить расход.");
            System.out.println("2. Уменьшить расход.");
            System.out.println("3. Просмотреть только определенные категории расходов.");
            System.out.println("0. Вернуться назад.");

            int choice = consoleValidator.getInt();
            switch (choice) {
                case 1: {
                    System.out.println("Какую категорию расходов вы хотите изменить?");
                    int categoryId = consoleValidator.getInt() - 1;
                    System.out.println("На сколько?");
                    double amount = consoleValidator.getDouble();
                    ExpenseCategory expense = expenses.get(categoryId);
                    expense.addValue(amount);
                    if (expense.hasBudget() && (expense.getBudget() - expense.getValue() < 0)) {
                        System.err.println("ВНИМАНИЕ: вы превысили свой бюджет!");
                    }
                    if (currentWallet.getExpensesTotal() > currentWallet.getIncomeTotal()) {
                        System.err.println("ВНИМАНИЕ: вы потратили больше, чем заработали!");
                    }
                    break;
                }
                case 2: {
                    System.out.println("Какую категорию расходов вы хотите изменить?");
                    int categoryId = consoleValidator.getInt() - 1;
                    System.out.println("На сколько?");
                    double amount = consoleValidator.getDouble();
                    ExpenseCategory expense = expenses.get(categoryId);
                    expense.removeValue(amount);
                    break;
                }
                case 3: {
                    System.out.println("Введите через запятую номера тех категорий, которые вы хотите оставить.");
                    String categoriesString = consoleValidator.getString();
                    String[] categoriesIds = categoriesString.split(",");
                    double selectedExpenses = 0;
                    for (String category : categoriesIds) {
                        try {
                            int categoryId = Integer.parseInt(category) - 1;
                            ExpenseCategory expense = expenses.get(categoryId);
                            selectedExpenses += expense.getValue();
                            System.out.println(expense.getName() + ": " + expense.getValue());
                        } catch (NumberFormatException | IndexOutOfBoundsException e) {
                            break;
                        }
                    }
                    System.out.println("ВЫБРАННЫЕ РАСХОДЫ: " + selectedExpenses);
                    consoleValidator.pause();
                    break;
                }
                case 0: {
                    return;
                }
                default:
                    System.out.println("Неправильный ввод.");
            }
        }
    }

    public void manageIncome() {
        while (true) {
            System.out.println("*************************");
            System.out.println("ДОХОДЫ");
            System.out.println("*************************");
            if (income.isEmpty()) {
                System.out.println("Вы еще не создали категории доходов.");
                consoleValidator.pause();
                return;
            } else {
                for (int i = 0; i < income.size(); i++) {
                    int id = i + 1;
                    System.out.println(id + ". " + income.get(i).getName() + ": " + income.get(i).getValue());
                }
            }
            System.out.println("ОБЩИЕ ДОХОДЫ: " + currentWallet.getIncomeTotal());
            System.out.println("*************************");
            System.out.println("1. Добавить доход.");
            System.out.println("2. Уменьшить доход.");
            System.out.println("3. Просмотреть только определенные категории доходов.");
            System.out.println("0. Вернуться назад.");

            int choice = consoleValidator.getInt();
            switch (choice) {
                case 1: {
                    System.out.println("Какую категорию доходов вы хотите изменить?");
                    int categoryId = consoleValidator.getInt() - 1;
                    System.out.println("На сколько?");
                    double amount = consoleValidator.getDouble();
                    income.get(categoryId).addValue(amount);
                    break;
                }
                case 2: {
                    System.out.println("Какую категорию доходов вы хотите изменить?");
                    int categoryId = consoleValidator.getInt() - 1;
                    System.out.println("На сколько?");
                    double amount = consoleValidator.getDouble();
                    income.get(categoryId).removeValue(amount);
                    break;
                }
                case 3: {
                    System.out.println("Введите номера тех категорий, которые вы хотите оставить, через пробел.");
                    String categoriesString = consoleValidator.getString();
                    String[] categoriesIds = categoriesString.split(" ");
                    for (String category : categoriesIds) {
                        int categoryId = Integer.parseInt(category) - 1;
                        IncomeCategory incomeCategory = income.get(categoryId);
                        System.out.println(incomeCategory.getName() + ": " + incomeCategory.getValue());
                    }
                    consoleValidator.pause();
                    break;
                }
                case 0:
                    return;
                default:
                    System.out.println("Неправильный ввод.");
            }
        }
    }

    public void manageBudgets() {
        while (true) {
            System.out.println("*************************");
            System.out.println("УПРАВЛЕНИЕ ЛИМИТАМИ");
            System.out.println("*************************");
            if (expenses.isEmpty()) {
                System.out.println("Сначала вам необходимо создать категорию расходов.");
                return;
            }
            for (int i = 0; i < expenses.size(); i++) {
                int id = i + 1;
                if (!expenses.get(i).hasBudget())
                    System.out.println(id + ". " + expenses.get(i).getName() + ": НЕТ ЛИМИТА.");
                else {
                    double balance = expenses.get(i).getBudget() - expenses.get(i).getValue();
                    System.out.println(id + ". "
                            + expenses.get(i).getName() + ": "
                            + expenses.get(i).getBudget()
                            + " (" + balance + " осталось)"
                    );
                }
            }
            System.out.println("*************************");
            System.out.println("1. Установить лимит.");
            System.out.println("0. Вернуться назад.");
            int choice = consoleValidator.getInt();
            if (choice == 1) {
                System.out.println("Какой категории вы хотите поставить лимит?");
                int categoryId = consoleValidator.getInt() - 1;
                System.out.println("Какой? 0 отключает лимит.");
                double amount = consoleValidator.getDouble();
                expenses.get(categoryId).setBudget(amount);
            } else return;
        }
    }

    public void manageCategories() {
        while (true) {
            System.out.println("РАСХОДЫ:");
            for (int i = 0; i < expenses.size(); i++) {
                int id = i + 1;
                System.out.println(id + ". " + expenses.get(i).getName());
            }
            System.out.println("ДОХОДЫ:");
            for (int i = 0; i < income.size(); i++) {
                int id = i + 1;
                System.out.println(id + ". " + income.get(i).getName());
            }
            System.out.println("*************************");
            System.out.println("1. Добавить новую категорию.");
            System.out.println("2. Убрать категорию.");
            System.out.println("0. Вернуться назад.");

            int choice = consoleValidator.getInt();
            switch (choice) {
                case 1: {
                    System.out.println("Введите название категории: ");
                    String name = consoleValidator.getString();
                    System.out.println("Введите '1' чтобы сделать категорию расходов. '2' для категории доходов.");
                    int categoryType = consoleValidator.getInt();
                    if (categoryType == 1) currentWallet.addExpensesCategory(name);
                    else if (categoryType == 2) currentWallet.addIncomeCategory(name);
                    else System.err.println("Неправильный тип категории. Попробуйте еще раз.");
                    break;
                }
                case 2: {
                    System.out.println("Введите '1' чтобы сделать категорию расходов. '2' для категории доходов.");
                    int categoryType = consoleValidator.getInt();
                    System.out.println("Введите номер категории: ");
                    int id = consoleValidator.getInt() - 1;
                    try {
                        if (categoryType == 1) currentWallet.removeExpensesCategory(id);
                        else if (categoryType == 2) currentWallet.removeIncomeCategory(id);
                        else System.err.println("Неправильный тип категории.");
                    } catch (IndexOutOfBoundsException e) {
                        System.err.println("Неправильная категория.");
                    }
                    break;
                }
                case 0: return;
                default:
                    System.err.println("Неправильный ввод.");
            }
        }
    }
}
