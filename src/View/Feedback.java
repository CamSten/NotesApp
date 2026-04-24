package View;

import Control.Event;
import Control.Prompts;
import Model.Note;
import Model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Feedback {
    private Scanner scan;

    public Feedback(Scanner scan){
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

    public List<Note> getNotes(Event event) {
        if (event.getData() instanceof List list && !list.isEmpty() && list.getFirst() instanceof Note) {
           return  (List<Note>) event.getData();
        }
        return Collections.emptyList();
    }
    public List<User> getUsers(Event event){
        if (event.getData() instanceof List list && !list.isEmpty() && list.getFirst() instanceof User) {
            return  (List<User>) event.getData();
        }
        return Collections.emptyList();
    }
    private final String invalidChoice = "You haven't submitted av valid choice. Please try again.\n";
}
