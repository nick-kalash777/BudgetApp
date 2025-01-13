package database;

import budget_app.BudgetApp;
import user.User;
import wallet.Wallet;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.UUID;

public class DataSaver {
    private static final String dataDelimiter = BudgetApp.getDataDelimiter();
    private static final String walletDelimiter = BudgetApp.getWalletDelimiter();

    public static void saveUsers() {
            BudgetApp.getUsers().values().forEach(DataSaver::saveUser);
    }

    public static void saveWallets() {
            BudgetApp.getWallets().values().forEach(DataSaver::saveWallet);
    }

    public static void saveUser(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        StringBuilder walletSB = new StringBuilder();
        for (UUID uuid: user.getWallets()) {
            if (!walletSB.isEmpty())
                walletSB.append(walletDelimiter);
            walletSB.append(uuid.toString());
        }
        String userString = username+dataDelimiter+password+dataDelimiter+walletSB;

        String fileName = username + ".usr";
        try (FileWriter file = new FileWriter(BudgetApp.getDataFolderPath()+"/users/"+fileName)) {
            file.write(userString);
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR WHEN SAVING DATA!");
        }
    }

    public static void saveWallet(Wallet wallet) {
        String fileName = wallet.getUUID() + ".wallet";
        try {
            FileOutputStream fileOut = new FileOutputStream(BudgetApp.getDataFolderPath() + "/wallets/" + fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            out.writeObject(wallet);

            out.close();
            fileOut.close();

        } catch (IOException e) {
            System.out.println("CRITICAL ERROR: UNABLE TO SAVE FILE.");
            e.printStackTrace();
        }
    }



}
