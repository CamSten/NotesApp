package View.AdminIO;

import Control.AdminAction;
import Control.AppManager;
import Control.Event;
import Control.Prompts;
import Model.LogPost;
import Model.Note;
import Model.NoteLog;
import View.ConsoleInput;
import View.ConsoleOutput;
import java.sql.SQLException;
import java.util.List;

public class AdminMenu {
    AppManager appManager;
    ConsoleInput ci;
    ConsoleOutput co;
    AdminNotesMenu notesMenu;
    AdminUserMenu userMenu;
    AdminLogsMenu logsMenu;

    public AdminMenu(AppManager appManager, ConsoleInput ci, ConsoleOutput co) {
        this.appManager = appManager;
        this.ci = ci;
        this.co = co;
        this.notesMenu = new AdminNotesMenu(co, ci, this, appManager);
        this.userMenu = new AdminUserMenu(co, ci, this, appManager);
        this.logsMenu = new AdminLogsMenu(co, ci, this, appManager);
    }

    public void displayUsers(Event event) throws SQLException {
        userMenu.displayUsers(event);
    }

    public void displayNotes(List<Note> notes, boolean allNotes, boolean allInfo) throws SQLException {
        notesMenu.displayNotes(notes, allNotes, allInfo);
    }

    public void showLogPosts(List<LogPost> logPosts) {
        logsMenu.showLogPosts(logPosts);
    }

    public void showNoteLogs(List<NoteLog> noteLogs) {
        notesMenu.showNoteLogs(noteLogs);
    }

    public void showAdminMenu() throws SQLException {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println(mainMenuPrompt + submit);
            String choice = ci.lowCaseInput();
            switch (choice) {
                case "1" -> notesMenu.seeNotes();
                case "2" -> appManager.handleAdminAction(AdminAction.SEE_USERS, -1);
                case "3" -> logsMenu.seeLogins();
                case "4" -> {
                    appManager.handleLogOut();
                    loggedIn = false;
                }
                default -> co.promptInvalid();
            }
        }
    }

    public void printResponse(Prompts prompts) {
        co.prompt(prompts);
    }

    public String promptVerify() {
        return ci.promptVerify();
    }

    public void forcedLogout() throws SQLException {
        System.out.println(threeFailedAttempts);
        appManager.handleLogOut();
    }
    private final String mainMenuPrompt = "What would you like to do?\n- 1: View notes\n- 2: View users\n- 3: View log ins\n- 4: Log out\n";
    public final String submit = "Submit your choice:\n";
    public final String userNamePrompt = "Enter the username to view their ";
    public final String returnToMain = "Press 'X' to return to the main menu.\n";
    private final String threeFailedAttempts = "You have submitted an incorrect password three times, you will therefore be logged out.\n";
}