package View;

public class LoginData {
    String usernameInput;
    String passwordInput;
    String confirmPassInput;
    boolean nameIsSet;

    public LoginData() {
    }

    public boolean isNameSet() {
        return nameIsSet;
    }

    public void resetName(){
        this.usernameInput = "";
        this.nameIsSet = false;
    }
    public void resetPass(){
        setPasswordInput("");
        setConfirmPassInput("");
    }

    public void thisNameIsSet() {
        this.nameIsSet = true;
    }

    public String getUsernameInput() {
        return usernameInput;
    }

    public void setUsernameInput(String usernameInput) {
        this.usernameInput = usernameInput;
        this.nameIsSet = true;
    }

    public String getPasswordInput() {
        return passwordInput;
    }

    public void setPasswordInput(String passwordInput) {
        this.passwordInput = passwordInput;
    }

    public String getConfirmPassInput() {
        return confirmPassInput;
    }

    public void setConfirmPassInput(String confirmPassInput) {
        this.confirmPassInput = confirmPassInput;
    }
}