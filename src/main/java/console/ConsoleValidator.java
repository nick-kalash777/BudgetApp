package console;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ConsoleValidator {
    Scanner scanner = new Scanner(System.in);

    public int getInt() {
        while (true) {
            try {
                int input = scanner.nextInt();
                if (input < 0) throw new InputMismatchException();
                return input;
            } catch (InputMismatchException e) {
                System.err.println("Используйте только целые положительные числа.");
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

    public double getDouble() {
        while (true) {
            try {
                double input = scanner.nextDouble();
                if (input < 0) throw new InputMismatchException();
                return input;
            } catch (InputMismatchException e) {
                System.err.println("Используйте только положительные числа.");
                scanner.nextLine();
            }
        }
    }

    public String getString () {
        return scanner.next();
    }
    public String getUsername() {
        if (scanner.hasNextLine()) scanner.nextLine();
        while (true) {
            String userName = scanner.nextLine();
            if (userName.matches(".*[;%, ].*")) {
                System.err.println("Запрещенные символы! Введите логин еще раз.");
            } else {
                return userName;
            }
        }
    }
}
