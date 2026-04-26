package View;

import Control.Event;
import Control.NoteAction;
import Control.Prompts;
import Model.LogPost;
import Model.Note;
import Model.NoteLog;
import Model.User;
import com.mysql.cj.log.Log;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class ConsoleIO {
    private Scanner scan;

    public ConsoleIO(Scanner scan){
        this.scan = scan;
    }

    public String input(){
        return scan.nextLine().toLowerCase().trim();
    }

    public void prompt(Prompts prompt) {
        System.out.println(prompt.toString());
    }

    public boolean checkIfQuit(String input) {
        return input.equalsIgnoreCase("x");
    }

    public void promptInvalid(){
        System.out.println(invalidChoice);
    }
    public String promptVerify() {
        System.out.println(passwordPrompt);
        return scan.nextLine().trim();
    }
    public boolean checkIfValidChoice(List<String> options, String userInput){
        return options.contains(userInput);
    }
    public boolean checkConfirm(String input, String prompt) {
        if (input.equals("1")) {
            return true;
        } else if (input.equals("2")) {
            return false;
        } else {
            System.out.println(invalidChoice);
            System.out.println(prompt);
            return checkConfirm(scan.nextLine(), prompt);
        }
    }
    public boolean checkValidAction(Prompts response){
        return response == Prompts.OK;
    }
    public boolean validUserSelection(int userIdInput, List<User> users){
        return users.stream().anyMatch(user -> user.getId() == userIdInput);
    }
    public User getUserFromList(int input, List<User> users){
        return users.stream().filter(u -> u.getId() == input).findAny().orElse(null);
    }
    public boolean validNoteSelection (int noteIdInput, List<Note> notes){
        return notes.stream().anyMatch(note -> note.getId() == noteIdInput);
    }

    public List<User> getUsers(Event event){
        if (event.getData() instanceof List list && !list.isEmpty() && list.getFirst() instanceof User) {
            return  (List<User>) event.getData();
        }
        return Collections.emptyList();
    }
    public String getNotePostString(Note n, int index, boolean allInfo, boolean allNotes, boolean isAdmin){
        StringBuilder post = new StringBuilder();
        String numberLabel = isAdmin ? "Note id: " : "Note nr. ";
        int noteNumber = allNotes ? n.getId() : index ;
        if (index >-1) {
            post.append(numberLabel);
            post.append(noteNumber);
            post.append(":");
        }
        if (allNotes) {
            post.append("User: ");
            post.append(n.getUsername());
        }
        post.append("\nDate: ");
        post.append(n.getSubmitDate());
        post.append("\nTitle: ");
        post.append(n.getTitle());
        if (allInfo) {
            post.append("\nContents: ");
            post.append(n.getContents());
        }
        post.append("\n");
        return post.toString();
    }

    public String getUserPostString(User u, boolean allInfo){
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

    public String getLogPostString(LogPost log){
        StringBuilder post = new StringBuilder();
        post.append("Date of action: ");
        post.append(log.getDate());
        post.append("\nAction performed by user with id: ");
        post.append(log.getUserId());
        post.append("\nStatus: ");
        post.append(log.getLoginStatus());
        post.append("\n");
        return post.toString();
    }

    public String getNoteLogString(NoteLog log){
        StringBuilder post = new StringBuilder();
        post.append("Action performed by user with id: ");
        post.append(log.getActorUserId());
        post.append("\nDate of action: ");
        post.append(log.getDate());
        post.append("\nNote id:");
        post.append(log.getNoteId());
        post.append("\nAction: ");
        post.append(log.getNoteAction());
        post.append("\n");
        return post.toString();
    }

    private final String invalidChoice = "You haven't submitted av valid choice. Please try again.\n";
    private final String passwordPrompt = "Please submit your password to confirm:\n";
}