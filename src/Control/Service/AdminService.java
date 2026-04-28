package Control.Service;

import Control.*;
import Control.Enums.*;
import Model.DatabaseRelay;
import Model.DataObjects.*;
import View.AdminIO.AdminMenu;
import View.UserIO.*;
import java.sql.SQLException;
import java.util.*;
import static Control.Enums.AdminAction.*;

public class AdminService {
    private final AppManager am;
    private final DatabaseRelay databaseRelay;
    private final AdminMenu adminMenu;
    private final PasswordService passwordService;
    private final User user;

    public AdminService(AppManager am, DatabaseRelay databaseRelay, AdminMenu adminMenu, PasswordService passwordService, User user){
        this.am = am;
        this.databaseRelay = databaseRelay;
        this.adminMenu = adminMenu;
        this.passwordService = passwordService;
        this.user = user;
    }
    public void handleAdminAction(AdminAction action, int input) throws SQLException {
        if (!confirmAdmin()){
            adminMenu.printResponse(action, Prompts.WRONG_PASS);
            return;
        }
        user.setVerified(true);
        Map<String, SQLRunnableVoid> menu = getAdminActionOptions(action, input);
        SQLRunnableVoid runAction = menu.get(action.toString());
        if (runAction == null){
            adminMenu.printResponse(action, Prompts.ERROR);
            return;
        }
        runAction.run();
    }
    public void retrieveUsers() throws SQLException {
        resetAdminVerification();
        adminMenu.displayUsers(new Event(databaseRelay.getUsers(), Prompts.OK));
    }
    public void retrieveNotes(AdminAction action, int userId) throws SQLException {
        boolean allNotes = (action == SEE_ALL_NOTES);
        resetAdminVerification();
        adminMenu.displayNotes(databaseRelay.getNotes(userId, allNotes), allNotes, true);
    }

    public void getLogPosts(int input, LoginStatus status) throws SQLException {
        List<LogPost> logPosts;
        if (status!= null){
            logPosts = databaseRelay.getLogPostForStatus(status);
        }
        else if (input!=-1){
            logPosts = databaseRelay.getLogPostForUser(input);
        }
        else {
            logPosts = databaseRelay.getLogPosts();
        }
        resetAdminVerification();
        adminMenu.showLogPosts(logPosts);
    }

    public void getNoteLogs(int input) throws SQLException {
        List<NoteLog> noteLogs;
        noteLogs = (input!=-1) ? databaseRelay.getNoteLogsForActor(input) : databaseRelay.getAllNoteLogs();
        resetAdminVerification();
        adminMenu.showNoteLogs(noteLogs);
    }

    private boolean confirmAdmin() throws SQLException {
        boolean verified = false;
        if (user.getRole()== User.Role.ADMIN) {
            verified = true;
            if (!user.isVerified()) {
                verified = verifyAdminPassword();
            }
        }
        return verified;
    }
    public void resetAdminVerification(){
        if(user.getRole() == User.Role.ADMIN) {
            user.setVerified(false);
        }
    }
    public int getUserIdForAdminUse(String username) throws SQLException {
        return databaseRelay.getUserIdForAdminUse(username);
    }
    public boolean verifyAdminPassword() throws SQLException {
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
    public void relayReply(AdminAction action, Prompts prompts){
        adminMenu.printResponse(action, prompts);
    }
    public Map<String, SQLRunnableVoid> getAdminActionOptions(AdminAction action, int input){
        List<String> inputValues = List.of(SEE_USERS.toString(), SEE_ALL_NOTES.toString(), SEE_NOTES.toString(), DELETE_USER_NOTES.toString(),
                DELETE_NOTE.toString(), DELETE_ALL_NOTES.toString(), SEE_ALL_LOGIN_LOGS.toString(),
                SEE_LOGIN_FOR_STATUS.toString(), SEE_LOGIN_FOR_USER.toString(), SEE_ALL_NOTE_LOGS.toString(), SEE_USER_NOTE_LOGS.toString());
        MenuOptions options = getOptions((List.of(this::retrieveUsers, () -> retrieveNotes(action, input), () -> retrieveNotes(action, input),
                () -> am.removeNote(input, user.getId(), true, false), () -> am.removeNote(input, user.getId(), false, false),
                () -> am.removeNote(input, user.getId(), false, true), () -> getLogPosts(-1, null),
                () -> getLogPosts(-1, LoginStatus.getStatus(input)),
                () -> getLogPosts(input, null), () -> getNoteLogs(-1), () -> getNoteLogs(input))), inputValues);
        return options.createMenuVoid();
    }
    private MenuOptions getOptions(List<SQLRunnableVoid> toPerform, List<String> inputValues) {
        MenuOptions menuOptions = new MenuOptions();
        menuOptions.getMenuOptions(inputValues, toPerform);
        return menuOptions;
    }
}