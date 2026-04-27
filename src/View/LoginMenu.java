package View;

import Control.AppManager;
import Control.Prompts;
import Model.User;
import View.AdminIO.AdminMenu;
import View.UserIO.UserMenu;
import java.sql.SQLException;
import java.util.EnumSet;

public class LoginMenu {
    private final ConsoleInput ci;
    private final ConsoleOutput co;
    private final AppManager appManager;
    private final UserMenu userMenu;
    private final AdminMenu adminMenu;
    private User user;
    private final EnumSet<Prompts> passwordErrors = EnumSet.of(Prompts.WRONG_PASS, Prompts.SHORT_PASS, Prompts.LONG_PASS);
    private final EnumSet<Prompts> usernameErrors = EnumSet.of(Prompts.NO_SUCH_USER, Prompts.SHORT_NAME, Prompts.LONG_NAME);

    public LoginMenu(UserMenu userMenu, AdminMenu adminMenu, AppManager appManager, ConsoleInput ci, ConsoleOutput co){
        this.userMenu = userMenu;
        this.adminMenu = adminMenu;
        this.appManager = appManager;
        this.ci = ci;
        this.co = co;
    }

    public void showLoginMenu() throws SQLException {
        System.out.print("\nPress 1 to log in\nPress 2 to create a new account\nPress 'x' to quit");
        String choice = ci.lowCaseInput();
        switch (choice) {
            case "1" -> getLoginInput("--Log in:\n", false);
            case "2" -> getLoginInput("--Creating a new account:\n", true);
            case "x" -> System.exit(1);
            default -> co.promptInvalid();
        }
    }

    private String getPasswordInput(boolean newUser) throws SQLException {
        String prompt = passwordPrompt;
        if (newUser){
            prompt = newPasswordPrompt;
        }
        System.out.println(prompt);
        String password = ci.input();
        if (ci.checkIfQuit(password)) {
            showLoginMenu();
        }
        return password;
    }
    private String getUsernameInput(boolean newUser) throws SQLException {
        String prompt = namePrompt;
        if (newUser){
            prompt = newUsernamePrompt;
        }
        System.out.println(prompt);
        System.out.println(returnToLogin);
        String name = ci.input();
        if (ci.checkIfQuit(name.toLowerCase())) {
            showLoginMenu();
        }
        return name;
    }
    private boolean confirmPass(String firstPass, String secondPass){
        return firstPass.equals(secondPass);
    }
    private void getLoginInput (String prompt, boolean newUser) throws SQLException {
        String passwordInput;
        String nameInput = "";
        boolean validPassword = false;
        boolean validUsername = false;
        boolean readyToVerify = true;
        boolean completed = false;
        while (!completed) {
            System.out.println(prompt);
            if (!validUsername) {
                nameInput = getUsernameInput(newUser);
            }
            passwordInput = getPasswordInput(newUser);
            if (newUser){
                System.out.println("Verifying password.");
                String passwordAgain = getPasswordInput(true);
                readyToVerify = confirmPass(passwordInput, passwordAgain);
            }
            if (readyToVerify) {
                Prompts response = validate(nameInput, passwordInput, newUser);
                if (response == Prompts.OK) {
                    validPassword = true;
                    validUsername = true;
                }
                else if (usernameErrors.contains(response)){
                    co.prompt(response);
                }
                else if (passwordErrors.contains(response)){
                    validUsername = true;
                    co.prompt(response);
                }
            }
            if (validPassword){
                completed = true;
            }
        }
        giveAppAccess();
    }

    private Prompts validate(String name, String password, boolean newUser) throws SQLException {
        return appManager.validate(name, password, newUser);
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
    private final String returnToLogin = "Press 'x' to cancel.";
    private final String namePrompt = "Enter user name: ";
    private final String passwordPrompt = "Enter password:";
    private final String newPasswordPrompt = "Enter your choice of password:";
    private final String newUsernamePrompt = "Enter your choice of user name:";
}