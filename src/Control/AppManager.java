package Control;

import Model.DatabaseRelay;
import Model.Note;
import Model.User;
import View.AdminMenu;
import View.LoginMenu;
import View.UserMenu;

import javax.swing.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

public class AppManager {
    private UserMenu userMenu;
    private AdminMenu adminMenu;
    private LoginMenu loginMenu;
    private final PasswordService passwordService = new PasswordService();
    private final DatabaseRelay databaseRelay = new DatabaseRelay();
    private final InputValidator iv;
    private User user;

    public AppManager() throws SQLException {
        this.iv = new InputValidator();
    }

    public void run() throws SQLException {
        showLoginMenu();
    }
    private void showLoginMenu() throws SQLException {
        this.userMenu = new UserMenu(this);
        this.adminMenu = new AdminMenu(this);
        this.loginMenu = new LoginMenu(userMenu, adminMenu, this);
        this.user = null;
        loginMenu.showLoginMenu();
    }

    public Event handleAdminAction(AdminMenu.AdminAction action, List<String> input) throws SQLException {
        Event event = new Event(null, Prompts.ERROR);
        System.out.println("handleAdminAction, input is: " + input + " Action is: " + action);
        if (verifyAdmin()) {
            System.out.println("Verified admin: check");
            switch (action) {
                case SEE_USERS -> event = retrieveUsers();
                case SEE_ALL_NOTES -> event = retrieveNotes(action, "");
                case SEE_NOTES -> event = retrieveNotes(action, input.getFirst());
                case DELETE_USER_NOTES, DELETE_NOTE -> {
                    System.out.println("case delete_user_notes, delete_note is reached. Input is: " + input);
                    try {
                        int noteId = Integer.parseInt(input.getFirst());
                        System.out.println("noteId is: " + noteId);
                        if (action == AdminMenu.AdminAction.DELETE_USER_NOTES) {
                            event.setPrompts(requestRemoveAllUserNotes(noteId));
                        } else {
                            System.out.println("DELETE_NOTE");
                            int userId = databaseRelay.getUserIdForAdminUse(input.getLast());
                            event.setPrompts(removeNote(noteId, userId));
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("number format exception. data is: " + input);
                        event.setPrompts(Prompts.NO_SUCH_NOTE);
                    }
                }
                case DELETE_ALL_NOTES -> {
                }
            }
            System.out.println("event.prompts is: " + event.getPrompts());
            if (event.getData() != null) {
                System.out.println("event.getData() is: " + event.getData().getClass());
            }
        }
        else {
            event.setPrompts(Prompts.WRONG_PASS);
        }
        return event;
    }

    private Event retrieveUsers() throws SQLException {
        System.out.println("REETRIEVE USERS in AppManager is called");
        List<User> usernames = databaseRelay.getUsers();
        user.setVerified(false);
        return new Event(usernames, Prompts.OK);
    }
    private Event retrieveNotes(AdminMenu.AdminAction action, String input) throws SQLException {
        boolean allNotes = true;
        int userId = -1;
        if (action == AdminMenu.AdminAction.SEE_NOTES){
            allNotes = false;
            userId = databaseRelay.getUserIdForAdminUse(input);
        }
        System.out.println("in APPm retrieveNotes, userId is: " + userId + ", input is: " + input);
        List<Note> notes = databaseRelay.getNotes(userId, allNotes);
        user.setVerified(false);
        return new Event(notes, Prompts.OK);
    }

    public Prompts handleNoteAction(UserMenu.NoteAction userAction, Note note) throws SQLException {
        switch (userAction) {
            case ADD -> {
                return(addNote(note));
            }
            case READ -> {
                return Prompts.AWAIT_DATA;
            }
            case EDIT -> {
                editNote(note);
                return Prompts.OK;
            }
            case REMOVE -> {
                return removeNote(note.getId(), user.getId());
            }
            case REMOVE_ALL -> {
                return requestRemoveAllUserNotes(user.getId());
            }
            default -> {
                return Prompts.ERROR;
            }
        }
    }
    public void handleLogOut() throws SQLException {
        showLoginMenu();
    }
    public Prompts requestRemoveAllUserNotes(int userId) throws SQLException {
        boolean valid = user.isVerified();
        if(!valid) {
//            String passwordHash = databaseRelay.getPasswordHash(user.getUsername());
//            valid = passwordService.validatePassword(user.get, passwordHash);
        }
        if (valid){
            databaseRelay.removeNote(userId, -1, true);
            return Prompts.OK;
        }
        else {
            return Prompts.WRONG_PASS;
        }
    }

    public Prompts validateLogin(String username, String passwordInput) throws SQLException {
        boolean usernameIsAvailable = databaseRelay.checkAvailability(username);
        if (!usernameIsAvailable) {
            String passwordHash = databaseRelay.getPasswordHash(username);
            boolean result = passwordService.validatePassword(passwordInput, passwordHash);
            databaseRelay.logLoginAttempt(username, result);
            System.out.println("result for validateLogin is: " + result);
            if (!result) {
                return Prompts.WRONG_PASS;
            } else {
                setUser(username, passwordHash);
                return Prompts.OK;
            }
        } else {
            return Prompts.NO_SUCH_USER;
        }
    }

    public Prompts validateNewUser(String username, String passwordInput) throws SQLException {
        boolean availableName = databaseRelay.checkAvailability(username);
        if (!availableName){
            return Prompts.NAME_TAKEN;
        }
        Prompts lenghtValidation = iv.lengthValidation(username, passwordInput);
        if (lenghtValidation == Prompts.OK) {
            String passwordHash = passwordService.hashPassword(passwordInput);
            databaseRelay.addNewUser(username, passwordHash);
            setUser(username, passwordHash);
            return Prompts.OK;
        }
        else return lenghtValidation;
    }
    private void setUser(String username, String passwordHash) throws SQLException {
        int userId = databaseRelay.getUserId(username, passwordHash);
        User.Role role = User.Role.USER;
        if (databaseRelay.checkIfAdmin(userId)){
            role = User.Role.ADMIN;
        }
        this.user = new User(username, userId, role, LocalDateTime.now());
        loginMenu.setUser(user);
    }
    public Prompts changePassword(String username, String passwordInput, String newPasswordInput) throws SQLException {
        if (iv.checkTooShort(newPasswordInput, InputValidator.InputType.PASSWORD.PASSWORD) != Prompts.OK){
            return Prompts.SHORT_PASS;
        }
        else if (iv.checkTooLong(newPasswordInput, InputValidator.InputType.PASSWORD) != Prompts.OK){
            return Prompts.LONG_PASS;
        }
        else {
            String passwordHash = databaseRelay.getPasswordHash(username);
            boolean result = passwordService.validatePassword(passwordInput, passwordHash);
            if (!result) {
                return Prompts.WRONG_PASS;
            } else {
                String newPasswordHash = passwordService.hashPassword(newPasswordInput);
                databaseRelay.saveNewPassword(username, newPasswordHash);
                return Prompts.NEW_PASS_OK;
            }
        }
    }
    private boolean verifyAdmin() throws SQLException {
        boolean verified = false;
        if (user.getRole()== User.Role.ADMIN) {
            verified = true;
            if (!user.isVerified()) {
                verified = verifyPassword();
                user.setVerified(verified);
            }
        }
        System.out.println("In verifyAdmin, role is: " + user.getRole() + " number of failed attempts is: " + user.getIncorrectAttempts());
        return verified;
    }
    private boolean verifyPassword() throws SQLException {
        String passwordHash = databaseRelay.getPasswordHash(user.getUsername());
        String passwordInput = adminMenu.promptVerify();
        boolean verified = passwordService.validatePassword(passwordInput, passwordHash);
        int attempt = 0;
        if (!verified){
            attempt = 1;
        }
        if (user.getIncorrectAttempts() >=3) {
            adminMenu.forcedLogout();
        }
        user.setIncorrectAttempts(attempt);
        return verified;
    }

    private Prompts addNote(Note note) throws SQLException {
        if (iv.checkTooLong(note.getTitle(), InputValidator.InputType.NOTE_TITLE) != Prompts.OK){
            return Prompts.LONG_TITLE;
        }
        else if (iv.checkTooShort(note.getTitle(), InputValidator.InputType.NOTE_TITLE) != Prompts.OK){
            return Prompts.SHORT_TITLE;
        }
        else if (iv.checkTooLong(note.getContents(), InputValidator.InputType.NOTE_TEXT) != Prompts.OK){
            return Prompts.LONG_TEXT;
        }
        else if (iv.checkTooShort(note.getContents(), InputValidator.InputType.NOTE_TEXT) != Prompts.OK) {
            return Prompts.SHORT_TEXT;
        }
        else{
            databaseRelay.addNote(user.getId(), note.getTitle(), note.getContents());
            return Prompts.OK;
        }
    }
    public void readNotes(boolean allNotes) throws SQLException {
        List<Note> notes = databaseRelay.getNotes(user.getId(), allNotes);
        userMenu.showAllNotes(notes);
    }
    private void editNote(Note n) throws SQLException {
        databaseRelay.editNote(user.getId(), n);
    }
    private Prompts removeNote(int noteId, int userId) throws SQLException {
        System.out.println("_____ in removeNote in AppM, noteId is: " + noteId + ", userId is: " + userId);
        if (databaseRelay.removeNote(userId, noteId, false)){
            if (user.getRole() == User.Role.ADMIN){
                user.setVerified(false);
            }
            return Prompts.OK;
        }
        else return Prompts.ERROR;
    }
}