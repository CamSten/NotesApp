package Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class User {
    private String username;
    private int id;
    public enum Role {
        USER, ADMIN}
    private Role role;
    private boolean verified;
    int incorrectAttempts = 0;
    private LocalDateTime regDate;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm");


    public User(String username, int id, Role role, LocalDateTime regDate) {
        this.username = username;
        this.id = id;
        this.role = role;
        this.regDate = regDate;
    }
    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public String getRegDate() {
        return formatter.format(regDate);
    }

    public void setRegDate(LocalDateTime regDate) {
        this.regDate = regDate;
    }

    public boolean isVerified() {
        return verified;
    }
    public void setVerified(boolean verified) {
        this.verified = verified;
    }
    public int getIncorrectAttempts(){
        return incorrectAttempts;
    }
    public void setIncorrectAttempts(int number) {
        System.out.println("number is: " + number);
        if (number == 0) {
            this.incorrectAttempts = 0;
        } else {
            incorrectAttempts += 1;
        }
    }


}
