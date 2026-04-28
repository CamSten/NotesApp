package Model;

import Control.Enums.LoginStatus;
import Model.DataObjects.*;
import Model.Properties.PropertyRetriever;
import Model.Repositories.*;
import java.sql.*;
import java.util.List;

public class DatabaseRelay {
    private static Connection c;
    private final UserRepo userRepo;
    private final NoteRepo noteRepo;
    private final AdminRepo adminRepo;

    public DatabaseRelay() throws SQLException {
        this.c = DriverManager.getConnection(PropertyRetriever.getUrl(), PropertyRetriever.getUser(), PropertyRetriever.getPassword());
        this.userRepo = new UserRepo(c);
        this.noteRepo = new NoteRepo(c);
        this.adminRepo = new AdminRepo(c);
    }
    public List<User> getUsers() throws SQLException {
        return userRepo.getUsers();
    }
    public String getPasswordHash(String username) throws SQLException {
        return userRepo.getPasswordHash(username);
    }
    public boolean checkAvailability(String requestedName) throws SQLException {
        return userRepo.checkNameAvailability(requestedName);
    }
    public void logLoginAttempt(String username, LoginStatus status) throws SQLException {
        int id = userRepo.getUserIdForAdminUse(username);
        userRepo.logLoginAttempt(id, status);
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
    public void saveNewPassword(int userId, String newPasswordHash) throws SQLException {
        userRepo.saveNewPassword(userId, newPasswordHash);
    }
    public boolean addNote(int userId, String title, String contents) throws SQLException {
        return noteRepo.addNote(userId, title, contents);
    }
    public List<Note> getNotes(int thisUserId, boolean allNotes) throws SQLException {
        return noteRepo.getNotes(thisUserId, allNotes);
    }
    public boolean editNote(int userId, Note n) throws SQLException {
        return (noteRepo.editNote(userId, n.getId(), n.getTitle(), n.getContents()) > 0);
    }
    public boolean removeNotesForUser(int userId, int noteId, boolean allNotes) throws SQLException {
        return noteRepo.deleteNotes(userId, noteId, allNotes);
    }
    public boolean removeAllNotes() throws SQLException {
        return noteRepo.deleteAllNotes();
    }
    public List<LogPost> getLogPosts() throws SQLException {
        return adminRepo.getLoginData();
    }
    public List<LogPost> getLogPostForStatus(LoginStatus status) throws SQLException {
        return adminRepo.getLoginDataForStatus(status);
    }
    public List<LogPost> getLogPostForUser(int userId) throws SQLException {
        return adminRepo.getLoginDataForUser(userId);
    }
    public List<NoteLog> getAllNoteLogs() throws SQLException {
        return adminRepo.getNotesLog();
    }
    public List<NoteLog> getNoteLogsForActor(int userId) throws SQLException {
        return adminRepo.getNotesLogForActor(userId);
    }
}