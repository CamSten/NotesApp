package Control;

import Model.DatabaseRelay;
import Model.Note;
import Model.User;
import View.AdminMenu;
import View.LoginMenu;
import View.UserMenu;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class AppManager {
    private final Scanner scan;
    private UserMenu userMenu;
    private AdminMenu adminMenu;
    private LoginMenu loginMenu;
    private final PasswordService passwordService = new PasswordService();
    private final DatabaseRelay databaseRelay = new DatabaseRelay();
    private final InputValidator iv;
    private User user;
    public enum LoginStatus {SUCCESS, FAIL, UNKNOWN_USER}

    public AppManager() throws SQLException {
        this.iv = new InputValidator();
        this.scan = new Scanner(System.in);
    }

    public void run() throws SQLException {
        showLoginMenu();
    }
    private void showLoginMenu() throws SQLException {
        this.userMenu = new UserMenu(this, scan);
        this.adminMenu = new AdminMenu(this, scan);
        this.loginMenu = new LoginMenu(userMenu, adminMenu, this);
        this.user = null;
        loginMenu.showLoginMenu();
    }

    public Event handleAdminAction(AdminMenu.AdminAction action, int input) throws SQLException {
        Event event = new Event(null, Prompts.ERROR);
        System.out.println("handleAdminAction, input is: " + input + " Action is: " + action);
        if (!verifyAdmin()){
            return new Event(null, Prompts.WRONG_PASS);
        }
        switch (action) {
            case SEE_USERS -> event = retrieveUsers();
            case SEE_ALL_NOTES, SEE_NOTES -> event = retrieveNotes(action, input);
            case DELETE_USER_NOTES -> event.setPrompts(removeNote(input, user.getId(), true, false));
            case DELETE_NOTE -> event.setPrompts(removeNote(input, user.getId(), false, false));
            case DELETE_ALL_NOTES -> event.setPrompts(removeNote(input, user.getId(), false, true));
        }
        return event;
    }

    public void handleLogOut() throws SQLException {
        showLoginMenu();
    }
    private Prompts validateLogin(String username, String passwordInput) throws SQLException {
        System.out.println("validateLogin is reached");
        if (databaseRelay.checkAvailability(username)) {
            databaseRelay.logLoginAttempt(username, LoginStatus.UNKNOWN_USER);
            return Prompts.NO_SUCH_USER;
        }
        System.out.println("name is not available (which means that there IS a user with this user name).");
        String passwordHash = databaseRelay.getPasswordHash(username);
        if (!passwordService.validatePassword(passwordInput, passwordHash)) {
            return Prompts.WRONG_PASS;
        }
        setUser(username, passwordHash);
        return Prompts.OK;
    }

    public Prompts validate(String username, String passwordInput, boolean newAccount) throws SQLException {
        Prompts result = newAccount ? validateNewUser(username, passwordInput) : validateLogin(username, passwordInput);
        LoginStatus status = getLoginStatus(result);
        if ((!newAccount) || result == Prompts.OK) {
            databaseRelay.logLoginAttempt(username, status);
        }
        System.out.println("result is: " + result + " newAccount is: " + newAccount);
        return result;
    }

    public LoginStatus getLoginStatus(Prompts prompt){
        return switch (prompt) {
            case OK, NEW_PASS_OK -> LoginStatus.SUCCESS;
            case NO_SUCH_USER -> LoginStatus.UNKNOWN_USER;
            default -> LoginStatus.FAIL;
        };
    }
    private Prompts validateNewUser(String username, String passwordInput) throws SQLException {
        Prompts check = iv.validate(InputValidator.InputType.USERNAME, List.of(username));
        if (check != Prompts.OK){
            return check;
        }
        if (!databaseRelay.checkAvailability(username)){
            return Prompts.NAME_TAKEN;
        }
        check = iv.validate(InputValidator.InputType.PASSWORD, List.of(passwordInput));
        if (check != Prompts.OK) {
            return check;
        }
        String passwordHash = passwordService.hashPassword(passwordInput);
        databaseRelay.addNewUser(username, passwordHash);
        setUser(username, passwordHash);
        return Prompts.OK;
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
        Prompts lengthValidation = iv.validate(InputValidator.InputType.PASSWORD, List.of(newPasswordInput));
        if(lengthValidation != Prompts.OK){
            return lengthValidation;
        }
        if(!passwordService.validatePassword(passwordInput, databaseRelay.getPasswordHash(username))){
            return Prompts.WRONG_PASS;
        }
        databaseRelay.saveNewPassword(username, passwordService.hashPassword(newPasswordInput));
        return Prompts.NEW_PASS_OK;
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
    public Prompts handleNoteAction(UserMenu.NoteAction userAction, Note note) throws SQLException {
        Prompts result = Prompts.OK;
        switch (userAction) {
            case ADD -> result = addNote(note);
            case READ ->  {
                result = Prompts.AWAIT_DATA;
                readNotes(false);
            }
            case EDIT -> {
                if(!editNote(note)) {
                    result = Prompts.NO_SUCH_NOTE;
                }
            }
            case REMOVE -> result = removeNote(note.getId(), user.getId(), false, false);
            case REMOVE_ALL -> result = removeNote(-1, user.getId(), true, false);
            default -> result = Prompts.ERROR;
        }
        return result;
    }
    private Event retrieveUsers() throws SQLException {
        List<User> usernames = databaseRelay.getUsers();
        user.setVerified(false);
        return new Event(usernames, Prompts.OK);
    }
    private Event retrieveNotes(AdminMenu.AdminAction action, int userId) throws SQLException {
        boolean allNotes = action != AdminMenu.AdminAction.SEE_NOTES;
        List<Note> notes = databaseRelay.getNotes(userId, allNotes);
        user.setVerified(false);
        return new Event(notes, Prompts.OK);
    }

    private Prompts addNote(Note note) throws SQLException {
        Prompts lengthsResult = iv.validate(InputValidator.InputType.NOTE, List.of(note.getTitle(), note.getContents()));
        if (lengthsResult != Prompts.OK){
             return lengthsResult;
         }
        return databaseRelay.addNote(user.getId(), note.getTitle(), note.getContents()) ? Prompts.OK : Prompts.ERROR;
    }
    public void readNotes(boolean allNotes) throws SQLException {
        List<Note> notes = databaseRelay.getNotes(user.getId(), allNotes);
        userMenu.showAllNotes(notes);
    }
    private boolean editNote(Note n) throws SQLException {
        return databaseRelay.editNote(user.getId(), n);
    }
    private Prompts removeNote(int noteId, int userId, boolean allUserNotes, boolean allNotes) throws SQLException {
        if (allNotes) {
            if (user.isVerified()) {
                user.setVerified(false);
                databaseRelay.removeAllNotes();
                return Prompts.OK;
            }
            return Prompts.WRONG_PASS;
        }
        if (user.getRole() == User.Role.ADMIN || user.getId() == userId) {
            user.setVerified(false);
            return databaseRelay.removeNotesForUser(userId, noteId, allUserNotes) ? Prompts.OK : Prompts.NO_SUCH_NOTE;
        }
        return Prompts.ERROR;
    }
}