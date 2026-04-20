package Control;

import Model.DatabaseRelay;

import java.sql.SQLException;

public class AppManager {
    private PasswordService passwordService = new PasswordService();
    private DatabaseRelay databaseRelay = new DatabaseRelay(this);
    private enum InputType{USERNAME, PASSWORD, NOTE_TITLE, NOTE_TEXT}

    public AppManager() throws SQLException {
    }

    private void validateLogin(String username, String passwordInput) throws SQLException {
        boolean userExists = databaseRelay.checkAvailability(username);
        if (userExists) {
            String passwordHash = databaseRelay.getPasswordHash(username);
            boolean result = passwordService.validatePassword(passwordInput, passwordHash);
            if (!result) {
                //prompt
            } else {
                //show menu
            }
        }
        else {
            //prompt
        }
    }

    private void validateNewUser(String username, String passwordInput) throws SQLException {
        if (lengthValidation(username, passwordInput)) {
            boolean availableName = databaseRelay.checkAvailability(username);
            if (availableName) {
                String passwordHash = passwordService.hashPassword(passwordInput);
                databaseRelay.addNewUser(username, passwordHash);
            }
            else {
                //prompt
            }
        }
    }

    private  boolean lengthValidation(String username, String password){
        boolean validInput = true;
        if (checkTooLong(username, InputType.USERNAME)){
            validInput = false;
            //prompt
        }
        else if (checkTooShort(username, InputType.USERNAME)){
            validInput = false;
            //prompt
        }
        if (checkTooLong(password, InputType.PASSWORD)){
            validInput = false;
            //prompt
        }
        else if (checkTooShort(password, InputType.PASSWORD)){
            validInput = false;
            //prompt
        }
        return validInput;
    }
    private boolean checkTooShort(String userinput, InputType inputType){
        int minSize = 0;
        switch (inputType){
            case USERNAME -> minSize = 2;
            case PASSWORD -> minSize = 6;
            case NOTE_TEXT, NOTE_TITLE -> minSize = 1;
        }
        return userinput.length()  < minSize;
    }
    private boolean checkTooLong(String userinput, InputType inputType){
        int maxSize = 0;
        switch (inputType){
            case USERNAME -> maxSize = 15;
            case PASSWORD -> maxSize = 20;
            case NOTE_TITLE -> maxSize = 75;
            case NOTE_TEXT -> maxSize = 500;
        }
        return userinput.length() > maxSize;
    }
}
