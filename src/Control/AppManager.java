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
        this.adminMenu = new AdminMenu();
        this.loginMenu = new LoginMenu(userMenu, adminMenu, this);
        this.user = null;
        loginMenu.showLoginMenu();
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
                return Prompts.NOTE_OK;
            }
            case REMOVE -> {
                return removeNote(note);
            }
            default -> {
                return Prompts.ERROR;
            }
        }
    }
    public void handleLogOut() throws SQLException {
        showLoginMenu();
    }
    public Prompts requestRemoveAllUserNotes(String password) throws SQLException {
        String passwordHash = databaseRelay.getPasswordHash(user.getUsername());
        boolean valid = passwordService.validatePassword(password, passwordHash);
        if (valid){
            databaseRelay.removeNote(user.getId(), new Note(0, "","", LocalDateTime.now()), true);
            return Prompts.NOTE_OK;
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
            if (!result) {
                return Prompts.WRONG_PASS;
            } else {
                setUser(username, passwordHash);
                return Prompts.LOGIN_OK;
            }
        } else {
            return Prompts.NO_SUCH_USER;
        }
    }

    public Prompts validateNewUser(String username, String passwordInput) throws SQLException {
        Prompts lenghtValidation = iv.lengthValidation(username, passwordInput);
        if (lenghtValidation == Prompts.LOGIN_OK) {
            boolean availableName = databaseRelay.checkAvailability(username);
            if (availableName) {
                String passwordHash = passwordService.hashPassword(passwordInput);
                databaseRelay.addNewUser(username, passwordHash);
                setUser(username, passwordHash);
                return Prompts.LOGIN_OK;
            } else {
                return Prompts.NAME_TAKEN;
            }
        }
        else return lenghtValidation;
    }
    private void setUser(String username, String passwordHash) throws SQLException {
        int userId = databaseRelay.getUserId(username, passwordHash);
        User.Role role = User.Role.USER;
        if (databaseRelay.checkIfAdmin(userId)){
            role = User.Role.ADMIN;
        }
        this.user = new User(username, userId, role);
        loginMenu.setUser(user);
    }
    public Prompts changePassword(String username, String passwordInput, String newPasswordInput) throws SQLException {
        if (iv.checkTooShort(newPasswordInput, InputValidator.InputType.PASSWORD.PASSWORD) != Prompts.LENGTH_OK){
            return Prompts.SHORT_PASS;
        }
        else if (iv.checkTooLong(newPasswordInput, InputValidator.InputType.PASSWORD) != Prompts.LENGTH_OK){
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

    private Prompts addNote(Note note) throws SQLException {
        if (iv.checkTooLong(note.getTitle(), InputValidator.InputType.NOTE_TITLE) != Prompts.LENGTH_OK){
            return Prompts.LONG_TITLE;
        }
        else if (iv.checkTooShort(note.getTitle(), InputValidator.InputType.NOTE_TITLE) != Prompts.LENGTH_OK){
            return Prompts.SHORT_TITLE;
        }
        else if (iv.checkTooLong(note.getContents(), InputValidator.InputType.NOTE_TEXT) != Prompts.LENGTH_OK){
            return Prompts.LONG_TEXT;
        }
        else if (iv.checkTooShort(note.getContents(), InputValidator.InputType.NOTE_TEXT) != Prompts.LENGTH_OK) {
            return Prompts.SHORT_TEXT;
        }
        else{
            databaseRelay.addNote(user.getId(), note.getTitle(), note.getContents());
            return Prompts.NOTE_OK;
        }
    }
    public void readNotes() throws SQLException {
        List<Note> notes = databaseRelay.getNotes(user.getId());
        userMenu.showAllNotes(notes);
    }
    private void editNote(Note n) throws SQLException {
        databaseRelay.editNote(user.getId(), n);
    }
    private Prompts removeNote(Note n) throws SQLException {
        if (databaseRelay.removeNote(user.getId(), n, false)){
            return Prompts.NOTE_OK;
        }
        else return Prompts.ERROR;
    }
}