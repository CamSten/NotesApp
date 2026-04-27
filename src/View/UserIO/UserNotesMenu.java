package View.UserIO;

import Control.AppManager;
import Control.NoteAction;
import Control.Prompts;
import Model.Note;
import View.ConsoleInput;
import View.ConsoleOutput;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class UserNotesMenu {
    private final ConsoleOutput co;
    private final ConsoleInput ci;
    private final UserMenu um;
    private final AppManager appManager;

    public UserNotesMenu(ConsoleOutput co, ConsoleInput ci, UserMenu um, AppManager appManager) {
        this.co = co;
        this.ci = ci;
        this.um = um;
        this.appManager = appManager;
    }

    public void readNotes(boolean allNotes) throws SQLException {
        appManager.readNotes(allNotes);
    }

    public void showAllNotes(List<Note> notes) throws SQLException {
        if (notes.isEmpty()) {
            System.out.println("You haven't added any notes yet.");
            return;
        }
        System.out.println(notesmenu);
        for (int i = 0; i < notes.size(); i++) {
            System.out.println(co.getNotePostString(notes.get(i), i + 1, true, false, false));
        }
        while (true) {
            System.out.println(notesMenuPrompt);
            String choice = ci.lowCaseInput();
            if (ci.checkIfQuit(choice)) {
                return;
            }
            int inputId = ci.parseInput(choice);
            if (choice.equalsIgnoreCase("clear")) {
                deleteAllNotes();
                return;
            }
            if (ci.getNoteIdFromList(inputId, notes) !=-1) {
                showNotesMenu(inputId, notes);
                return;
            }
            System.out.println("The note id does not exists. Try again.");
        }
    }

    public void showNotesMenu(int inputId, List<Note> notes) throws SQLException {
        while (true){
            Note n = notes.get(inputId - 1);
            System.out.println(notePrompt);
            System.out.println(co.getNotePostString(n, inputId, true, false, false));
            String choice = ci.lowCaseInput();
            if (ci.checkIfQuit(choice)){
                return;
            }
            Map<String, SQLRunnable> menu = getNoteMenuOptions(n, notes);
            SQLRunnable action = menu.get(choice);
            if (action == null){
                co.promptInvalid();
                continue;
            }
            action.run();
        }
    }
    public void addNewNote() throws SQLException {
        while (true) {
            Note note = new Note(-1, "", "", LocalDateTime.now());
            Consumer<String> titleSetter = note::setTitle;
            Consumer<String> textSetter = note::setContents;
            boolean addedTitle = gettingNoteInput("Submit the title for your note post:\n", titleSetter);
            if (!addedTitle) {
                return;
            }
            boolean addedText = gettingNoteInput("Submit your note contents:\n", textSetter);
            if (addedText) {
                Prompts result = appManager.handleUserAction(NoteAction.ADD, note);
                if (result == Prompts.OK) {
                    System.out.print("Your new note post has been added:\nTitle: " + note.getTitle() + "\nContents: " + note.getContents());
                    return;
                } else {
                    co.prompt(result);
                }
            }
        }
    }
    private boolean gettingNoteInput(String prompt, Consumer<String> setter){
        System.out.println(prompt);
        String input = ci.input();
        if (ci.checkIfQuit(input)) {
            return false;
        }
        setter.accept(input);
        return true;
    }

    private void editNote(Note n) throws SQLException {
        Consumer<String> titleSetter = n::setTitle;
        Consumer<String> textSetter = n::setContents;
        boolean editTitle = editField(askTitle, "Input your new title:\n", titleSetter);
        boolean editText = editField(askContents, "Input your new note contents:\n", textSetter);
        if (!editTitle && !editText) {
            System.out.println("No edits have been made to the post.");
            return;
        }
        System.out.println("Your note has been updated: \n" + co.getNotePostString(n, -1, true, false, false));
        appManager.handleUserAction( NoteAction.EDIT, n);
    }

    private boolean editField(String query, String prompt, Consumer<String> setter) {
        System.out.println(query + yesno + um.returnToMain);
        String choice = ci.input();
        if (ci.checkIfQuit(choice)) {
            return false;
        }
        if (!ci.checkConfirm(choice, query)) {
            return false;
        }
        System.out.println(prompt);
        String input = ci.input();
        setter.accept(input);
        return true;
    }

private void deleteNote(Note n) throws SQLException {
    System.out.println("Are you sure that you want to delete this note?\n");
    System.out.println(yesno);
    String choice = ci.lowCaseInput();
    if (choice.equalsIgnoreCase("1")) {
        Prompts response = appManager.handleUserAction(NoteAction.REMOVE, n);
        if (response == Prompts.OK) {
            System.out.println("The note has been deleted.");
        } else {
            co.prompt(Prompts.ERROR);
        }
    }
}

private void deleteAllNotes() throws SQLException {
    while (true) {
        System.out.println(confirmDelete + um.returnToMain + yesno);
        String choice = ci.input();
        if (ci.checkIfQuit(choice)) {
            return;
        }
        if (ci.checkConfirm(choice, confirmDelete)) {
            System.out.println("--Removing all notes:");
            Prompts response = appManager.handleUserAction(NoteAction.REMOVE_ALL, new Note(-1, "", "", null));
            if (response == Prompts.OK) {
                System.out.println("All of your notes have been removed.");
                return;
            }
        }
    }
}
private Map<String, SQLRunnable> getNoteMenuOptions(Note n, List<Note> notes) {
    Map<String, SQLRunnable> noteMenuOptions = new HashMap<>();
    noteMenuOptions.put("1", () -> editNote(n));
    noteMenuOptions.put("2", () -> deleteNote(n));
    noteMenuOptions.put("b", () -> showAllNotes(notes));
    return noteMenuOptions;
}
private final String notesmenu = "You have saved the following notes.\n";
private final String notesMenuPrompt = "\nEnter the number of a post to edit or delete it.\nEnter 'clear' if you want to delete all posts.\nEnter 'x' to return to the main menu.\n";
private final String notePrompt = "Would you like to:\n- 1: Edit, or \n- 2: Delete\nthe note below? \n- Type 'B' to return to all of your posts\n- or 'X' to return to the main menu.\nSubmit your choice:\n";
private final String askTitle = "Would you like to edit your title?\n";
private final String askContents = "Would you like to edit your note contents?\n";
private final String yesno = "Type '1' for YES, '2' for NO:\n";
private final String confirmDelete = "Are you sure that you want to delete all of your notes?\n";
}