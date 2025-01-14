package console;

import budget_app.BudgetApp;
import database.DataSaver;
import user.User;
import wallet.ExpenseCategory;
import wallet.Operation;
import wallet.Wallet;
import wallet.WalletCategory;

import java.util.ArrayList;
import java.util.UUID;

public class ConsoleInterface {

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setCurrentWallet(Wallet currentWallet) {
        this.currentWallet = currentWallet;
    }

    private User currentUser;
    private Wallet currentWallet;

    private enum Menu {
        AUTHORIZATION, USER, WALLET, EXPENSES, EXPENSES_REPORT, EXPENSES_REMOVING, EXPENSES_ADDING,
        INCOME, INCOME_REPORT, INCOME_REMOVING, INCOME_ADDING, LIMITS, CATEGORIES
    }

    private Menu menu = Menu.AUTHORIZATION;
    private final ConsoleValidator consoleValidator = new ConsoleValidator();

    public void run() {
        while(true) {
            switch(menu) {
                case AUTHORIZATION: authorizationMenu();break;
                case USER: userMenu(); break;
                case WALLET: walletMenu(); break;
                case EXPENSES: operationsMenu(Menu.EXPENSES); break;
                case EXPENSES_REPORT:
                    operationsReportMenu(Menu.EXPENSES_REPORT, currentWallet.getExpenseCategories()); break;
                case EXPENSES_REMOVING:
                    operationsRemoveMenu(Menu.EXPENSES_REMOVING); break;
                case EXPENSES_ADDING:
                    operationsAddMenu(Menu.EXPENSES_ADDING, currentWallet.getExpenseCategories()); break;
                case INCOME: operationsMenu(Menu.INCOME); break;
                case INCOME_REPORT:
                    operationsReportMenu(Menu.INCOME_REPORT, currentWallet.getIncomeCategories()); break;
                case INCOME_REMOVING:
                    operationsRemoveMenu(Menu.INCOME_REMOVING); break;
                case INCOME_ADDING:
                    operationsAddMenu(Menu.INCOME_ADDING, currentWallet.getIncomeCategories()); break;
                case CATEGORIES: categoriesMenu(); break;
                case LIMITS: limitsMenu(); break;
            }
        }
    }

    private void userMenu() {
        prettySeparator();
        System.out.println("ЛИЧНЫЙ КАБИНЕТ:");
        prettySeparator();
        System.out.println("1. Выбрать кошелек.");
        System.out.println("0. Выйти из аккаунта.");

        int choice = consoleValidator.getInt();
        switch(choice) {
            case 1: chooseWalletMenu(); break;
            case 0: setMenu(Menu.AUTHORIZATION); DataSaver.saveUser(currentUser); return;
            default:
                System.err.println("Неправильная операция.");
                consoleValidator.pause();
        }
    }

    private void chooseWalletMenu() {
        ArrayList<UUID> wallets = currentUser.getWallets();
        if (wallets.isEmpty()) {
            System.out.println("У вас пока нет кошельков. Хотите открыть его? (y/n)");
            String response = consoleValidator.getString();
            if (response.equals("y")) {
                Wallet newWallet = BudgetApp.createNewWallet(currentUser);
                setCurrentWallet(newWallet);
                setMenu(Menu.WALLET);
            } else {
                System.out.println("А что вы тогда делаете в этом приложении...?");
                System.out.println("Обидно! Закрываюсь!");
                System.exit(0);
            }
            return;
        }
        for (int i = 0; i < wallets.size(); i++) {
            int id = i + 1;
            System.out.println(id + ". " + wallets.get(i));
        }
        System.out.println("Какой кошелек хотите открыть?");

        int choice = consoleValidator.getInt() - 1;

        try {
            Wallet chosenWallet = BudgetApp.getWallets().get(wallets.get(choice));
            setCurrentWallet(chosenWallet);
            setMenu(Menu.WALLET);
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Кошелька с таких индексом не существует.");
        } catch (Exception e) {
            System.err.println("Что-то пошло серьезно не так...");
            e.printStackTrace();
        }

    }

    private void walletMenu() {
        prettySeparator();
        System.out.println("УПРАВЛЕНИЕ КОШЕЛЬКОМ " + currentWallet.getUUID());
        prettySeparator();
        System.out.println("Общие расходы: " + currentWallet.getExpensesTotal());
        System.out.println("Общий доход: " + currentWallet.getIncomeTotal());
        prettySeparator();
        System.out.println("1. Расходы.");
        System.out.println("2. Доходы.");
        System.out.println("3. Лимиты.");
        System.out.println("4. Категории");
        System.out.println("5. Сделать перевод на другой кошелек.");
        System.out.println("0. Назад в личный кабинет.");
        int choice = consoleValidator.getInt();

        switch(choice) {
            case 1: operationsMenu(Menu.EXPENSES); break;
            case 2: operationsMenu(Menu.INCOME); break;
            case 3: limitsMenu(); break;
            case 4: categoriesMenu(); break;
            case 5: transferMenu(); break;
            case 0: setMenu(Menu.USER); DataSaver.saveWallet(currentWallet); return;
        }
    }

    private void transferMenu() {
        prettySeparator();
        System.out.println("Введите ID кошелька, на который хотите сделать перевод.");
        UUID walletID = consoleValidator.getUUID();
        if (walletID == null) {
            return;
        }
        if (!BudgetApp.getWallets().containsKey(walletID)) {
            System.out.println("Такого кошелька нет в базе данных. Попробуй еще раз.");
            consoleValidator.pause();
            return;
        }
        System.out.println("Сколько вы хотите перевести?");
        double amount = consoleValidator.getDouble();

        BudgetApp.makeTransfer(currentWallet, BudgetApp.getWallets().get(walletID), amount);
        System.out.println("Перевод был успешно проведен.");
        consoleValidator.pause();
    }

    private void limitsMenu() {
        setMenu(Menu.LIMITS);
        prettySeparator();
        ArrayList<ExpenseCategory> expenseCategories = currentWallet.getExpenseCategories();
        if (expenseCategories.isEmpty()) {
            System.err.println("У вас отсутствуют какие-либо категории расходов. Добавьте их и попробуйте снова.");
            consoleValidator.pause();
            return;
        }
        for (int i = 0; i < expenseCategories.size(); i++) {
            int id = i + 1;
            ExpenseCategory category = expenseCategories.get(i);
            String limitInfo = category.hasLimit()
                    ? " (осталось " + category.getRemainingLimit() + ")"
                    : " (нет лимита)";
            System.out.println(id + ". " + category + limitInfo);
        }
        System.out.println("Какой категории вы хотите изменить лимит? 0, чтобы вернуться.");
        int choice = consoleValidator.getInt() - 1;
        if (choice < 0) {
            setMenu(Menu.WALLET);
            return;
        }
        try {
            ExpenseCategory category = expenseCategories.get(choice);
            System.out.println("Сколько?");
            double limit = consoleValidator.getDouble();
            category.setLimit(limit);
            System.out.println("Лимит был успешно установлен.");
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Этой категории не существует.");
        }

    }

    private void categoriesMenu() {
        setMenu(Menu.CATEGORIES);
        prettySeparator();
        System.out.println("КАТЕГОРИИ:");
        prettySeparator();
        System.out.println("РАСХОДЫ:");
        displayCategories(currentWallet.getExpenseCategories());
        System.out.println("ДОХОДЫ:");
        displayCategories(currentWallet.getIncomeCategories());
        prettySeparator();
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
            case 0:
                setMenu(Menu.WALLET);
        }
    }

    private void operationsMenu(Menu operationType) {
        int showingLimit = 5;

        double totalOperations;
        ArrayList<Operation> lastOperations;
        if (operationType.equals(Menu.EXPENSES)) {
            totalOperations = currentWallet.getExpensesTotal();
            lastOperations = currentWallet.getExpenseOperations();
        } else {
            totalOperations = currentWallet.getIncomeTotal();
            lastOperations = currentWallet.getIncomeOperations();
        }

        prettySeparator();
        System.out.println("Общие: " + totalOperations);

        int i = 0;
        if (lastOperations.size() > showingLimit) i = lastOperations.size() -showingLimit;
        for (; i < lastOperations.size(); i++) {
            System.out.println(lastOperations.get(i).toString());
        }

        prettySeparator();
        System.out.println("1. Посмотреть операции по категориям.");
        System.out.println("2. Добавить операцию.");
        System.out.println("3. Убрать операцию.");
        System.out.println("0. Назад.");

        int choice = consoleValidator.getInt();

        Menu passedMenu;
        ArrayList<? extends WalletCategory> passedCategories;
        if (operationType.equals(Menu.EXPENSES)) {
            passedMenu = getExpensesMenu(choice);
            passedCategories = currentWallet.getExpenseCategories();
        } else {
            passedMenu = getIncomeMenu(choice);
            passedCategories = currentWallet.getIncomeCategories();
        }


        switch(choice) {
            case 1: operationsReportMenu(passedMenu, passedCategories);
                setMenu(operationType); break;
            case 2: operationsAddMenu(passedMenu, passedCategories);
                setMenu(operationType); break;
            case 3: operationsRemoveMenu(passedMenu);
                setMenu(operationType); break;
            case 0: setMenu(Menu.WALLET); return;
        }

    }

    private Menu getExpensesMenu(int choice) {
        return switch (choice) {
            case 1 -> Menu.EXPENSES_REPORT;
            case 2 -> Menu.EXPENSES_ADDING;
            case 3 -> Menu.EXPENSES_REMOVING;
            default -> Menu.EXPENSES;
        };
    }

    private Menu getIncomeMenu(int choice) {
        return switch (choice) {
            case 1 -> Menu.INCOME_REPORT;
            case 2 -> Menu.INCOME_ADDING;
            case 3 -> Menu.INCOME_REMOVING;
            default -> Menu.INCOME;
        };
    }

    private void expensesMenu() {
        prettySeparator();
        System.out.println("Общие расходы: " + currentWallet.getExpensesTotal());
        prettySeparator();
        System.out.println("Последние пять операций:");
        int i = 0;
        ArrayList<Operation> operations = currentWallet.getOperations();
        if (operations.size() > 5) {
         i = operations.size() - 5;
        }
        for (; i < currentWallet.getOperations().size(); i++) {
            System.out.println(operations.get(i).toString());
        }
        prettySeparator();
        System.out.println("1. Посмотреть расходы по категориям.");
        System.out.println("2. Добавить расход.");
        System.out.println("3. Убрать расход.");
        System.out.println("0. Назад.");

        int choice = consoleValidator.getInt();

        switch(choice) {
            case 1: operationsReportMenu(Menu.EXPENSES_REPORT, currentWallet.getExpenseCategories());
            setMenu(Menu.EXPENSES); break;
            case 2: operationsAddMenu(Menu.EXPENSES_ADDING, currentWallet.getExpenseCategories());
            setMenu(Menu.EXPENSES); break;
            case 3: operationsRemoveMenu(Menu.EXPENSES_REMOVING);
            setMenu(Menu.EXPENSES); break;
            case 0: setMenu(Menu.WALLET); return;
        }

        setMenu(Menu.EXPENSES);
    }

    private <T extends WalletCategory> void operationsReportMenu(Menu chosenMenu, ArrayList<T> chosenCategories) {
        setMenu(chosenMenu);
        ArrayList<Integer> forbiddenIndexes = new ArrayList<>();
        while (true) {
            double selectedExpenses = 0;
            prettySeparator();
            for (int i = 0; i < chosenCategories.size(); i++) {
                int id = i + 1;
                if (forbiddenIndexes.contains(i)) continue;
                System.out.println(id + ". "
                        + chosenCategories.get(i).getName()
                        + " " + currentWallet.getCategoryTotal(chosenCategories.get(i))
                );
                selectedExpenses += currentWallet.getCategoryTotal(chosenCategories.get(i));
            }
            System.out.println();
            System.out.println("Общие операции по выбранным категориям: " + selectedExpenses);
            prettySeparator();
            System.out.println("Если вы хотите скрыть/показать какую-то категорию, введите ее номер. '0' для выхода.");
            int choice = consoleValidator.getInt();
            if (choice == 0) {
                menu = Menu.EXPENSES;
                return;
            }
            if (choice < 0 || choice > chosenCategories.size()) {
                System.err.println("Неверный номер категории.");
                return;
            }
            boolean removedValue = false;
            for (int i = forbiddenIndexes.size() - 1; i >= 0; i--) {
                if (forbiddenIndexes.get(i) == choice-1) {
                    forbiddenIndexes.remove(i);
                    removedValue = true;
                    continue;
                }
                removedValue = false;
            }
            if (!removedValue) forbiddenIndexes.add(choice - 1);
        }
    }

    private <T extends WalletCategory> void operationsAddMenu(Menu chosenMenu, ArrayList<T> chosenCategories) {
        setMenu(chosenMenu);
        prettySeparator();
        System.out.println("Сколько?");
        double amount = consoleValidator.getDouble();
        prettySeparator();
        System.out.println("Какая категория?");
        displayCategories(chosenCategories);

        try {
            int categoryIndex = consoleValidator.getInt() - 1;
            WalletCategory category = chosenCategories.get(categoryIndex);
            currentWallet.addOperation(category, amount);
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Неверная категория.");
        }

    }

    private void operationsRemoveMenu(Menu chosenMenu) {
        setMenu(chosenMenu);
        prettySeparator();
        //more efficient algorithm (only iterates through either expenses or income, but loses correct order
//        for (int i = 0; i < chosenOperations.size(); i++) {
//            int id = i + 1;
//            System.out.println(id + ". " + chosenOperations.get(i));
//        }
        ArrayList<Operation> operations = currentWallet.getOperations();
        for (int i = 0; i < operations.size(); i++) {
            int id = i + 1;
            System.out.println(id + ". " + operations.get(i));
        }
        System.out.println();
        System.out.println("Какую операцию вы хотите удалить?");
        int choice = consoleValidator.getInt() - 1;
        try {
            Operation chosenOperation = operations.get(choice);
            currentWallet.removeOperation(chosenOperation);
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Операция не была найдена!");
        }
    }

    private void authorizationMenu() {
        System.out.println("АВТОРИЗАЦИЯ");
        prettySeparator();
        System.out.println("1. Авторизироваться.");
        System.out.println("2. Зарегистрироваться.");
        System.out.println("0. Завершить работу.");

        int choice = consoleValidator.getInt();
        switch(choice) {
            case 1: loginMenu(); break;
            case 2: registrationMenu(); break;
            case 0: DataSaver.saveAll(); System.exit(0);
        }
    }

    private void registrationMenu() {
        prettySeparator();
        String username = "";
        do {
            if (!username.isEmpty()) {
                System.err.println("Этот логин уже используется.");
                username = consoleValidator.getString();
            } else {
                System.out.println("Введите желаемый логин:");
                username = consoleValidator.getUsername();
            }
            if (username.equals("0")) return;
        } while (BudgetApp.getUsers().containsKey(username));

        System.out.println("Введите желаемый пароль: ");
        String password = consoleValidator.getString();
        if (password.equals("0")) {
            return;
        }

        registerUser(username, password);
        prettySeparator();
        System.out.println("Пользователь " + username + " был успешно зарегистрирован.");
        prettySeparator();
    }

    private void registerUser(String username, String password) {
        User newUser = new User(username, password);
        DataSaver.saveUser(newUser);
    }

    private void loginMenu() {
        prettySeparator();
        String username = "";
        do {
            if (!username.isEmpty()) {
                System.err.println("Пользователь не был найден в базе данных. " +
                        "Попробуйте еще раз или введите '0', чтобы вернуться");
            }
            System.out.println("Введите ваш логин:");
            username = consoleValidator.getUsername();
            if (username.equals("0")) return;
        } while (!BudgetApp.getUsers().containsKey(username));

        User user = BudgetApp.getUsers().get(username);

        System.out.println("Введите пароль:");
        String password = consoleValidator.getString();
        while (!user.login(password)) {
            System.err.println("Неверный пароль.");
            password = consoleValidator.getString();
            if (password.equals("0")) return;
        }
        prettySeparator();
        System.out.println("УСПЕШНАЯ АВТОРИЗАЦИЯ.");
        prettySeparator();
        System.out.println("Добро пожаловать, " + username + "!");
        prettySeparator();

        setMenu(Menu.USER);
        setCurrentUser(user);

    }

    private void setMenu (Menu menu) {
        this.menu = menu;
    }

    private void prettySeparator() {
        System.out.println("*************************");
    }

    private <T extends WalletCategory> void displayCategories(ArrayList<T> categories) {
        for (int i = 0; i < categories.size(); i++) {
            int id = i + 1;
            System.out.println(id + ". " + categories.get(i));
        }
    }
}
