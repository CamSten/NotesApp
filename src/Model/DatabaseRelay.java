package Model;

import Control.AppManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseRelay {
    private static Connection c;
    private static AppManager appManager;
    private UserRepo userRepo;
    private NoteRepo noteRepo;

    public DatabaseRelay(AppManager appManager) throws SQLException {
        this.appManager = appManager;
        this.c = DriverManager.getConnection(PropertyRetriever.getUrl(), PropertyRetriever.getUser(), PropertyRetriever.getPassword());
        this.userRepo = new UserRepo();
        this.noteRepo = new NoteRepo(c);
    }
    public String getPasswordHash(String username) throws SQLException {
        return userRepo.getPasswordHash(userRepo.getPasswordHash(username));
    }

    public boolean checkAvailability(String requestedName) throws SQLException {
        return userRepo.checkNameAvailability(requestedName);
    }

    public boolean addNewUser(String username, String passwordHash){
        return userRepo.
    }

}
