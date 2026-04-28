package Control.Enums;

public enum LoginStatus{
    SUCCESS(1),
    FAIL(2),
    UNKNOWN_USER(3);

    private final int status;

    LoginStatus(int input){
        status = input;
    }
    public static LoginStatus getStatus(int input){
        if (input == 1){
            return SUCCESS;
        }
        if (input == 2){
            return FAIL;
        }
        if (input == 3){
            return UNKNOWN_USER;
        }
        return null;
    }
}