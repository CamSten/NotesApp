package Control.Service;

import Control.AppManager;
import Control.Enums.NoteAction;
import Control.Enums.Prompts;
import Model.DatabaseRelay;
import Model.DataObjects.Note;
import Model.DataObjects.User;
import View.UserIO.MenuOptions;
import View.UserIO.SQLRunnable;
import View.UserIO.UserMenu;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
        Map<String, SQLRunnable> menu = getUserActionOptions(note);
        SQLRunnable runAction = menu.get(userAction.toString());
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

    Map<String, SQLRunnable> getUserActionOptions (Note note){
        List<String> input = List.of(NoteAction.ADD.toString(), NoteAction.READ.toString(), NoteAction.EDIT.toString(), NoteAction.REMOVE.toString(), NoteAction.REMOVE_ALL.toString());
        MenuOptions options = getOptions(List.of(() -> addNote(note), () -> readNotes(false), () -> editNote(note), () -> am.removeNote(note.getId(), user.getId(), false, false), () -> am.removeNote(-1, user.getId(), true, false)), input);
        return options.createMenu();
    }
    private MenuOptions getOptions(List<SQLRunnable> toPerform, List<String> inputValues) {
        return new MenuOptions(inputValues, toPerform);
    }
    public void replyToUser(Prompts prompts) {
        if (prompts != Prompts.OK) {
            userMenu.displayReply(prompts);
        }
    }
}
