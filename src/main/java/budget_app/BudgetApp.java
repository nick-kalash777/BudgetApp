package budget_app;

import console.ConsoleInterface;
import console.ConsoleValidator;
import database.DataLoader;
import user.User;
import wallet.Wallet;

import java.util.HashMap;
import java.util.UUID;

public class BudgetApp {
    private static final String dataDelimiter = ";";
    private static final String walletDelimiter = ",";
    private static final String dataFolderPath = System.getProperty("user.dir") + "/data/";
    private static final ConsoleValidator consoleValidator = new ConsoleValidator();
    private static final HashMap<String, User> users = new HashMap<>();
    private static final HashMap<UUID, Wallet> wallets = new HashMap<>();

    public static void run() {
        loadUsers();
        loadWallets();
        ConsoleInterface console = new ConsoleInterface();
        console.authorizeMenu();
    }

    public static String getDataDelimiter() {
        return dataDelimiter;
    }

    public static String getWalletDelimiter() {
        return walletDelimiter;
    }

    public static String getDataFolderPath() {
        return dataFolderPath;
    }

    public static void loadWallets() {
        DataLoader.WalletLoader walletLoader = new DataLoader.WalletLoader();
        walletLoader.load("wallets");
    }

    public static void loadUsers() {
        DataLoader.UserLoader userLoader = new DataLoader.UserLoader();
        userLoader.load("users");
    }

    public static HashMap<String, User> getUsers() {
        return users;
    }

    public static HashMap<UUID, Wallet> getWallets() {
        return wallets;
    }

    public static void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public static void addWallet(Wallet wallet) {
        wallets.put(wallet.getUUID(), wallet);
    }
}
