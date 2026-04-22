package Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepo {
    private static Connection c;

    public UserRepo(Connection c) {
        this.c = c;
    }

    public String getPasswordHash(String username) throws SQLException {
        PreparedStatement s = c.prepareStatement("SELECT * FROM AppUser where username = ?");
        s.setString(1, username);
        String passwordHash = "";
        ResultSet rs = s.executeQuery();
        while (rs.next()){
           passwordHash = rs.getString("passwordHash");
        }
        return passwordHash;
    }

    public boolean checkNameAvailability(String requestedName) throws SQLException {
        PreparedStatement s = c.prepareStatement("SELECT * FROM AppUser where username = ?");
        s.setString(1, requestedName);
        List<String> results = new ArrayList<>();
        ResultSet rs = s.executeQuery();
        while (rs.next()){
            results.add(rs.getString("username"));
        }
        return (results.isEmpty());
    }

    public int getUserId(String thisUsername, String passwordHash) throws SQLException {
        CallableStatement s = c.prepareCall("CALL getUserId(?, ?, ?)");
        s.setString(1, thisUsername);
        s.setString(2, passwordHash);
        s.execute();
        return s.getInt(3);
    }
    public boolean checkIfAdmin(int userId) throws SQLException {
        CallableStatement s = c.prepareCall("CALL checkIfAdmin(?, ?)");;
        s.setInt(1, userId);
        s.execute();
        boolean result = s.getBoolean(2);
        return result;
    }
    public boolean addNewUser(String username, String passwordHash) throws SQLException {
        CallableStatement s = c.prepareCall("CALL addNewUser (?, ?)");
        s.setString(1, username);
        s.setString(2, passwordHash);
        return s.execute();
    }

    public boolean saveNewPassword(String username, String newPasswordHash) throws SQLException {
        CallableStatement s = c.prepareCall("CALL changePassword (?, ?)");
        s.setString(1, username);
        s.setString(2, newPasswordHash);
        return s.execute();

    }
    public List<String> getUsers() throws SQLException {
        List<String> allUsers = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement s = c.prepareStatement("SELECT * FROM AppUser");
        while (rs.next()){
            String userName = rs.getString("username");
            allUsers.add(userName);
        }
        return allUsers;
    }
}