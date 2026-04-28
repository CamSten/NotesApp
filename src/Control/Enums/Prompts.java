package Control.Enums;

public enum Prompts {
    SHORT_PASS ("The password is too short, please try again.\nThe password needs to consist of at least 14 characters."),
    LONG_PASS ("The password is too long, please try again."),
    SHORT_NAME ("The user name is too short, please try again."),
    LONG_NAME ("The user name is too long, please try again."),
    SHORT_TITLE ("The title you've submitted is too short, please try again."),
    LONG_TITLE ("The title you've submitted is too long, please try again."),
    SHORT_TEXT ("The text you've submitted is too short, please try again."),
    LONG_TEXT ("The text you've submitted is too long, please try again."),
    NAME_TAKEN ("The user name is already taken. Please try a different name."),
    NO_SUCH_USER ("The user name has not been found. Please try a different name."),
    NO_SUCH_NOTE("The note has not been found"),
    WRONG_PASS ("The password you have submitted is incorrect."),
    PASS_MISMATCH("The password inputs do not match."),
    NEW_PASS_OK("Your new password has been saved."),
    OK("Ok"),
    ERROR("ERROR, something went wrong.");

    private final String prompt;

    Prompts(String input){
        prompt = input;
    }
    @Override
    public String toString() {
        return prompt;
    }
}