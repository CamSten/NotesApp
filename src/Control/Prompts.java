package Control;

public enum Prompts {
    SHORT_PASS ("The password is too short, please try again.\nThe password needs to be at least 14 characters, with at least one letter, one digit and one special character"),
    LONG_PASS ("The password is too long, please try again."),
    SHORT_NAME ("The user name is too short, please try again."),
    LONG_NAME ("The user name is too long, please try again."),
    SHORT_TITLE ("The title you've submitted is too short, please try again."),
    LONG_TITLE ("The title you've submitted is too long, please try again."),
    SHORT_TEXT ("The text you've submitted is too short, please try again."),
    LONG_TEXT ("The text you've submitted is too long, please try again."),
    NAME_TAKEN ("The user name is already taken. Please try a different name."),
    NO_SUCH_USER ("The user name has not been found. Please try a different name."),
    WRONG_PASS ("The password you have submitted is incorrect."),
    NEW_PASS_OK("Your new password has been saved."),
    LOGIN_OK("Login successful."),
    NOTE_OK("Note ok"),
    LENGTH_OK("Length ok"),
    ERROR("ERROR, something went wrong."),
    AWAIT_DATA("Data retrieval has been initiated.");

    private final String prompt;

    Prompts(String input){
        prompt = input;
    }
    @Override
    public String toString() {
        return prompt;
    }
}