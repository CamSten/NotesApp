package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepo {
    //Needs admin validation
    //needs hash methods
    private static Connection c;

    public UserRepo() {
        this.c = c;
    }

    private boolean validateLogin(String username){
        boolean existingName = false;
        boolean validPassword = false;

        return (existingName && validPassword);

    }
    private boolean validateNewUser(String username){
        boolean availableName = false;
        boolean validName = false;
        boolean validPassword = false;

        return (availableName && validName && validPassword);
    }

    private List<User> getUsers() throws SQLException {
        List<User> allUsers = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement s = c.prepareStatement("SELECT * FROM AppUser");
        while (rs.next()){
            String userName = rs.getString("username");
        }
        return allUsers;
    }
}
