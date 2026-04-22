package View;

import Control.AppManager;
import Control.Prompts;
import Model.Note;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class UserMenu {
    Feedback f;
    AppManager appManager;
    Scanner scan;
    String username;

    public UserMenu(AppManager appManager) {
        this.appManager = appManager;
        this.scan = new Scanner(System.in);
        this.f = new Feedback(scan);
    }

    public enum NoteAction {
        READ,
        EDIT,
        REMOVE,
        REMOVE_ALL,
        ADD
    }

    public void greet(String username) throws SQLException {
        this.username = username;
        System.out.println("Welcome, " + username + "!");
        showMainMenu();
    }
    public void showMainMenu() throws SQLException {
        System.out.println(mainMenuPrompt);
        String choice = f.input();
        switch (choice) {
            case "1" -> addNewNote();
            case "2" -> readNotes();
            case "3" -> changePassword();
            case "4" -> appManager.handleLogOut();
            default -> System.out.println(invalidChoice);
        }
    }
    public void readNotes() throws SQLException {
        List<Note> notes = appManager.readNotes();
        showAllNotes(notes);
    }
    public void showAllNotes(List<Note> notes) throws SQLException {
        if (notes.isEmpty()) {
            System.out.println("You haven't added any notes yet.");
            returnToMain();
        } else {
            int index = 1;
            System.out.println(notesmenu);
            System.out.println(notesMenuPrompt);
            for (Note n : notes) {
                System.out.println("Note nr. " + index + "  " + n.getDate() + "\n" + n.getTitle() + "\n" + n.getContents());
                index += 1;
            }
            System.out.println(notesMenuPrompt);
            String choice = f.input();
            if (choice.equalsIgnoreCase("x")) {
                showMainMenu();
            } else {
                showNotesMenu(choice, notes);
            }
        }
    }

    public void showNotesMenu(String userInput, List<Note> notes) throws SQLException {
        try {
            int index = Integer.parseInt(userInput);
            if (index - 1 < notes.size()) {
                Note n = notes.get(index - 1);
                System.out.println(notePrompt);
                System.out.println("Note nr. " + index + "  " + n.getDate() + "\n" + n.getTitle() + "\n" + n.getContents());
                String choice = f.input();
                switch (choice) {
                    case "1" -> editNote(n);
                    case "2" -> deleteNote(n);
                    case "clear" -> deleteAllNotes();
                    case "b" -> showAllNotes(notes);
                    case "x" -> showMainMenu();
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("You have not submitted a number, or 'x' for exit. Please try again");
            showAllNotes(notes);
        }
    }

    private void addNewNote() throws SQLException {
        boolean complete = false;
        while (!complete) {
            System.out.println("Submit the title for your note post:\n");
            System.out.println(returnToMain);
            String title = scan.nextLine();
            if (f.checkIfQuit(title)){
                complete = true;
            }
            System.out.println("Submit your note contents:\n");
            String contents = scan.nextLine();
            if (f.checkIfQuit(contents)){
                complete = true;
            }
            Prompts result = appManager.handleNoteAction(NoteAction.ADD, new Note(-1, title, contents, LocalDateTime.now()));
            if (result == Prompts.NOTE_OK) {
                System.out.print("Your new note post has been added:\nTitle: " + title + "\nContents: " + contents);
                complete = true;
            } else {
                f.prompt(result);
            }
        }
        returnToMain();
    }

    private void editNote(Note n) throws SQLException {
        String title = n.getTitle();
        String text = n.getContents();
        String prompt = "Would you like to edit your title?\n";
        System.out.println(prompt);
        System.out.println(returnToMain);
        System.out.println(yesno);
        String titleChoice = f.input();
        if (f.checkIfQuit(titleChoice)){
            returnToMain();
        }
        if (f.checkConfirm(titleChoice, prompt)) {
            System.out.println("Input your new title:\n");
            title = scan.nextLine().trim();
        }
        prompt = "Would you like to edit your note contents?\n";
        System.out.println(prompt);
        System.out.println(yesno);
        System.out.println(returnToMain);
        String textChoice = f.input();
        if (f.checkIfQuit(textChoice)){
            returnToMain();
        }
        if (f.checkConfirm(textChoice, prompt)) {
            System.out.println("Input your new note contents:\n");
            text = scan.nextLine().trim();
        }
        n.setTitle(title);
        n.setContents(text);
        System.out.println("Your note has been updated: \n" + n.getDate() + "\n" + n.getTitle() + "\n" + n.getContents());
        appManager.handleNoteAction(NoteAction.EDIT, n);
        returnToMain();
    }

    private void deleteNote(Note n) throws SQLException {
        System.out.println("Are you sure that you want to delete this note?\n");
        System.out.println(yesno);
        String choice = scan.nextLine();
        if (choice.equalsIgnoreCase("1")) {
            Prompts response =  appManager.handleNoteAction(NoteAction.REMOVE, n);
            if (response == Prompts.NOTE_OK){
                System.out.println("The note has been deleted.");
            }
            else {
                f.prompt(Prompts.ERROR);
            }
        } else {
            returnToMain();
        }
    }

    private void deleteAllNotes() throws SQLException {
        int attempts = 0;
        boolean valid = true;
        System.out.println("Are you sure that you want to delete all of your notes?\n");
        System.out.println(yesno);
        String choice = scan.nextLine();
        if (choice.equalsIgnoreCase("1")) {
            while (valid) {
                if (attempts > 3) {
                    System.out.println("You've made 3 failed attempts to input your password. Please try again at a later point.");
                    valid = false;
                }
                System.out.println("--Removing all notes:");
                System.out.println("Please input your password to confirm:\n");
                System.out.println(returnToMain);
                String password = scan.nextLine().trim();
                if (!f.checkIfQuit(password)) {
                    attempts += 1;
                    Prompts response = appManager.requestRemoveAllUserNotes(password);
                    if (response != Prompts.NOTE_OK) {
                        valid = false;
                        f.prompt(response);
                    }
                }
            }
        }
        returnToMain();
    }

    private void returnToMain() throws SQLException {
        System.out.println(returnToMain);
        String choice = f.input();
        if (choice.equalsIgnoreCase("x")) {
            showMainMenu();
        }
    }

    private void changePassword() throws SQLException {
        boolean completed = false;
        while (!completed) {
            System.out.println("--Changing password:");
            System.out.println(returnToMain);
            System.out.println("Enter your current password:");
            String password = scan.nextLine().trim();
            System.out.println("Enter your new password: ");
            String newPassword = scan.nextLine().trim();
            if (password.equalsIgnoreCase("x") || newPassword.equalsIgnoreCase("x")) {
                completed = true;
            }
            Prompts response = appManager.changePassword(username, password, newPassword);
            if (response == Prompts.NEW_PASS_OK) {
                completed = true;
            } else {
                f.prompt(response);
            }
        }
        returnToMain();
    }

    private final String mainMenuPrompt = "What would you like to do?\n- 1: Add a new note\n- 2: Browse saved notes\n- 3: Change password\n- 4:Log out\nSubmit the number of your choice:\n";
    private final String invalidChoice = "You haven't submitted av valid choice. Please try again.\n";
    private final String yesno = "Type '1' for YES, '2' for NO:\n";
    private final String notesmenu = "You have saved the following notes.\n";
    private final String notesMenuPrompt = "\nEnter the number of a post to edit or delete it.\nEnter 'clear' if you want to delete all posts.\nEnter 'x' to return to the main menu.\n";
    private final String notePrompt = "Would you like to:\n- 1: Edit, or \n- 2: Delete\nthe note below? \n- Type 'B' to return to all of your posts\n- or 'X' to return to the main menu.\nSubmit your choice:\n";
    private final  String returnToMain = "Press 'x' to return to the main menu.";
}