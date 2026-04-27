package View.UserIO;

import Control.AppManager;
import Control.Enums.NoteAction;
import Control.Enums.Prompts;
import Model.DataObjects.Note;
import View.ConsoleInput;
import View.ConsoleOutput;
import java.sql.SQLException;
import java.time.LocalDateTime;
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

    public void readNotes() throws SQLException {
        appManager.handleUserAction(NoteAction.READ, null);
    }

    public void showAllNotes(List<Note> notes) throws SQLException {
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
            int inputId = ci.getNoteIdFromList(ci.parseInput(choice), notes);
            if (inputId == -1) {
                System.out.println("The note id does not exists. Try again.");
                continue;
            }
            Map<String, SQLRunnable> options = getNoteMenu(ci.parseInput(choice), notes);
            SQLRunnable action = options.get(choice);
            if (action != null) {
                action.run();
                return;
            }
        }
    }
    public void informEmpty(){
        System.out.println("You haven't added any notes yet.");
    }

    public void showNotesMenu(int inputId, List<Note> notes) throws SQLException {
        while (true){
            Note n = notes.get(inputId - 1);
            System.out.println(notePrompt);
            System.out.println(co.getNotePostString(n, inputId, true, false, false));
            System.out.println(submit);
            String choice = ci.lowCaseInput();
            if (ci.checkIfQuit(choice)){
                return;
            }
            Map<String, SQLRunnable> menu = getNoteSubMenu(n, notes);
            SQLRunnable action = menu.get(choice);
            if (action == null){
                co.promptInvalid();
                continue;
            }
            action.run();
            return;
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
                appManager.handleUserAction(NoteAction.ADD, note);
                System.out.print("Your new note post has been added:\nTitle: " + note.getTitle() + "\nContents: " + note.getContents());
                return;
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

    public void editNote(Note n) throws SQLException {
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
        if (ci.checkIfQuit(choice) || !ci.checkConfirm(choice, query)) {
            return false;
        }
        System.out.println(prompt);
        String input = ci.input();
        setter.accept(input);
        return true;
    }

    public void deleteNote(Note n) throws SQLException {
        System.out.println("Are you sure that you want to delete this note?\n");
        System.out.println(yesno);
        String choice = ci.lowCaseInput();
        if (choice.equalsIgnoreCase("1")) {
            appManager.handleUserAction(NoteAction.REMOVE, n);
            return;
        }
        co.prompt(Prompts.ERROR);
    }

    public void deleteAllNotes() throws SQLException {
        while (true) {
            System.out.println(confirmDelete + um.returnToMain + yesno);
            String choice = ci.input();
            if (ci.checkIfQuit(choice)) {
                return;
            }
            if (ci.checkConfirm(choice, confirmDelete)) {
                System.out.println("--Removing all notes:");
                appManager.handleUserAction(NoteAction.REMOVE_ALL, new Note(-1, "", "", null));
                return;
            }
        }
    }
    private Map<String, SQLRunnable> getNoteSubMenu(Note n, List<Note> notes) {
        List<String> input = List.of("1", "2", "3");
        List<SQLRunnable> toPerform = List.of(() -> editNote(n), () -> deleteNote(n), () -> showAllNotes(notes));
        MenuOptions menuOptions = new MenuOptions(input, toPerform);
        return menuOptions.createMenu();
    }
    private Map<String, SQLRunnable> getNoteMenu(int inputId, List<Note> notes){
        List<String> input = List.of("clear", String.valueOf(inputId));
        List<SQLRunnable> toPerform = List.of(this::deleteAllNotes,() -> showNotesMenu(inputId, notes));
        MenuOptions menuOptions = new MenuOptions(input, toPerform);
        return menuOptions.createMenu();
    }
    private final String notesmenu = "You have saved the following notes.\n";
    private final String notesMenuPrompt = "\nEnter the number of a post to edit or delete it.\nEnter 'clear' if you want to delete all posts.\nEnter 'x' to return to the main menu.\n";
    private final String notePrompt = "Would you like to:\n- 1: Edit this post\n- 2: Delete this post\n- 3: Return to all of your posts\nPress 'X' to return to the main menu.\n";
    private final String askTitle = "Would you like to edit your title?\n";
    private final String askContents = "Would you like to edit your note contents?\n";
    private final String yesno = "Type '1' for YES, '2' for NO:\n";
    private final String submit = "Submit your choice:\n";
    private final String confirmDelete = "Are you sure that you want to delete all of your notes?\n";
}