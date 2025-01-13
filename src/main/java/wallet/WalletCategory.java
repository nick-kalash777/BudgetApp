package wallet;

import java.io.Serializable;

public class WalletCategory implements Serializable {
    private String name;
    private double value;

    public WalletCategory (String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public void addValue (double value) {
        this.value += value;
    }
    public void removeValue (double value) {
        this.value -= value;
    }
}
