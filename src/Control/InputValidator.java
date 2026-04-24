package Control;

import java.util.ArrayList;
import java.util.List;

public class InputValidator {
    public enum InputType {USERNAME, PASSWORD, NOTE, NOTE_TITLE, NOTE_TEXT}

    public Prompts validate(InputType inputType, List<String> inputValues){
        List<Prompts> results = new ArrayList<>();
        Prompts result = Prompts.OK;
        switch (inputType) {
            case PASSWORD, USERNAME -> results = List.of(
                    checkTooShort(inputValues.getFirst(), inputType),
                    checkTooLong(inputValues.getFirst(), inputType));
            case NOTE -> results = List.of(
                    checkTooLong(inputValues.getFirst(), InputType.NOTE_TITLE),
                    checkTooShort(inputValues.getFirst(), InputType.NOTE_TITLE),
                    checkTooLong(inputValues.getLast(), InputType.NOTE_TEXT),
                    checkTooShort(inputValues.getLast(), InputType.NOTE_TEXT));
        }
        return results.stream().filter(p -> p != result).findFirst().orElse(result);
    }
    public Prompts lengthValidation(String username, String password){
        if (checkTooLong(username, InputType.USERNAME) != Prompts.OK) {
            return Prompts.LONG_NAME;
        }
        else if (checkTooShort(username, InputType.USERNAME) != Prompts.OK){
            return Prompts.SHORT_NAME;
        }
        if (checkTooLong(password, InputType.PASSWORD) != Prompts.OK){
            return Prompts.LONG_PASS;
        }
        else if (checkTooShort(password, InputType.PASSWORD) != Prompts.OK){
            return Prompts.SHORT_PASS;
        }
        return Prompts.OK;
    }
    public Prompts checkTooShort(String userinput, InputType inputType){
        switch (inputType){
            case USERNAME -> {
                if( userinput.length() < 2){
                    return Prompts.SHORT_NAME;
                }
            }
            case PASSWORD -> {
                if (userinput.length() < 14){
                    return Prompts.SHORT_PASS;
                }
            }
            case NOTE_TITLE -> {
                if (userinput.isEmpty()){
                    return Prompts.SHORT_TITLE;
                }
            }
            case NOTE_TEXT ->{
                if (userinput.isEmpty()){
                    return Prompts.SHORT_TEXT;
                }
            }
            default -> {
                return Prompts.ERROR;
            }
        }
        return Prompts.OK;
    }
    public Prompts checkTooLong(String userinput, InputType inputType){
        switch (inputType){
            case USERNAME -> {
                if (userinput.length() > 15){
                    return Prompts.LONG_NAME;
                }
            }
            case PASSWORD -> {
                if (userinput.length() > 30){
                    return Prompts.LONG_PASS;
                }
            }
            case NOTE_TITLE -> {
                if (userinput.length() > 75){
                    return Prompts.LONG_TITLE;
                }
            }
            case NOTE_TEXT -> {
                if (userinput.length() > 500){
                    return Prompts.LONG_TEXT;
                }
            }
            default -> {
                return Prompts.ERROR;
            }
        }
        return Prompts.OK;
    }
}