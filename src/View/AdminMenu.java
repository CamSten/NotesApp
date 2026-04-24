package View;

import Control.AppManager;
import Control.Event;
import Control.Prompts;
import Model.Note;
import Model.User;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    AppManager appManager;
    Scanner scan;
    Feedback f;
    public enum AdminAction {SEE_USERS, SEE_NOTES, SEE_ALL_NOTES, DELETE_NOTE, DELETE_USER_NOTES, DELETE_ALL_NOTES}

    public AdminMenu(AppManager appManager, Scanner scan) {
        this.appManager = appManager;
        this.scan = scan;
        this.f = new Feedback(scan);
    }

    public void showAdminMenu() throws SQLException {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println(mainMenuPrompt);
            String choice = f.input();
            switch (choice) {
                case "1" -> seeNotes();
                case "2" -> displayUsers(appManager.handleAdminAction(AdminAction.SEE_USERS, -1));
                case "3" -> {
                    appManager.handleLogOut();
                    loggedIn = false;
                }
                default -> System.out.println(invalidChoice);
            }
        }
    }

    private void seeNotes() throws SQLException {
        boolean complete = false;
        Event event = null;
        boolean allNotes = true;
        while (!complete) {
            System.out.println(noteMenuPrompt + returnToMain);
            String choice = f.input();
            switch (choice) {
                case "x" ->{
                    complete = true;
                    continue;
                }
                case "1" -> event = appManager.handleAdminAction(AdminAction.SEE_ALL_NOTES, -1);
                case "2" -> {
                    System.out.println(userNamePrompt);
                    allNotes = false;
                    event = appManager.handleAdminAction(AdminAction.SEE_NOTES, parseInput(f.input()));
                }
                default -> f.promptInvalid();
            }
            if (event.getPrompts() == Prompts.WRONG_PASS || event.getPrompts() == Prompts.ERROR){
                System.out.println(event.getPrompts());
                complete = true;
            }
            else  {
                displayNotes(event, allNotes);
                complete = true;
            }
        }
    }

    public Prompts displayUsers(Event event) throws SQLException {
        List<User> users = f.getUsers(event);
        if (users.isEmpty()){
            System.out.println(noUsers);
            return Prompts.OK;
        }
        while (true) {
            System.out.println(usersInfo);
            for (User u : users) {
                System.out.println(getUserString(u, false));
            }
            System.out.println(userNumberPrompt + returnToMain);
            String choice = f.input();
            if (f.checkIfQuit(choice)) {
                return Prompts.OK;
            }
            int userIdInput = parseInput(choice);
            if (validUserSelection(userIdInput, users)) {
                displayNotes(appManager.handleAdminAction(AdminAction.SEE_NOTES, userIdInput), false);
                return Prompts.OK;
            }
            f.promptInvalid();
        }
    }
    private boolean validUserSelection(int userIdInput, List<User> users){
        return users.stream().anyMatch(user -> user.getId() == userIdInput);
    }
    private int parseInput(String input){
        try {
            return Integer.parseInt(input);
        }
        catch (NumberFormatException e){
            return -1;
        }
    }
    private void displayNotes(Event event, boolean allNotes) throws SQLException {
        boolean complete = false;
        List<Note> notes = f.getNotes(event);
        if (!notes.isEmpty()) {
            while (!complete) {
                String prompt = allNotes ? notesIntroUser + " " + notes.getFirst().getUsername() : notesIntroAll;
                System.out.println(prompt);
                int index = 1;
                for (Note n : notes) {
                    System.out.println(getNotePostString(n, index, allNotes, false));
                    index += 1;
                }
                System.out.println(notesPrompt + returnToMain + submit);
                String input = f.input();
                complete = f.checkIfQuit(input);
                if(complete){
                    displayNotesSubmenu(input, notes);
                }
            }
        }
        System.out.println(noNotes);
    }
    private Prompts displayNotesSubmenu(String choice, List<Note> notes) throws SQLException {
        int noteIndex = parseInput(choice);
        Prompts result = Prompts.OK;
        if (!f.checkIfQuit(choice)) {
            return Prompts.OK;
        }
        while (true) {
            if (noteIndex >-1) {
                Note thisNote = notes.get(noteIndex - 1);
                System.out.println(getNotePostString(thisNote, -1, true, true));
                System.out.println(deletePrompt + returnToMain);
                String subChoice = f.input();
                if (subChoice.equals("1")) {
                    Event response = appManager.handleAdminAction(AdminAction.DELETE_NOTE, thisNote.getId());
                    System.out.println(response.getPrompts());
                    return response.getPrompts();
                } else if (f.checkIfQuit(subChoice)) {
                    return result;
                }
            }
            f.promptInvalid();
        }
    }
    public String promptVerify() {
        System.out.println(passwordPrompt);
        return scan.nextLine().trim();
    }

    private String getUserString(User u, boolean allInfo){
        StringBuilder post = new StringBuilder();
        if (!allInfo){
            post.append("User nr.");
            post.append(u.getId());
            post.append(":\nUsername: ");
            post.append(u.getUsername());
            post.append("\nDate of registration: ");
            post.append(u.getRegDate());
            post.append("\n");
        }
        return post.toString();
    }
    private String getNotePostString(Note n, int index, boolean allNotes, boolean allInfo) {
        StringBuilder post = new StringBuilder();
        if (!allInfo) {
            post.append("Note nr. ");
            post.append(index);
            post.append(" :\n");
        }
        if (allNotes) {
            post.append("User: ");
            post.append(n.getUsername());
        }
        post.append("\nDate: ");
        post.append(n.getSubmitDate());
        if (allInfo) {
            post.append("\nTitle: ");
            post.append(n.getTitle());
            post.append("\nContents:");
            post.append(n.getContents());
            post.append("\n");
        }
        return post.toString();
    }

    public void forcedLogout() throws SQLException {
        System.out.println(threeFailedAttempts);
        appManager.handleLogOut();
    }

    private final String mainMenuPrompt = "What would you like to do?\n- 1: View notes\n- 2: View users\n- 3: Log out\nSubmit the number of your choice:\n";
    private final String noteMenuPrompt = "Options:\n- 1: See all notes\n- 2: See all notes for a specific user\n";
    private final String submit = "Submit your choice:\n";
    private final String notesIntroUser = "The following notes have been submitted for user: ";
    private final String notesIntroAll = "The following notes have been submitted.\n";
    private final String noNotes = "No notes have been found.";
    private final String noUsers = "No users have been registered.";
    private final String notesPrompt = "Enter the number of a note post for more details.\n Submit your number below:\n";
    private final String userNamePrompt = "Enter the username to view their notes";
    private final String userNumberPrompt = "Enter the number of a user to view their notes";
    private final String returnToMain = "Press 'X' to return to the main menu.\n";
    private final String deletePrompt = "Enter 1 if you would like to delete the note.\n";
    private final String passwordPrompt = "Please submit your password to confirm:\n";
    private final String usersInfo = "The following users are currently registered:\n";
    private final String invalidChoice = "You haven't submitted av valid choice. Please try again.\n";
    private final String threeFailedAttempts = "You have submitted an incorrect password three times, you will therefore be logged out.";
}