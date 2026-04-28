package View;

import Control.Event;
import Control.Enums.Prompts;
import Model.DataObjects.*;
import java.util.*;

public class ConsoleInput {
    private final Scanner scan;

    public ConsoleInput(Scanner scan){
        this.scan = scan;
    }

    public String lowCaseInput(){
        return scan.nextLine().toLowerCase().trim();
    }
    public String input(){
        return scan.nextLine().trim();
    }

    public int parseInput(String input){
        try {
            return Integer.parseInt(input);
        }
        catch (NumberFormatException e){
            return -1;
        }
    }
    public String promptVerify() {
        System.out.println(passwordPrompt);
        return scan.nextLine().trim();
    }
    public boolean checkIfQuit(String input) {
        return input.equalsIgnoreCase("x");
    }
    public boolean checkIfValidChoice(List<String> options, String userInput){
        return options.contains(userInput);
    }
    public boolean checkConfirm(String input, String prompt) {
        while (true) {
            if (input.equals("1")) {
                return true;
            } else if (input.equals("2")) {
                return false;
            } else {
                System.out.println(invalidChoice);
                System.out.println(prompt);
                input = lowCaseInput();
            }
        }
    }
    public boolean checkValidAction(Prompts response){
        return response == Prompts.OK;
    }
    public User getUserFromList(int input, List<User> users){
        return users.stream().filter(u -> u.getId() == input).findAny().orElse(null);
    }
    public boolean validNoteSelection (int noteIdInput, List<Note> notes){
        return notes.stream().anyMatch(note -> note.getId() == noteIdInput);
    }
    public int getNoteIdFromList(int userInput, List<Note> notes){
        if (userInput == -1){
            return userInput;
        }
        return (userInput-1 < notes.size()) ? notes.get(userInput-1).getId() : -1;
    }

    public List<User> getUsersFromEvent(Event event){
        if (event.getData() instanceof List list && !list.isEmpty() && list.getFirst() instanceof User) {
            return  (List<User>) event.getData();
        }
        return Collections.emptyList();
    }
    private final String passwordPrompt = "Please submit your password to confirm:\n";
    private final String invalidChoice = "You haven't submitted av valid choice. Please try again.\n";
}