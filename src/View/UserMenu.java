package View;

import Control.AppManager;
import Control.NoteAction;
import Control.Prompts;
import Model.Note;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class UserMenu {
    ConsoleIO f;
    AppManager appManager;
    Scanner scan;
    String username;

    public UserMenu(AppManager appManager, Scanner scan) {
        this.appManager = appManager;
        this.scan = scan;
        this.f = new ConsoleIO(scan);
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
            String choice = f.input();
            switch (choice) {
                case "1" -> addNewNote();
                case "2" -> readNotes(false);
                case "3" -> changePassword();
                case "4" -> {
                    loggedIn = false;
                    appManager.handleLogOut();
                }
                default -> System.out.println(invalidChoice);
            }
        }
    }

    public void readNotes(boolean allNotes) throws SQLException {
        appManager.readNotes(allNotes);
    }

    public void showAllNotes(List<Note> notes) throws SQLException {
        if (notes.isEmpty()) {
            System.out.println("You haven't added any notes yet.");
            returnToMain();
        } else {
            int index = 1;
            System.out.println(notesmenu + notesMenuPrompt);
            for (Note n : notes) {
                System.out.println(f.getNotePostString(n, index, true, false, false));
                index += 1;
            }
            System.out.println(notesMenuPrompt);
            String choice = f.input();
            if (!f.checkIfQuit(choice)) {
                if (choice.equalsIgnoreCase("clear")) {
                    deleteAllNotes();
                } else {
                    showNotesMenu(choice, notes);
                }
            }
        }
    }

    public void showNotesMenu(String userInput, List<Note> notes) throws SQLException {
        try {
            int index = Integer.parseInt(userInput);
            if (index - 1 < notes.size()) {
                Note n = notes.get(index - 1);
                System.out.println(notePrompt);
                System.out.println(f.getNotePostString(n, index, true, false, false));
                String choice = f.input();
                switch (choice) {
                    case "1" -> editNote(n);
                    case "2" -> deleteNote(n);
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
            if (f.checkIfQuit(title)) {
                complete = true;
            }
            System.out.println("Submit your note contents:\n");
            String contents = scan.nextLine();
            if (f.checkIfQuit(contents)) {
                complete = true;
            }
            Prompts result = appManager.handleUserAction(NoteAction.ADD, new Note(-1, title, contents, LocalDateTime.now()));
            if (result == Prompts.OK) {
                System.out.print("Your new note post has been added:\nTitle: " + title + "\nContents: " + contents);
                complete = true;
            } else {
                f.prompt(result);
            }
        }
    }

    private boolean returnToMain() {
        System.out.println(returnToMain);
        String choice = f.input();
        return choice.equalsIgnoreCase("x");
    }

    private void editNote(Note n) throws SQLException {
        boolean titleDone = false;
        int changes = 0;
        boolean textDone = false;
        while (!textDone) {
            String askUser = !titleDone ? askTitle : askContents;
            String promptUser = !titleDone ? "Input your new title:\n" : "Input your new note contents:\n";
            System.out.println(askUser + yesno + yesno);
            String choice = f.input();
            if (f.checkIfQuit(choice)) {
                return;
            }
            boolean doChange = f.checkConfirm(choice, askUser);
            if (doChange) {
                System.out.println(promptUser);
                String input = f.input();
                if (f.checkIfQuit(input)) {
                    return;
                }
                if (!titleDone) {
                    n.setTitle(input);
                    changes += 1;
                    titleDone = true;
                } else {
                    n.setContents(input);
                    changes += 1;
                    textDone = true;
                }
            } else {
                if (titleDone) {
                    textDone = true;
                }
                titleDone = true;
            }
        }
        if (changes == 0) {
            System.out.println("No edits have been made to the post.");
        } else {
            System.out.println("Your note has been updated: \n" + f.getNotePostString(n, -1, true, false, false));
            appManager.handleUserAction( NoteAction.EDIT, n);
        }
    }

    private void deleteNote(Note n) throws SQLException {
        System.out.println("Are you sure that you want to delete this note?\n");
        System.out.println(yesno);
        String choice = scan.nextLine();
        if (choice.equalsIgnoreCase("1")) {
            Prompts response = appManager.handleUserAction(NoteAction.REMOVE, n);
            if (response == Prompts.OK) {
                System.out.println("The note has been deleted.");
            } else {
                f.prompt(Prompts.ERROR);
            }
        }
    }

    private void deleteAllNotes() throws SQLException {
        int attempts = 0;
        boolean valid = true;
        System.out.println(confirmDelete + yesno);
        String choice = scan.nextLine();
        if (choice.equalsIgnoreCase("1")) {
            while (valid) {
                if (attempts > 3) {
                    System.out.println("You've made 3 failed attempts to input your password. Please try again at a later point.");
                    valid = false;
                }
                System.out.println("--Removing all notes:");
                String password = f.promptVerify();
                attempts += 1;
                if (f.checkIfQuit(password)){
                    return;
                }
                Prompts response = appManager.handleUserAction(NoteAction.REMOVE_ALL, new Note(-1, "", "", null));
               System.out.println("response is: " + response);
                if (response == Prompts.OK) {
                    System.out.println("All of your notes have been removed.");
                    valid = false;
                }
                else {
                    f.prompt(response);
                }
            }
        }
    }

    private void changePassword() throws SQLException {
        boolean completed = false;
        while (!completed) {
            System.out.println("--Changing password:");
            System.out.println(returnToMain);
            System.out.println("Enter your current password:");
            String password = scan.nextLine().trim();
            if (f.checkIfQuit(password)) {
                return;
            }
            System.out.println("Enter your new password: ");
            String newPassword = scan.nextLine().trim();
            if (f.checkIfQuit(newPassword)){
                return;
            }
            Prompts response = appManager.changePassword(username, password, newPassword);
            if (response == Prompts.NEW_PASS_OK) {
                completed = true;
            } else {
                f.prompt(response);
            }
        }
    }

    private final String mainMenuPrompt = "What would you like to do?\n- 1: Add a new note\n- 2: Browse saved notes\n- 3: Change password\n- 4:Log out\nSubmit the number of your choice:\n";
    private final String invalidChoice = "You haven't submitted av valid choice. Please try again.\n";
    private final String yesno = "Type '1' for YES, '2' for NO:\n";
    private final String notesmenu = "You have saved the following notes.\n";
    private final String notesMenuPrompt = "\nEnter the number of a post to edit or delete it.\nEnter 'clear' if you want to delete all posts.\nEnter 'x' to return to the main menu.\n";
    private final String notePrompt = "Would you like to:\n- 1: Edit, or \n- 2: Delete\nthe note below? \n- Type 'B' to return to all of your posts\n- or 'X' to return to the main menu.\nSubmit your choice:\n";
    private final String returnToMain = "Press 'x' to return to the main menu.\n";
    private final String confirmDelete = "Are you sure that you want to delete all of your notes?\n";
    private String askTitle = "Would you like to edit your title?\n";
    private String askContents = "Would you like to edit your note contents?\n";
}