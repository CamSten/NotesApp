package Model;

import Control.AppManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class DatabaseRelay {
    private static Connection c;
    private UserRepo userRepo;
    private NoteRepo noteRepo;

    public DatabaseRelay() throws SQLException {
        this.c = DriverManager.getConnection(PropertyRetriever.getUrl(), PropertyRetriever.getUser(), PropertyRetriever.getPassword());
        this.userRepo = new UserRepo(c);
        this.noteRepo = new NoteRepo(c);
    }
    public List<User> getUsers() throws SQLException {
        System.out.println("---get users is called in DB R");
        return userRepo.getUsers();
    }
    public String getPasswordHash(String username) throws SQLException {
        return userRepo.getPasswordHash(username);
    }
    public boolean checkAvailability(String requestedName) throws SQLException {
        return userRepo.checkNameAvailability(requestedName);
    }

    public boolean logLoginAttempt(String username, AppManager.LoginStatus status) throws SQLException {
        int id = userRepo.getUserIdForAdminUse(username);
        return userRepo.logLoginAttempt(id, status);
    }
    public int getUserId(String username, String passwordHash) throws SQLException {
        return userRepo.getUserId(username, passwordHash);
    }
    public int getUserIdForAdminUse(String username) throws SQLException {
        return userRepo.getUserIdForAdminUse(username);
    }
    public boolean checkIfAdmin(int userid) throws SQLException {
        System.out.println("CHECKIFADMIN in D B R");
        return userRepo.checkIfAdmin(userid);
    }
    public void addNewUser(String username, String passwordHash) throws SQLException {
        System.out.println("IN D B R ADDNEWUSER, username/hash is: " + username + "/" +passwordHash);
        userRepo.addNewUser(username, passwordHash);
    }
    public void saveNewPassword(String username, String newPasswordHash) throws SQLException {
        userRepo.saveNewPassword(username, newPasswordHash);
    }
    public boolean addNote(int userId, String title, String contents) throws SQLException {
        return noteRepo.addNote(userId, title, contents);
    }
    public List<Note> getNotes(int thisUserId, boolean allNotes) throws SQLException {
        return noteRepo.getNotes(thisUserId, allNotes);
    }
    public boolean editNote(int userId, Note n) throws SQLException {
        return noteRepo.editNote(userId, n.getId(), n.getTitle(), n.getContents());
    }
    public boolean removeNotesForUser(int userId, int noteId, boolean allNotes) throws SQLException {
        return noteRepo.deleteNotes(userId, noteId, allNotes);
    }
    public boolean removeAllNotes() throws SQLException {
        return noteRepo.deleteAllNotes();
    }
}