package View.AdminIO;

import Control.Enums.AdminAction;
import Control.*;
import Model.DataObjects.User;
import View.*;
import java.sql.SQLException;
import java.util.List;

public class AdminUserMenu {
    private final ConsoleOutput co;
    private final ConsoleInput ci;
    private final AdminMenu am;
    private final AppManager appManager;

    public AdminUserMenu(ConsoleOutput co, ConsoleInput ci, AdminMenu am, AppManager appManager){
        this.co = co;
        this.ci = ci;
        this.am = am;
        this.appManager = appManager;
    }
    public void displayUsers(Event event) throws SQLException {
        if (!ci.checkValidAction(event.getPrompts())) {
            System.out.println(event.getPrompts());
            return;
        }
        co.printHeader("Users");
        List<User> users = ci.getUsersFromEvent(event);
        if (users.isEmpty()) {
            System.out.println(noUsers);
            return;
        }
        System.out.println(usersInfo);
        for (User u : users) {
            System.out.println(co.getUserPostString(u, false));
        }
        System.out.println(userNumberPrompt + am.returnToMain);
        String choice = ci.lowCaseInput();
        if (ci.checkIfQuit(choice)) {
            return;
        }
        displayUserSubmenu(choice, users);
    }

    private void displayUserSubmenu(String input, List<User> users) throws SQLException {
        int userIdInput = ci.parseInput(input);
        User u = ci.getUserFromList(userIdInput, users);
        if (u == null){
            co.promptInvalid();
            return;
        }
        while (true){
            System.out.println(co.getUserPostString(u, false));
            System.out.println(userSubmenuPrompt + am.returnToMain + am.submit);
            String choice = ci.lowCaseInput();
            if (ci.checkIfQuit(choice)) {
                return;
            }
            switch (choice) {
                case "1" -> {
                    appManager.handleAdminAction(AdminAction.SEE_NOTES, userIdInput);
                    return;
                }
                case "2" -> {
                    appManager.handleAdminAction(AdminAction.SEE_LOGIN_FOR_USER, userIdInput);
                    return;
                }
                default -> co.promptInvalid();
            }
        }
    }
    private final String userSubmenuPrompt = "-- Would you like to:\n- 1: See notes \n- 2: See log in history\n";
    private final String noUsers = "-- No users have been registered.";
    private final String userNumberPrompt = "-- Enter the number of a user in order to view their notes or their log in history\n";
    private final String usersInfo = "-- The following users are currently registered:\n";
}