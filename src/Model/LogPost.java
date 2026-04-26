package Model;

import Control.LoginStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogPost {
    private LocalDateTime date;
    private int userId;
    public LoginStatus loginStatus;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm");

    public LogPost(LocalDateTime date, int userId, String status) {
        this.date = date;
        this.userId = userId;
        setLoginStatus(status);
    }

    public String getDate() {
        return formatter.format(date);
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LoginStatus getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String status) {
        LoginStatus l = LoginStatus.SUCCESS;
        if (status.equalsIgnoreCase("unknown_user")){
            l = LoginStatus.UNKNOWN_USER;
        }
        if (status.equalsIgnoreCase("fail")){
            l = LoginStatus.FAIL;
        }
        this.loginStatus = l;
    }
}
