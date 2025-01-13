package user;
import budget_app.BudgetApp;
import org.mindrot.jbcrypt.BCrypt;
import wallet.Wallet;

import java.util.ArrayList;

import java.io.*;
import java.util.UUID;

public class User implements Serializable {
    private String username;
    private String password;
    private ArrayList<UUID> walletUUIDs;

    //new user
    public User(String username, String password) {
        this.username = username;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
        this.walletUUIDs = new ArrayList<>();
        BudgetApp.addUser(this);
    }
    //loading existing user
    public User(String username, String password, ArrayList<UUID> walletUUIDs) {
        this.username = username;
        this.password = password;
        this.walletUUIDs = walletUUIDs;
        BudgetApp.addUser(this);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean login(String password) {
        return BCrypt.checkpw(password, this.password);
    }

    public void addWallet(UUID uuid) {
        walletUUIDs.add(uuid);
    }
    public void addWallet(Wallet wallet) { walletUUIDs.add(wallet.getUUID()); }

    public void removeWallet(UUID uuid) {
        walletUUIDs.remove(uuid);
    }
    public void removeWallet(int index) {
        walletUUIDs.remove(index);
    }
    public void removeWallet(Wallet wallet) {
        walletUUIDs.remove(wallet.getUUID());
    }

    public ArrayList<UUID> getWallets() {
        return walletUUIDs;
    }
}
