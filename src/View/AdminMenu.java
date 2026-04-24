package View;

import Control.AppManager;
import Control.Event;
import Control.Prompts;
import Model.Note;
import Model.User;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    AppManager appManager;
    Scanner scan;
    Feedback f;


    public enum AdminAction {SEE_USERS, SEE_NOTES, SEE_ALL_NOTES, DELETE_NOTE, DELETE_USER_NOTES, DELETE_ALL_NOTES}

    public AdminMenu(AppManager appManager) {
        this.appManager = appManager;
        this.scan = new Scanner(System.in);
        this.f = new Feedback(scan);
    }

    public void showAdminMenu() throws SQLException {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println(mainMenuPrompt);
            String choice = f.input();
            switch (choice) {
                case "1" -> seeNotes();
                case "2" -> displayUsers(appManager.handleAdminAction(AdminAction.SEE_USERS, Collections.emptyList()));
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
            System.out.println(noteMenuPrompt);
            System.out.println(returnToMain);
            String choice = f.input();
            System.out.println("choice in seeNotes is: " + choice);
            switch (choice) {
                case "x" ->{
                    complete = true;
                    continue;
                }
                case "1" -> event = appManager.handleAdminAction(AdminAction.SEE_ALL_NOTES, Collections.emptyList());
                case "2" -> {
                    System.out.println(userNamePrompt);
                    allNotes = false;
                    event = appManager.handleAdminAction(AdminAction.SEE_NOTES, List.of(f.input()));
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

    public void displayUsers(Event event) throws SQLException {
        boolean complete = false;
        while (!complete) {
            if (event.getData() instanceof List list) {
                if (!list.isEmpty() && list.getFirst() instanceof User) {
                    List<User> users = (List<User>) event.getData();
                    System.out.println(usersInfo);
                    for (User u : users) {
                        System.out.println("User nr." + u.getId() + ":\nUsername: " + u.getUsername() + "\nDate of registration: " + u.getRegDate()+"\n");
                    }
                    System.out.println(userNumberPrompt + returnToMain);
                    String choice = f.input();
                    try {
                        Integer.parseInt(choice);
                        displayNotes(appManager.handleAdminAction(AdminAction.SEE_NOTES, List.of(choice)), false);
                        complete = true;
                    } catch (NumberFormatException e) {
                        complete = notParseable(choice);
                    }
                }
                else {
                    System.out.println(noUsers);
                    complete = true;
                }
            }
        }
    }
    private boolean notParseable(String choice){
        boolean complete = false;
        if (f.checkIfQuit(choice)) {
            complete = true;
        } else {
            f.promptInvalid();
        }
        return complete;
    }
    private void displayNotes(Event event, boolean allNotes){
        boolean complete = false;
        if (event.getData() instanceof List list && list.isEmpty()){
        }
        while (!complete) {
            if (event.getData() instanceof List list) {
                if (list.isEmpty()) {
                    System.out.println(noNotes);
                    complete = true;
                } else if (list.getFirst() instanceof Note) {
                    List<Note> notes = (List<Note>) event.getData();
                    String prompt = notesIntroUser + " " + notes.getFirst().getUsername();
                    if (allNotes) {
                        prompt = notesIntroAll;
                    }
                    System.out.println(prompt);
                    int index = 1;
                    for (Note n : notes) {
                        System.out.println(getNotePostString(n, index, allNotes, false));
                        index += 1;
                    }
                    System.out.println(notesPrompt + returnToMain + submit);
                    String choice = f.input();
                    complete = displayNotesSubmenu(choice, notes);
                }
            }
            else {
                System.out.println(event.getPrompts());
                complete = true;
            }
        }
    }
    private boolean displayNotesSubmenu(String choice, List<Note> notes) {
        boolean completeSubmenu = false;
        boolean complete = false;
        System.out.println("choice in displayNotesSubmenu is: " + choice);
        if(f.checkIfQuit(choice)){
            completeSubmenu = true;
            complete = true;
        }
        while (!completeSubmenu) {
            try {
                int userIndex = Integer.parseInt(choice);
                Note thisNote = notes.get(userIndex - 1);
                System.out.println(getNotePostString(thisNote, -1, true, true));
                System.out.println(deletePrompt + returnToMain);
                String subChoice = f.input();
                System.out.println("subChoice is: " + subChoice);
                if (subChoice.equals("1")) {
                    Event response = appManager.handleAdminAction(AdminAction.DELETE_NOTE, List.of(String.valueOf(thisNote.getId()), thisNote.getUsername()));
                    System.out.println(response.getPrompts());
                    complete = true;
                    completeSubmenu = true;
                }
                else if (subChoice.equals("x")){
                    complete = true;
                    completeSubmenu = true;
                }
            } catch (NumberFormatException | SQLException e) {
                complete = notParseable(choice);
            }
        }
        return complete;
    }
    public String promptVerify() {
        System.out.println(passwordPrompt);
        return scan.nextLine().trim();
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
        post.append(n.getDate());
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