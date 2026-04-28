package View;

import Control.AppManager;
import Control.Enums.Prompts;
import Model.DataObjects.User;
import View.AdminIO.AdminMenu;
import View.UserIO.*;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class LoginMenu {
    private final ConsoleInput ci;
    private final ConsoleOutput co;
    private final AppManager appManager;
    private final UserMenu userMenu;
    private final AdminMenu adminMenu;
    private User user;
    private MenuOptions loginMenuOptions;
    Map<String, SQLRunnable> options;

    public LoginMenu(UserMenu userMenu, AdminMenu adminMenu, AppManager appManager, ConsoleInput ci, ConsoleOutput co) {
        this.userMenu = userMenu;
        this.adminMenu = adminMenu;
        this.appManager = appManager;
        this.ci = ci;
        this.co = co;
        getLoginMenuOptions();
        this.options = loginMenuOptions.createMenu();
    }

    public void showLoginMenu() throws SQLException {
        while (true) {
            co.printHeader("Log in");
            System.out.print("\nPress 1 to log in\nPress 2 to create a new account\nPress 'x' to quit\n");
            String choice = ci.lowCaseInput();
            SQLRunnable action = options.get(choice);
            if (action != null) {
                 if (action.run()){
                     return;
                 }
            } else {
                co.promptInvalid();
            }
        }
    }

    private boolean confirmPass(String firstPass, String secondPass) {
        return firstPass.equals(secondPass);
    }

    private boolean getLoginInput(String prompt, boolean newUser) throws SQLException {
        LoginData loginData = new LoginData();
        while (true) {
            System.out.println(prompt);
            boolean addedName = getName(loginData, newUser);
            if (!addedName) {
                return false;
            }
            if (!checkValidName(loginData.getUsernameInput(), newUser)) {
                Prompts response = newUser ? Prompts.NAME_TAKEN : Prompts.NO_SUCH_USER;
                loginData.resetName();
                co.prompt(response.toString());
                continue;
            }
            String inputPrompt = newUser ? newPasswordPrompt : passwordEnter;
            boolean addedPassword = gettingLoginInput(inputPrompt, loginData::setPasswordInput);
            if (!addedPassword) {
                return false;
            }
            if (!checkCompletedInput(loginData, newUser)) {
                continue;
            }
            Prompts response = validate(loginData.getUsernameInput(), loginData.getPasswordInput(), newUser);
            if (response != Prompts.OK) {
                co.prompt(response.toString());
                continue;
            }
            giveAppAccess();
            return true;
        }
    }

    private boolean getName(LoginData loginData, boolean newUser) {
        String inputPrompt = newUser ? newUsernamePrompt : namePrompt;
        if (loginData.isNameSet()) {
            return true;
        }
        return gettingLoginInput(inputPrompt, loginData::setUsernameInput);
    }

    private boolean checkCompletedInput(LoginData loginData, boolean newUser) {
        Consumer<String> confirmPassSetter = loginData::setConfirmPassInput;
        if (newUser) {
            boolean addedConfirm = gettingLoginInput(passwordConfirm, confirmPassSetter);
            if (!addedConfirm) {
                return false;
            }
            if (!confirmPass(loginData.getPasswordInput(), loginData.getConfirmPassInput())) {
                co.prompt(Prompts.PASS_MISMATCH.toString());
                loginData.resetPass();
                return false;
            }
        }
        return true;
    }

    private boolean gettingLoginInput(String prompt, Consumer<String> inputSetter) {
        System.out.println(prompt);
        System.out.println(returnToLogin);
        String input = ci.input();
        if (ci.checkIfQuit(input)) {
            return false;
        }
        inputSetter.accept(input);
        return true;
    }

    private boolean checkValidName(String nameInput, boolean newUser) throws SQLException {
        boolean isAvailable = appManager.checkAvailability(nameInput);
        return newUser ? isAvailable : !isAvailable;
    }

    private Prompts validate(String name, String password, boolean newUser) throws SQLException {
        return appManager.validate(name, password, newUser);
    }

    public void setUser(User user) {
        this.user = user;
    }

    private void giveAppAccess() throws SQLException {
        if (user.getRole() == User.Role.ADMIN) {
            adminMenu.showAdminMenu();
        } else {
            userMenu.greet(user.getUsername());
        }
    }
    private void getLoginMenuOptions(){
        this.loginMenuOptions = new MenuOptions(List.of("1", "2", "x"),
                List.of(() -> getLoginInput("--Submit username and password:\n", false),
                        ()-> getLoginInput("--Creating a new account:\n", true),
                        () -> {
                            System.exit(1);
                            return true;}));
    }
    private final String returnToLogin = "-- Press 'x' to cancel.";
    private final String namePrompt = "-- Enter user name: ";
    private final String passwordEnter = "-- Enter password: ";
    private final String passwordConfirm = "-- Confirm password: ";
    private final String newPasswordPrompt = "-- Enter your choice of password: ";
    private final String newUsernamePrompt = "-- Enter your choice of user name: ";
}