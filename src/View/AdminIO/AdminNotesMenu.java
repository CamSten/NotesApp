package View.AdminIO;

import Control.*;
import Control.Enums.*;
import Model.DataObjects.*;
import View.*;
import java.sql.SQLException;
import java.util.List;

public class AdminNotesMenu {
    private final ConsoleOutput co;
    private final ConsoleInput ci;
    private final AdminMenu am;
    private final AppManager appManager;

    public AdminNotesMenu(ConsoleOutput co, ConsoleInput ci, AdminMenu am, AppManager appManager){
        this.am = am;
        this.co = co;
        this.ci = ci;
        this.appManager = appManager;
    }

    public void seeNotes() throws SQLException {
        boolean completed = true;
        while (completed) {
            co.printHeader("Notes");
            System.out.println(noteMenuPrompt + am.returnToMain);
            String choice = ci.lowCaseInput();
            if (ci.checkIfQuit(choice)) {
                return;
            }
            switch (choice) {
                case "1" -> appManager.handleAdminAction(AdminAction.SEE_ALL_NOTES, -1);
                case "2" -> {
                    System.out.println(am.userNamePrompt + "notes.");
                    String input = ci.input();
                    int userId = appManager.getUserIdForAdminUse(input);
                    appManager.handleAdminAction(AdminAction.SEE_NOTES, userId);
                }
                case "3" -> seeNoteLogs();
                default -> {
                    co.promptInvalid();
                    completed = false;
                }
            }
        }
    }
    public void displayNotes(List<Note> notes, boolean allNotes, boolean allInfo) throws SQLException {
        if (notes.isEmpty()) {
            System.out.println(noNotes);
            return;
        }
        String prompt = allNotes ? notesIntroAll : notesIntroUser + " " + notes.getFirst().getUsername();
        System.out.println(prompt);
        for (Note n : notes) {
            System.out.println(co.getNotePostString(n, n.getId(), allInfo, allNotes, true));
        }
        while (true) {
            System.out.println(notesPrompt + am.returnToMain);
            String input = ci.lowCaseInput();
            if (ci.checkIfQuit(input)){
                return;
            }
            if(ci.validNoteSelection(ci.parseInput(input), notes)){
                displayNotesSubmenu(input, notes);
                return;
            }
            co.promptInvalid();
        }
    }
    private void displayNotesSubmenu(String choice, List<Note> notes) throws SQLException {
        int noteIdInput = ci.parseInput(choice);
        Note thisNote = notes.stream().filter(n -> n.getId() == noteIdInput).findFirst().orElse(null);
        if (ci.checkIfQuit(choice)) {
            return;
        }
        while (true) {
            if (thisNote != null) {
                System.out.println(co.getNotePostString(thisNote, -1, true, true, true));
                System.out.println(deletePrompt + am.returnToMain);
                String subChoice = ci.lowCaseInput();
                if (subChoice.equals("1")) {
                    appManager.handleAdminAction(AdminAction.DELETE_NOTE, thisNote.getId());
                    return;
                } else if (ci.checkIfQuit(subChoice) || subChoice.equals("2")) {
                    return;
                }
            }
            co.promptInvalid();
        }
    }
    public void showNoteLogs(List<NoteLog> noteLogs){
        co.printHeader("List of note logs:");
        for (NoteLog log : noteLogs){
            System.out.println(co.getNoteLogString(log));
        }
    }
    private void showNoteLogSubmenu(String choice) throws SQLException {
        if (ci.checkIfQuit(choice)){
            return;
        }
        while (true) {
            System.out.println(noteLogMenuPrompt + am.returnToMain + submit);
            String input = ci.lowCaseInput();
            if (ci.checkIfQuit(input)){
                return;
            }
            switch (input){
                case "1" -> {
                    appManager.handleAdminAction(AdminAction.SEE_ALL_NOTE_LOGS, -1);
                    return;
                }
                case "2" -> {
                    System.out.println(am.userNamePrompt + "note logs.\n" + am.returnToMain);
                    String username = ci.lowCaseInput();
                    if (ci.checkIfQuit(username)){
                        return;
                    }
                    int userId = appManager.getUserIdForAdminUse(username);
                    if (userId != -1){
                        appManager.handleAdminAction(AdminAction.SEE_USER_NOTE_LOGS, userId);
                        return;
                    }
                }
                default -> co.promptInvalid();
            }
        }
    }
    private void seeNoteLogs() throws SQLException {
        while (true) {
            System.out.println(noteLogMenuPrompt + am.returnToMain + submit);
            String choice = ci.lowCaseInput();
            if (ci.checkIfQuit(choice)){
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
                default -> co.promptInvalid();
            }
        }
    }
    private final String noteMenuPrompt = "-- Options:\n- 1: See all notes\n- 2: See all notes for a specific user\n- 3: See note logs\n";
    private final String notesIntroUser = "-- The following notes have been submitted for user: ";
    private final String notesIntroAll = "-- The following notes have been submitted:\n";
    private final String noNotes = "-- No notes have been found.";
    private final String noteLogMenuPrompt = "-- Would you like to:\n- 1: See all note logs\n- 2: See note logs for a specific user\n";
    private final String notesPrompt = "-- Enter the number of a note post for more details.\n";
    private final String submit = "Submit your choice:\n";
    private final String deletePrompt = "-- Enter 1 if you would like to delete the note.\n";
}