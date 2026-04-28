package View.UserIO;

import Control.AppManager;
import Control.Enums.Prompts;
import Model.DataObjects.Note;
import View.*;
import java.sql.SQLException;
import java.util.*;

public class UserMenu {
    private final ConsoleInput ci;
    private final AppManager appManager;
    private String username;
    private final ConsoleOutput co;
    private final UserNotesMenu notesMenu;
    private final Map<String, SQLRunnable> mainMenuOptions;

    public UserMenu(AppManager appManager, ConsoleInput ci, ConsoleOutput co) throws SQLException {
        this.appManager = appManager;
        this.ci = ci;
        this.co = co;
        this.notesMenu = new UserNotesMenu(co, ci, this, appManager);
        this.mainMenuOptions = getMainMenu();
    }

    public void greet(String username) throws SQLException {
        this.username = username;
        System.out.println("\nWelcome, " + username + "!");
        showMainMenu();
    }

    public void showMainMenu() throws SQLException {
        boolean loggedIn = true;
        while (loggedIn) {
            co.printHeader("Main Menu");
            System.out.println(mainMenuPrompt);
            String choice = ci.lowCaseInput();
            SQLRunnable action = mainMenuOptions.get(choice);
            if (action == null){
                co.promptInvalid();
                continue;
            }
            action.run();
            if (choice.equals("4")){
                loggedIn = false;
            }
        }
    }
    public void showAllNotes(List<Note> notes) throws SQLException {
        SQLRunnable action = !notes.isEmpty() ? () -> notesMenu.showAllNotes(notes) : () -> notesMenu.informEmpty();
        action.run();
    }
    public boolean changePassword() throws SQLException {
        while (true) {
            co.printHeader("Changing password:");
            System.out.println(returnToMain);
            System.out.println("-- Enter your current password:");
            String password = ci.input();
            if (ci.checkIfQuit(password)) {
                return false;
            }
            if(appManager.checkPassword(password, username)){
                System.out.println("-- Enter your new password: ");
                String newPassword = ci.input();
                if (ci.checkIfQuit(newPassword)){
                    return false;
                }
                Prompts response = appManager.changePassword(username, password, newPassword);
                if (response == Prompts.NEW_PASS_OK) {
                    System.out.println("-- Your password has been changed.\n");
                    return true;
                } else {
                    co.prompt(response.toString());
                }
            }
            co.prompt(Prompts.WRONG_PASS.toString());
        }
    }
    public String confirmPassword(){
        return ci.promptVerify();
    }

    public void displayReply(Prompts prompts){
        System.out.println(prompts);
    }

    private Map<String, SQLRunnable> getMainMenu() throws SQLException {
        List<String> input = List.of("1", "2", "3", "4");
        List<SQLRunnable> toPerform = List.of(() -> notesMenu.addNewNote(), () -> notesMenu.readNotes(), () -> changePassword(), () -> appManager.handleLogOut());
        MenuOptions menuOptions = new MenuOptions();
        return menuOptions.createMenu(input, toPerform);
    }
    private final String mainMenuPrompt = "-- What would you like to do?\n- 1: Add a new note\n- 2: Browse saved notes\n- 3: Change password\n- 4:Log out\nSubmit the number of your choice:\n";
    public final String returnToMain = "-- Press 'x' to return to the main menu.\n";
}