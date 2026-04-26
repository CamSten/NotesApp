package Control;

import Model.*;
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

    public void handleAdminAction(AdminAction action, int input) throws SQLException {
        System.out.println("handleAdminAction, input is: " + input + " Action is: " + action);
        if (!confirmAdmin()){
            adminMenu.printResponse(Prompts.WRONG_PASS);
        }
        else {
            switch (action) {
                case SEE_USERS -> retrieveUsers();
                case SEE_ALL_NOTES, SEE_NOTES -> retrieveNotes(action, input);
                case DELETE_USER_NOTES -> removeNote(input, user.getId(), true, false);
                case DELETE_NOTE -> removeNote(input, user.getId(), false, false);
                case DELETE_ALL_NOTES -> removeNote(input, user.getId(), false, true);
                case SEE_ALL_LOGIN_LOGS -> getLogPosts(-1, null);
                case SEE_LOGIN_FOR_STATUS -> getLogPosts(-1, LoginStatus.getStatus(input));
                case SEE_LOGIN_FOR_USER -> getLogPosts(input, null);
                case SEE_ALL_NOTE_LOGS, SEE_USER_NOTE_LOGS -> getNoteLogs(input);
            }
        }
    }

    public void handleLogOut() throws SQLException {
        showLoginMenu();
    }
    private Prompts validateLogin(String username, String passwordInput) throws SQLException {
        if (databaseRelay.checkAvailability(username)) {
            return Prompts.NO_SUCH_USER;
        }
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
        Prompts check = iv.validateLength(InputValidator.InputType.USERNAME, List.of(username));
        if (check != Prompts.OK){
            return check;
        }
        if (!databaseRelay.checkAvailability(username)){
            return Prompts.NAME_TAKEN;
        }
        check = iv.validateLength(InputValidator.InputType.PASSWORD, List.of(passwordInput));
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
        User.Role role = databaseRelay.checkIfAdmin(userId) ? User.Role.ADMIN : User.Role.USER;
        this.user = new User(username, userId, role, LocalDateTime.now());
        loginMenu.setUser(user);
    }
    public Prompts changePassword(String username, String passwordInput, String newPasswordInput) throws SQLException {
        Prompts lengthValidation = iv.validateLength(InputValidator.InputType.PASSWORD, List.of(newPasswordInput));
        if(lengthValidation != Prompts.OK){
            return lengthValidation;
        }
        if(!passwordService.validatePassword(passwordInput, databaseRelay.getPasswordHash(username))){
            return Prompts.WRONG_PASS;
        }
        databaseRelay.saveNewPassword(username, passwordService.hashPassword(newPasswordInput));
        return Prompts.NEW_PASS_OK;
    }
    private boolean confirmAdmin() throws SQLException {
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
    public int getUserIdForAdminUse(String username) throws SQLException {
        return (confirmAdmin()) ? databaseRelay.getUserIdForAdminUse(username) : -1;
    }
    private boolean verifyPassword() throws SQLException {
        String passwordHash = databaseRelay.getPasswordHash(user.getUsername());
        String passwordInput = adminMenu.promptVerify();
        boolean verified = passwordService.validatePassword(passwordInput, passwordHash);
        int attempt = verified ? 0 : 1;
        user.setIncorrectAttempts(attempt);
        if (user.getIncorrectAttempts() >=3) {
            adminMenu.forcedLogout();
        }
        return verified;
    }
    public Prompts handleUserAction(NoteAction userAction, Note note) throws SQLException {
        Prompts result = Prompts.OK;
        System.out.println("handleUserAction is called, action is: " + userAction);
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
    private void retrieveUsers() throws SQLException {

        user.setVerified(false);
        adminMenu.displayUsers(new Event(databaseRelay.getUsers(), Prompts.OK));
    }
    private void retrieveNotes(AdminAction action, int userId) throws SQLException {
        boolean allNotes = action != AdminAction.SEE_NOTES;
        user.setVerified(false);
        adminMenu.displayNotes(databaseRelay.getNotes(userId, allNotes), allNotes, true);
    }

    private Prompts addNote(Note note) throws SQLException {
        Prompts lengthsResult = iv.validateLength(InputValidator.InputType.NOTE, List.of(note.getTitle(), note.getContents()));
        if (lengthsResult != Prompts.OK){
            return lengthsResult;
        }
        return databaseRelay.addNote(user.getId(), note.getTitle(), note.getContents()) ? Prompts.OK : Prompts.ERROR;
    }
    public void readNotes(boolean allNotes) throws SQLException {
        userMenu.showAllNotes(databaseRelay.getNotes(user.getId(), allNotes));
    }
    private boolean editNote(Note n) throws SQLException {
        return databaseRelay.editNote(user.getId(), n);
    }
    private Prompts removeNote(int noteId, int userId, boolean allUserNotes, boolean allNotes) throws SQLException {
        if (allNotes) {
            if (user.isVerified()) {
                user.setVerified(false);
                return databaseRelay.removeAllNotes() ? Prompts.OK : Prompts.ERROR;
            }
            return Prompts.WRONG_PASS;
        }
        if (user.getRole() == User.Role.ADMIN || user.getId() == userId) {
            user.setVerified(false);
            return databaseRelay.removeNotesForUser(userId, noteId, allUserNotes) ? Prompts.OK : Prompts.NO_SUCH_NOTE;
        }
        return Prompts.ERROR;
    }

    private void getLogPosts(int input, LoginStatus status) throws SQLException {
        List<LogPost> logPosts;
        if (input!=-1){
            logPosts = databaseRelay.getLogPostForUser(input);
        }
        else if (status!= null){
            logPosts = databaseRelay.getLogPostForStatus(status);
        }
        else {
            logPosts = databaseRelay.getLogPosts();
        }
        user.setVerified(false);
        adminMenu.showLogPosts(logPosts);
    }

    private void getNoteLogs(int input) throws SQLException {
        List<NoteLog> noteLogs;
        if (input!=-1) {
            noteLogs = databaseRelay.getNoteLogsForActor(input);
        }
        else {
            noteLogs = databaseRelay.getAllNoteLogs();
        }
        user.setVerified(false);
        adminMenu.showNoteLogs(noteLogs);
    }
}