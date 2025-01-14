package database;

import budget_app.BudgetApp;
import user.User;
import wallet.Wallet;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.UUID;

public class DataLoader {
    private static String dataDelimiter = BudgetApp.getDataDelimiter();
    private static String walletDelimiter = BudgetApp.getWalletDelimiter();
    private static File[] loadFolder(String folderName) {
        File folder = new File(BudgetApp.getDataFolderPath() + folderName);
        if (!folder.exists()) {
            folder.mkdir();
            return null;
        }
        return folder.listFiles();
    }

    private abstract static class TextLoader {
        public void load(String folderName) {
            File[] files = DataLoader.loadFolder(folderName);
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        try {
                            parseText(Files.readString(file.toPath()));
                        } catch (IOException e) {
                            System.err.println("Error reading file " + file.getAbsolutePath());
                        }
                    }
                }
            }
        }

        protected abstract void parseText(String content);
    }

    public static class UserLoader extends TextLoader {
        @Override
        protected void parseText(String content) {
            String[] userInfo = content.split(dataDelimiter);
            try {
                String username = userInfo[0];
                String password = userInfo[1];
                ArrayList<UUID> walletUUIDs = new ArrayList<>();
                if (userInfo.length > 2) {
                    String[] stringUUIDs = userInfo[2].split(walletDelimiter);
                    for (String stringUUID : stringUUIDs) {
                        walletUUIDs.add(UUID.fromString(stringUUID));
                    }
                }
                new User(username, password, walletUUIDs);
            } catch (Exception e) {
                System.err.println("Data is corrupted!");
                e.printStackTrace();
            }
        }
    }

    private abstract static class SerializedLoader {

        public void load(String folderName) {
            File[] files = DataLoader.loadFolder(folderName);
            if (files != null) {
                for (File file : files) {
                    loadFile(file);
                }
            }
        }

        public void loadFile(File file) {
            if (file.isFile()) {
                try {
                    FileInputStream fileIn = new FileInputStream(file);
                    ObjectInputStream in = new ObjectInputStream(fileIn);
                    readObject(in.readObject());
                    in.close();
                    fileIn.close();

                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Error reading file " + file.getName());
                }
            }
        }

        protected abstract void readObject(Object obj);
    }

    public static class WalletLoader extends SerializedLoader {
        @Override
        protected void readObject(Object obj) {
            Wallet wallet = (Wallet) obj;
            BudgetApp.addWallet(wallet);
        }
    }
}
