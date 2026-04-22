package Model;

public class User {
    private String username;
    private int id;
    public enum Role {
        USER, ADMIN}
    private Role role;

    public User(String username, int id, Role role) {
        this.username = username;
        this.id = id;
        this.role = role;
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
}
