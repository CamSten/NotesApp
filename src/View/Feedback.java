package View;

import Control.Prompts;
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
    private final String invalidChoice = "You haven't submitted av valid choice. Please try again.\n";
}
