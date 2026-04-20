package Control;

public enum Prompts {
    SHORT_PASS ("The password is too short, please try again."),
    LONG_PASS ("The password is too long, please try again."),
    SHORT_NAME ("The user name is too short, please try again."),
    LONG_NAME ("The user name is too long, please try again."),
    SHORT_TITLE ("The title you've submitted is too short, please try again."),
    LONG_TITLE ("The title you've submitted is too long, please try again."),
    SHORT_TEXT ("The text you've submitted is too short, please try again."),
    LONG_TEXT ("The text you've submitted is too long, please try again."),
    NAME_TAKEN ("The user name is already taken. Please try a different name."),
    WRONG_PASS ("The password you have submitted is incorrect.");

    private final String prompt;

    private Prompts(String input){
        prompt = input;
    }
    @Override
    public String toString() {
        return super.toString();
    }
}

