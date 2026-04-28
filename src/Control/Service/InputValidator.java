package Control.Service;

import Control.Enums.Prompts;
import java.util.*;

public class InputValidator {
    public enum InputType {USERNAME, PASSWORD, NOTE, NOTE_TITLE, NOTE_TEXT}

    public Prompts validateLength(InputType inputType, List<String> inputValues){
        List<Prompts> results = new ArrayList<>();
        Prompts result = Prompts.OK;
        if (inputType == InputType.NOTE_TITLE || inputType == InputType.NOTE_TEXT){
            results = List.of(checkTooShort(inputValues.getFirst(), inputType), checkTooLong(inputValues.getFirst(), inputType));
        }
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
        return results.stream().filter(p -> p != result).findAny().orElse(result);
    }

    public Prompts checkTooShort(String userinput, InputType inputType){
        return switch (inputType){
            case USERNAME -> userinput.length() < 2 ? Prompts.SHORT_NAME : Prompts.OK;
            case PASSWORD -> userinput.length() < 14 ? Prompts.SHORT_PASS : Prompts.OK;
            case NOTE_TITLE -> userinput.isEmpty() ? Prompts.SHORT_TITLE :  Prompts.OK;
            case NOTE_TEXT -> userinput.isEmpty() ? Prompts.SHORT_TEXT : Prompts.OK;
            default ->  Prompts.ERROR;
        };
    }
    public Prompts checkTooLong(String userinput, InputType inputType){
        return switch (inputType){
            case USERNAME -> userinput.length() > 15 ? Prompts.LONG_NAME : Prompts.OK;
            case PASSWORD -> userinput.length() > 30 ? Prompts.LONG_PASS : Prompts.OK;
            case NOTE_TITLE -> userinput.length() > 75 ? Prompts.LONG_TITLE : Prompts.OK;
            case NOTE_TEXT ->  userinput.length() > 500 ? Prompts.LONG_TEXT : Prompts.OK;
            default -> Prompts.ERROR;
        };
    }
}