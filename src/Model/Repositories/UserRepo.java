package Model.Repositories;

import Control.Enums.LoginStatus;
import Model.DataObjects.User;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import static Model.DataObjects.User.Role.USER;

public class UserRepo {
    private static Connection c;

    public UserRepo(Connection c) {
        this.c = c;
    }

    public void logLoginAttempt(int userId, LoginStatus status) throws SQLException {
        CallableStatement s = c.prepareCall("CALL logLoginAttempt(?, ?)");
        s.setInt(1, userId);
        s.setString(2, String.valueOf(status));
        s.execute();
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

    public int getUserIdForAdminUse(String username) throws SQLException {
        PreparedStatement s = c.prepareStatement("SELECT * from AppUser where username = ?");
        s.setString(1, username);
        int userId = -1;
        ResultSet rs = s.executeQuery();
        while (rs.next()){
            userId = rs.getInt("id");
        }
        return userId;
    }
    public boolean checkIfAdmin(int userId) throws SQLException {
        CallableStatement s = c.prepareCall("CALL checkIfAdmin(?, ?)");;
        s.setInt(1, userId);
        s.execute();
        boolean result = s.getBoolean(2);
        return result;
    }
    public void addNewUser(String username, String passwordHash) throws SQLException {
        CallableStatement s = c.prepareCall("CALL newAppUserValidation (?, ?)");
        s.setString(1, username);
        s.setString(2, passwordHash);
        s.execute();
    }

    public void saveNewPassword(int userId, String newPasswordHash) throws SQLException {
        CallableStatement s = c.prepareCall("CALL changePassword (?, ?)");
        s.setInt(1, userId);
        s.setString(2, newPasswordHash);
        s.execute();
    }
    public List<User> getUsers() throws SQLException {
        List<User> allUsers = new ArrayList<>();
        PreparedStatement s = c.prepareStatement("SELECT * FROM AppUser");
        ResultSet rs = s.executeQuery();
        while (rs.next()){
            String userName = rs.getString("username");
            int userId = rs.getInt("id");
            String role = rs.getString("role");
            LocalDateTime regDate = rs.getTimestamp("regDate").toLocalDateTime();
            User.Role userRole = USER;
            if (role.equals("admin")){
                userRole = User.Role.ADMIN;
            }
            allUsers.add(new User(userName, userId, userRole, regDate));
        }
        return allUsers;
    }
}