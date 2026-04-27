package View.UserIO;

import Control.AppManager;
import Control.Prompts;
import Model.Note;
import View.ConsoleInput;
import View.ConsoleOutput;
import java.sql.SQLException;
import java.util.List;

public class UserMenu {
    ConsoleInput ci;
    AppManager appManager;
    String username;
    ConsoleOutput co;
    UserNotesMenu notesMenu;

    public UserMenu(AppManager appManager, ConsoleInput ci, ConsoleOutput co) {
        this.appManager = appManager;
        this.ci = ci;
        this.co = co;
        this.notesMenu = new UserNotesMenu(co, ci, this, appManager);
    }

    public void greet(String username) throws SQLException {
        this.username = username;
        System.out.println("Welcome, " + username + "!");
        showMainMenu();
    }

    public void showMainMenu() throws SQLException {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println(mainMenuPrompt);
            String choice = ci.lowCaseInput();
            switch (choice) {
                case "1" -> notesMenu.addNewNote();
                case "2" -> notesMenu.readNotes(false);
                case "3" -> changePassword();
                case "4" -> {
                    loggedIn = false;
                    appManager.handleLogOut();
                }
                default -> co.promptInvalid();
            }
        }
    }
    public void showAllNotes(List<Note> notes) throws SQLException {
        notesMenu.showAllNotes(notes);
    }
    private void changePassword() throws SQLException {
        boolean completed = false;
        while (!completed) {
            System.out.println("--Changing password:");
            System.out.println(returnToMain);
            System.out.println("Enter your current password:");
            String password = ci.input();
            if (ci.checkIfQuit(password)) {
                return;
            }
            System.out.println("Enter your new password: ");
            String newPassword = ci.input();
            if (ci.checkIfQuit(newPassword)){
                return;
            }
            Prompts response = appManager.changePassword(username, password, newPassword);
            if (response == Prompts.NEW_PASS_OK) {
                completed = true;
                System.out.println("Your password has been changed.\n");
            } else {
                co.prompt(response);
            }
        }
    }
    public String confirmPassword(){
        return ci.promptVerify();
    }
    private final String mainMenuPrompt = "What would you like to do?\n- 1: Add a new note\n- 2: Browse saved notes\n- 3: Change password\n- 4:Log out\nSubmit the number of your choice:\n";
    public final String returnToMain = "Press 'x' to return to the main menu.\n";
}