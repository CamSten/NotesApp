package View;

import Control.AdminAction;
import Control.AppManager;
import Control.Event;
import Control.Prompts;
import Model.LogPost;
import Model.Note;
import Model.NoteLog;
import Model.User;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    AppManager appManager;
    Scanner scan;
    ConsoleIO f;

    public AdminMenu(AppManager appManager, Scanner scan) {
        this.appManager = appManager;
        this.scan = scan;
        this.f = new ConsoleIO(scan);
    }

    public void showAdminMenu() throws SQLException {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println(mainMenuPrompt + submit);
            String choice = f.input();
            switch (choice) {
                case "1" -> seeNotes();
                case "2" -> appManager.handleAdminAction(AdminAction.SEE_USERS, -1);
                case "3" -> seeLogins();
                case "4" -> {
                    appManager.handleLogOut();
                    loggedIn = false;
                }
                default -> System.out.println(invalidChoice);
            }
        }
    }

    public void printResponse(Prompts prompts) {
        System.out.println(prompts);
    }

    public void seeNotes() throws SQLException {
        while (true) {
            System.out.println(noteMenuPrompt + returnToMain);
            String choice = f.input();
            if (f.checkIfQuit(choice)) {
                return;
            }
            switch (choice) {
                case "1" -> {
                    appManager.handleAdminAction(AdminAction.SEE_ALL_NOTES, -1);
                    return;
                }
                case "2" -> {
                    System.out.println(userNamePrompt + "notes.");
                    appManager.handleAdminAction(AdminAction.SEE_NOTES, parseInput(f.input()));
                    return;
                }
                case "3" -> {
                    seeNoteLogs();
                    return;
                }
                default -> f.promptInvalid();
            }
        }
    }

    public String promptVerify() {
        return f.promptVerify();
    }

    public void displayUsers(Event event) throws SQLException {
        if (!f.checkValidAction(event.getPrompts())) {
            System.out.println(event.getPrompts());
            return;
        }
        List<User> users = f.getUsers(event);
        if (users.isEmpty()) {
            System.out.println(noUsers);
            return;
        }
        System.out.println(usersInfo);
        for (User u : users) {
            System.out.println(f.getUserPostString(u, false));
        }
        System.out.println(userNumberPrompt + returnToMain);
        String choice = f.input();
        if (f.checkIfQuit(choice)) {
            return;
        }
        displayUserSubmenu(choice, users);
    }

    private void displayUserSubmenu(String input, List<User> users) throws SQLException {
        int userIdInput = parseInput(input);
        User u = f.getUserFromList(userIdInput, users);
        while (true){
            if (f.getUserFromList(userIdInput, users) != null) {
                System.out.println(f.getUserPostString(u, false));
                System.out.println(userSubmenuPrompt + returnToMain + submit);
                String choice = f.input();
                if (f.checkIfQuit(choice)) {
                    return;
                }
                switch (choice) {
                    case "1" -> {
                        appManager.handleAdminAction(AdminAction.SEE_NOTES, userIdInput);
                        return;
                    }
                    case "2" -> {
                        appManager.handleAdminAction(AdminAction.SEE_LOGIN_FOR_USER, userIdInput);
                        return;
                    }
                    default -> f.promptInvalid();
                }
            }
        }
    }
    private int parseInput(String input){
        try {
            return Integer.parseInt(input);
        }
        catch (NumberFormatException e){
            return -1;
        }
    }
    public void displayNotes(List<Note> notes, boolean allNotes, boolean allInfo) throws SQLException {
        boolean complete = false;
        if (notes.isEmpty()) {
            System.out.println(noNotes);
            return;
        }
        while (!complete) {
            String prompt = allNotes ? notesIntroAll : notesIntroUser + " " + notes.getFirst().getUsername();
            System.out.println(prompt);
            for (Note n : notes) {
                System.out.println(f.getNotePostString(n, n.getId(), allInfo, allNotes, true));
            }
            System.out.println(notesPrompt);
            String input = f.input();
            if(!f.checkIfQuit(input) && f.validNoteSelection(parseInput(input), notes)){
                displayNotesSubmenu(input, notes);
                complete = true;
            }
        }
    }
    private void displayNotesSubmenu(String choice, List<Note> notes) throws SQLException {
        int noteIdInput = parseInput(choice);
        Note thisNote = notes.stream().filter(n -> n.getId() == noteIdInput).findFirst().orElse(null);
        if (f.checkIfQuit(choice)) {
            return;
        }
        while (true) {
            if (thisNote != null) {
                System.out.println(f.getNotePostString(thisNote, -1, true, true, true));
                System.out.println(deletePrompt + returnToMain);
                String subChoice = f.input();
                if (subChoice.equals("1")) {
                    appManager.handleAdminAction(AdminAction.DELETE_NOTE, thisNote.getId());
                    return;
                } else if (f.checkIfQuit(subChoice)) {
                    return;
                }
            }
            f.promptInvalid();
        }
    }
    private void seeLogins() throws SQLException {
        while (true) {
            System.out.println(logMenuPrompt + returnToMain +  submit);
            String choice = f.input();
            if (f.checkIfQuit(choice)){
                return;
            }
            switch (choice){
                case "1"-> {
                    appManager.handleAdminAction(AdminAction.SEE_ALL_LOGIN_LOGS, -1);
                    return;
                }
                case "2", "3" -> {
                    showLogSubmenu(choice);
                    return;
                }
                default -> f.promptInvalid();
            }
        }
    }

    private void seeNoteLogs() throws SQLException {
        while (true) {
            System.out.println(noteLogMenuPrompt + returnToMain +  submit);
            String choice = f.input();
            if (f.checkIfQuit(choice)){
                return;
            }
            switch (choice){
                case "1" -> {
                    appManager.handleAdminAction(AdminAction.SEE_ALL_NOTE_LOGS, -1);
                    return;
                }
                case "2" -> {
                    showNoteLogSubmenu(choice);
                    return;
                }
                default -> f.promptInvalid();
            }
        }
    }
    private void showLogSubmenu(String choice) throws SQLException {
        if (f.checkIfQuit(choice)){
            return;
        }
        while (true) {
            if (choice.equalsIgnoreCase("2")) {
                System.out.println(userNamePrompt + "log in history");
                String username = f.input();
                int userName = appManager.getUserIdForAdminUse(username);
                if (userName != -1) {
                    appManager.handleAdminAction(AdminAction.SEE_LOGIN_FOR_USER, Integer.parseInt(username));
                    return;
                }
            } else if (choice.equalsIgnoreCase("3")) {
                System.out.println(loginStatusPrompt + returnToMain + submit);
                String status = f.input();
                if (f.checkIfQuit(status)) {
                    return;
                }
                if (f.checkIfValidChoice(List.of("1", "2", "3"), status)) {
                    appManager.handleAdminAction(AdminAction.SEE_LOGIN_FOR_STATUS, parseInput(status));
                    return;
                }
            }
            f.promptInvalid();
        }
    }

    private void showNoteLogSubmenu(String choice) throws SQLException {
        if (f.checkIfQuit(choice)){
            return;
        }
        while (true) {
            System.out.println(noteLogMenuPrompt + returnToMain + submit);
            String input = f.input();
            if (f.checkIfQuit(input)){
                return;
            }
            switch (input){
                case "1" -> {
                    appManager.handleAdminAction(AdminAction.SEE_ALL_NOTE_LOGS, -1);
                    return;
                }
                case "2" -> {
                    System.out.println(userNamePrompt + "note logs.\n" + returnToMain);
                    String username = f.input();
                    if (f.checkIfQuit(username)){
                        return;
                    }
                    int userId = appManager.getUserIdForAdminUse(username);
                    if (userId != -1){
                        appManager.handleAdminAction(AdminAction.SEE_USER_NOTE_LOGS, userId);
                        return;
                    }
                }
                default -> f.promptInvalid();
            }
        }
    }
    public void showLogPosts(List<LogPost> logPosts){
        System.out.println("List of login posts:\n");
        for (LogPost log : logPosts){
            System.out.println(f.getLogPostString(log));
        }
    }
    public void showNoteLogs(List<NoteLog> noteLogs){
        System.out.println("List of note logs:\n");
        for (NoteLog log : noteLogs){
            System.out.println(f.getNoteLogString(log));
        }
    }

    public void forcedLogout() throws SQLException {
        System.out.println(threeFailedAttempts);
        appManager.handleLogOut();
    }

    private final String mainMenuPrompt = "What would you like to do?\n- 1: View notes\n- 2: View users\n- 3: View log ins\n- 4: Log out\n";
    private final String noteMenuPrompt = "Options:\n- 1: See all notes\n- 2: See all notes for a specific user\n- 3: See note logs\n";
    private final String logMenuPrompt = "Would you like to:\n- 1: See all log in history\n- 2: See log in history for a specific user\n- 3: See log in with specific status\n";
    private final String noteLogMenuPrompt = "Would you like to:\n- 1: See all note logs\n- 2: See note logs for a specific user\n";
    private final String userSubmenuPrompt = "Would you like to:\n- 1: See notes \n- 2: See log in history\n";
    private final String submit = "Submit your choice:\n";
    private final String notesIntroUser = "The following notes have been submitted for user: ";
    private final String notesIntroAll = "The following notes have been submitted:\n";
    private final String noNotes = "No notes have been found.";
    private final String noUsers = "No users have been registered.";
    private final String notesPrompt = "Enter the number of a note post for more details.\nSubmit your number below:\n";
    private final String userNamePrompt = "Enter the username to view their ";
    private final String userNumberPrompt = "Enter the number of a user in order to view their notes or their log in history\n";
    private final String loginStatusPrompt = "Which log in status would you like to get the history for?\n- 1: Success\n- 2: Fail\n- 3: Unknown user\n";
    private final String returnToMain = "Press 'X' to return to the main menu.\n";
    private final String deletePrompt = "Enter 1 if you would like to delete the note.\n";
    private final String usersInfo = "The following users are currently registered:\n";
    private final String invalidChoice = "You haven't submitted av valid choice. Please try again.\n";
    private final String threeFailedAttempts = "You have submitted an incorrect password three times, you will therefore be logged out.\n";
}