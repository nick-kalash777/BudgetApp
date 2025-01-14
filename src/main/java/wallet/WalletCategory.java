package wallet;

import java.io.Serializable;

public class WalletCategory implements Serializable {
    private String name;

    public WalletCategory (String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

}
