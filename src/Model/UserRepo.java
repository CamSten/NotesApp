package Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepo {
    //Needs admin validation
    //needs hash methods
    private static Connection c;

    public UserRepo() {
        this.c = c;
    }

    public String getPasswordHash(String username) throws SQLException {
        PreparedStatement s = c.prepareStatement("SELECT * FROM AppUser where username = ?");
        s.setString(1, username);
        String passwordHash = "";
        ResultSet rs = null;
        while (rs.next()){
           passwordHash = rs.getString("passwordHash");
        }
        return passwordHash;
    }

    public boolean checkNameAvailability(String requestedName) throws SQLException {
        PreparedStatement s = c.prepareStatement("SELECT * FROM AppUser where username = ?");
        s.setString(1, requestedName);
        List<String> results = new ArrayList<>();
        ResultSet rs = null;
        while (rs.next()){
            results.add(rs.getString("username"));
        }
        return (results.isEmpty());
    }

    public boolean addNewUser(String username, String passwordHash) throws SQLException {
        CallableStatement s = c.prepareCall("CALL (?, ?)");
        s.setString(1, "username");
        s.setString(2, "passwordHash");
        return s.execute();
    }
    public List<User> getUsers() throws SQLException {
        List<User> allUsers = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement s = c.prepareStatement("SELECT * FROM AppUser");
        while (rs.next()){
            String userName = rs.getString("username");
        }
        return allUsers;
    }
}
