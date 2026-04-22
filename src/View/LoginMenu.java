package View;

import Control.AppManager;
import Control.Prompts;
import Model.User;
import java.sql.SQLException;
import java.util.Scanner;

public class LoginMenu {
    private final Feedback f;
    private final AppManager appManager;
    private final UserMenu userMenu;
    private final AdminMenu adminMenu;
    private User user;
    private final Scanner scan = new Scanner(System.in);

    public LoginMenu(UserMenu userMenu, AdminMenu adminMenu, AppManager appManager){
        this.userMenu = userMenu;
        this.adminMenu = adminMenu;
        this.appManager = appManager;
        this.f = new Feedback(scan);
    }

    public void showLoginMenu() throws SQLException {
        System.out.print("\nPress 1 to log in\nPress 2 to create a new account\nPress 'x' to quit");
        String choice = f.input();
        switch (choice) {
            case "1" -> getLoginInput();
            case "2" -> getNewAccountInput();
            case "x" -> System.exit(1);
            default -> System.out.println(invalidChoice);
        }
    }

    private void getLoginInput() throws SQLException {
        boolean completed = false;
        boolean validLogin = false;
        while (!completed) {
            System.out.println("Enter user name:\n");
            System.out.println(returnToLogin);
            String name = scan.nextLine().trim();
            if (f.checkIfQuit(name.toLowerCase())) {
                completed = true;
                showLoginMenu();
            }
            System.out.println("Enter password:\n");
            String password = scan.nextLine().trim();
            if (f.checkIfQuit(password)) {
                completed = true;
                showLoginMenu();
            }
            Prompts response = appManager.validateLogin(name, password);
            if (response == Prompts.LOGIN_OK) {
                completed = true;
                validLogin = true;
            } else {
                f.prompt(response);
            }
        }
        if (validLogin) {
            showLoginMenu();
        }
    }

    private void getNewAccountInput() throws SQLException {
        boolean completed = false;
        boolean validLogin = false;
        while (!completed) {
            System.out.println("Enter your choice of user name:\n");
            System.out.println(returnToLogin);
            String name = scan.nextLine().trim();
            if (f.checkIfQuit(name.toLowerCase())) {
                completed = true;
                showLoginMenu();
            }
            System.out.println("Enter your choice of password:\n");
            String passwordRequest = scan.nextLine().trim();
            if (f.checkIfQuit(passwordRequest.toLowerCase())) {
                completed = true;
                showLoginMenu();
            }
            Prompts response = appManager.validateNewUser(name, passwordRequest);
            if (response == Prompts.LOGIN_OK) {
                completed = true;
                validLogin = true;
            } else {
                f.prompt(response);
            }
        }
        if (validLogin) {
            giveAppAccess();
        }
    }
    public void setUser(User user) {
        this.user = user;
    }

    private void giveAppAccess() throws SQLException {
        if (user.getRole() == User.Role.ADMIN){
            adminMenu.showAdminMenu();
        }
        else {
            userMenu.greet(user.getUsername());
        }
    }
    private final String invalidChoice = "You haven't submitted av valid choice. Please try again.\n";
    private final String returnToLogin = "Press 'x' to cancel.";
}
