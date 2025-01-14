package console;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.UUID;

public class ConsoleValidator {
    Scanner scanner = new Scanner(System.in);

    public int getInt() {
        while (true) {
            try {
                int input = scanner.nextInt();
                return input;
            } catch (InputMismatchException e) {
                System.err.println("Используйте только целые числа.");
                scanner.nextLine();
            }
        }
    }

    public void pause() {
        scanner.nextLine();
        System.out.println("*************************");
        System.out.println("Нажмите ENTER, чтобы продолжить.");
        System.out.println("*************************");
        scanner.nextLine();
    }

    public double getDouble (boolean noNegatives) {
        while (true) {
            try {
                double input = scanner.nextDouble();
                if (noNegatives) {
                    if (input < 0) {
                        throw new InputMismatchException();
                    }
                }
                return input;
            } catch (InputMismatchException e) {
                System.err.println("Используйте только числа.");
                scanner.nextLine();
            }
        }
    }

    public double getDouble() {
        return getDouble(false);
    }

    public String getString () {
        return scanner.next();
    }
    public UUID getUUID() {
        while (true) {
            String input = "";
            try {
                input = scanner.next();
                return UUID.fromString(input);
            } catch (IllegalArgumentException e) {
                if (input.equals("0")) return null;
                System.err.println("Неверный ID. Попробуй еще раз.");
            }
        }
    }
    public String getUsername() {
        while (true) {
            String userName = scanner.next();
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            if (userName.matches(".*[;%, ].*")) {
                System.err.println("Запрещенные символы! Введите логин еще раз.");
            } else {
                return userName;
            }
        }
    }
}
