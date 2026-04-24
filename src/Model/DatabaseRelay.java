package Model;

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

    public boolean logLoginAttempt(String username, Boolean success) throws SQLException {
        int id = userRepo.getUserIdForAdminUse(username);
        return userRepo.logLoginAttempt(id, success);
    }
    public int getUserId(String username, String passwordHash) throws SQLException {
        return userRepo.getUserId(username, passwordHash);
    }
    public int getUserIdForAdminUse(String username) throws SQLException {
        return userRepo.getUserIdForAdminUse(username);
    }
    public boolean checkIfAdmin(int userid) throws SQLException {
        return userRepo.checkIfAdmin(userid);
    }
    public void addNewUser(String username, String passwordHash) throws SQLException {
        userRepo.addNewUser(username, passwordHash);
    }
    public void saveNewPassword(String username, String newPasswordHash) throws SQLException {
        userRepo.saveNewPassword(username, newPasswordHash);
    }
    public void addNote(int userId, String title, String contents) throws SQLException {
        noteRepo.addNote(userId, title, contents);
    }
    public List<Note> getNotes(int thisUserId, boolean allNotes) throws SQLException {
        return noteRepo.getNotes(thisUserId, allNotes);
    }
    public void editNote(int userId, Note n) throws SQLException {
        noteRepo.editNote(userId, n.getId(), n.getTitle(), n.getContents());
    }
    public boolean removeNote(int userId, int noteId, boolean allNotes) throws SQLException {
        return noteRepo.deleteNotes(userId, noteId, allNotes);
    }
}