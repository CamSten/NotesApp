package View.UserIO;

import Control.AppManager;
import Control.Enums.Prompts;
import Model.DataObjects.Note;
import View.ConsoleInput;
import View.ConsoleOutput;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class UserMenu {
    private ConsoleInput ci;
    private AppManager appManager;
    private String username;
    private ConsoleOutput co;
    private UserNotesMenu notesMenu;
    private Map<String, SQLRunnable> mainMenuOptions;

    public UserMenu(AppManager appManager, ConsoleInput ci, ConsoleOutput co) throws SQLException {
        this.appManager = appManager;
        this.ci = ci;
        this.co = co;
        this.notesMenu = new UserNotesMenu(co, ci, this, appManager);
        this.mainMenuOptions = getMainMenu();
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
    public void changePassword() throws SQLException {
        while (true) {
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
                System.out.println("Your password has been changed.\n");
                return;
            } else {
                co.prompt(response);
            }
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
        List<SQLRunnable> toPerform = List.of(() -> notesMenu.addNewNote(), () -> notesMenu.readNotes(), this::changePassword, () -> appManager.handleLogOut());
        MenuOptions menuOptions = new MenuOptions(input, toPerform);
        return menuOptions.createMenu();
    }
    private final String mainMenuPrompt = "What would you like to do?\n- 1: Add a new note\n- 2: Browse saved notes\n- 3: Change password\n- 4:Log out\nSubmit the number of your choice:\n";
    public final String returnToMain = "Press 'x' to return to the main menu.\n";
}