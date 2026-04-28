package Control.Service;

import Control.AppManager;
import Control.Enums.*;
import Model.DatabaseRelay;
import Model.DataObjects.*;
import View.UserIO.*;
import java.sql.SQLException;
import java.util.*;

public class UserService {
    private AppManager am;
    private User user;
    private PasswordService passwordService;
    private DatabaseRelay databaseRelay;
    private InputValidator iv;
    private UserMenu userMenu;

    public UserService (AppManager am, User user, PasswordService passwordService, InputValidator iv, DatabaseRelay databaseRelay, UserMenu userMenu){
        this.am = am;
        this.user = user;
        this.passwordService = passwordService;
        this.iv = iv;
        this.databaseRelay = databaseRelay;
        this.userMenu = userMenu;
    }
    public void handleUserAction(NoteAction userAction, Note note) throws SQLException {
        Map<String, SQLRunnableVoid> menu =  getUserActionOptions(note);
        SQLRunnableVoid runAction = menu.get(userAction.toString());
        if (runAction == null){
            replyToUser(Prompts.ERROR);
            return;
        }
        runAction.run();
    }
    public void readNotes(boolean allNotes) throws SQLException {
        userMenu.showAllNotes(databaseRelay.getNotes(user.getId(), allNotes));
    }

    public void addNote(Note note) throws SQLException {
        Prompts lengthsResult = iv.validateLength(InputValidator.InputType.NOTE, List.of(note.getTitle(), note.getContents()));
        if (lengthsResult != Prompts.OK){
            replyToUser(lengthsResult);
            return;
        }
        replyToUser(databaseRelay.addNote(user.getId(), note.getTitle(), note.getContents()) ? Prompts.OK : Prompts.ERROR);
    }
    public void editNote(Note n) throws SQLException {
        replyToUser(databaseRelay.editNote(user.getId(), n) ? Prompts.OK : Prompts.NO_SUCH_NOTE);
    }
    public boolean verifyUserPassword() throws SQLException {
        String userInput = userMenu.confirmPassword();
        return passwordService.validatePassword(userInput, databaseRelay.getPasswordHash(user.getUsername()));
    }

    private Map<String, SQLRunnableVoid> getUserActionOptions (Note note){
        List<String> input = List.of(NoteAction.ADD.toString(), NoteAction.READ.toString(), NoteAction.EDIT.toString(), NoteAction.REMOVE.toString(), NoteAction.REMOVE_ALL.toString());
        MenuOptions options = new MenuOptions();
        return options.getMenuOptions(input, List.of(() -> addNote(note), () -> readNotes(false), () -> editNote(note), () -> am.removeNote(note.getId(), user.getId(), false, false), () ->  am.removeNote(-1, user.getId(), true, false)));
    }

    public void replyToUser(Prompts prompts) {
        if (prompts != Prompts.OK) {
            userMenu.displayReply(prompts);
        }
    }
}