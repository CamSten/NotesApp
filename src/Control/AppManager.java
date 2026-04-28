package Control;

import Control.Enums.*;
import Control.Service.*;
import Control.Service.UserService;
import Model.*;
import Model.DataObjects.*;
import View.AdminIO.AdminMenu;
import View.UserIO.UserMenu;
import View.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class AppManager {
    private final Scanner scan;
    private UserMenu userMenu;
    private AdminMenu adminMenu;
    private LoginMenu loginMenu;
    private final PasswordService passwordService = new PasswordService();
    private final DatabaseRelay databaseRelay = new DatabaseRelay();
    private final InputValidator iv;
    private final View.ConsoleOutput co;
    private final View.ConsoleInput ci;
    private User user;
    private AdminService adminService;
    private UserService userService;

    public AppManager() throws SQLException {
        this.iv = new InputValidator();
        this.scan = new Scanner(System.in);
        this.co = new ConsoleOutput();
        this.ci = new ConsoleInput(scan);
    }

    public void run() throws SQLException {
        showLoginMenu();
    }
    private void showLoginMenu() throws SQLException {
        this.userMenu = new UserMenu(this, ci, co);
        this.adminMenu = new AdminMenu(this, ci, co);
        this.loginMenu = new LoginMenu(userMenu, adminMenu, this, ci, co);
        this.user = null;
        loginMenu.showLoginMenu();
    }

    public void handleAdminAction(AdminAction action, int input) throws SQLException {
        adminService.handleAdminAction(action, input);
    }
    public int getUserIdForAdminUse(String username) throws SQLException {
        return adminService.getUserIdForAdminUse(username);
    }
    public void handleUserAction(NoteAction action, Note note) throws SQLException {
        userService.handleUserAction(action, note);
    }
    private void setUser(String username, String passwordHash) throws SQLException {
        int userId = databaseRelay.getUserId(username, passwordHash);
        User.Role role = databaseRelay.checkIfAdmin(userId) ? User.Role.ADMIN : User.Role.USER;
        this.user = new User(username, userId, role, LocalDateTime.now());
        this.adminService = new AdminService(this, databaseRelay, adminMenu, passwordService, user);
        this.userService = new UserService(this, user, passwordService, iv, databaseRelay, userMenu);
        loginMenu.setUser(user);
    }
    public boolean handleLogOut() throws SQLException {
        showLoginMenu();
        return true;
    }

    public boolean checkPassword(String passwordInput, String username) throws SQLException {
        String passwordHash = databaseRelay.getPasswordHash(username);
        if (!passwordService.validatePassword(passwordInput, passwordHash)) {
            return false;
        }
        return true;
    }
    public Prompts validateLogin(String username, String passwordInput) throws SQLException {
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

    public boolean checkAvailability (String username) throws SQLException {
        return databaseRelay.checkAvailability(username);
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

    public Prompts changePassword(String username, String passwordInput, String newPasswordInput) throws SQLException {
        Prompts lengthValidation = iv.validateLength(InputValidator.InputType.PASSWORD, List.of(newPasswordInput));
        if(lengthValidation != Prompts.OK){
            return lengthValidation;
        }
        if(!passwordService.validatePassword(passwordInput, databaseRelay.getPasswordHash(username))){
            return Prompts.WRONG_PASS;
        }
        databaseRelay.saveNewPassword(user.getId(), passwordService.hashPassword(newPasswordInput));
        return Prompts.NEW_PASS_OK;
    }

    private void replyToUser(AdminAction action, Prompts reply){
        if(user.getRole() == User.Role.ADMIN){
            adminService.relayReply(action, reply);
        }
        else{
            userService.replyToUser(reply);
        }
    }
    public void removeNote(int noteId, int userId, boolean allUserNotes, boolean allNotes) throws SQLException {
        AdminAction response = allUserNotes ? AdminAction.DELETE_USER_NOTES : AdminAction.DELETE_NOTE;
        if (allNotes) {
            if (user.isVerified()) {
                adminService.resetAdminVerification();
                adminService.relayReply(AdminAction.DELETE_ALL_NOTES, databaseRelay.removeAllNotes() ? Prompts.OK : Prompts.ERROR);
                return;
            }
        }
        boolean validAction = (user.getRole() == User.Role.ADMIN) ? user.isVerified() : userService.verifyUserPassword();
        if (validAction){
            adminService.resetAdminVerification();
            if (user.getRole() != User.Role.ADMIN) {
                replyToUser(null, databaseRelay.removeNotesForUser(userId, noteId, allUserNotes) ? Prompts.OK : Prompts.NO_SUCH_NOTE);
            }
            else {
                adminService.relayReply(response, databaseRelay.removeNotesForUser(userId, noteId, allUserNotes) ? Prompts.OK : Prompts.NO_SUCH_NOTE);
            }
            return;
        }
        replyToUser(response, Prompts.WRONG_PASS);
    }
    public Prompts validateInputLength(String input, InputValidator.InputType inputType){
        return iv.validateLength(inputType, List.of(input));
    }
}