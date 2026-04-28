package View.AdminIO;

import Control.Enums.AdminAction;
import Control.AppManager;
import Model.DataObjects.LogPost;
import View.*;
import java.sql.SQLException;
import java.util.List;

public class AdminLogsMenu {
    private final ConsoleOutput co;
    private final ConsoleInput ci;
    private final AdminMenu am;
    private final AppManager appManager;

    public AdminLogsMenu(ConsoleOutput co, ConsoleInput ci, AdminMenu am, AppManager appManager) {
        this.co = co;
        this.ci = ci;
        this.am = am;
        this.appManager = appManager;
    }

    public void seeLogins() throws SQLException {
        while (true) {
            co.printHeader("Log in history");
            System.out.println(logMenuPrompt + am.returnToMain +  am.submit);
            String choice = ci.lowCaseInput();
            if (ci.checkIfQuit(choice)){
                return;
            }
            switch (choice){
                case "1"-> {
                    appManager.handleAdminAction(AdminAction.SEE_ALL_LOGIN_LOGS, -1);
                    return;
                }
                case "2", "3" -> {
                    showLogSubmenu(choice);
                    return;
                }
                default -> co.promptInvalid();
            }
        }
    }

    private void showLogSubmenu(String choice) throws SQLException {
        if (ci.checkIfQuit(choice)){
            return;
        }
        while (true) {
            if (choice.equalsIgnoreCase("2")) {
                System.out.println(am.userNamePrompt + "log in history");
                String username = ci.lowCaseInput();
                int userId = appManager.getUserIdForAdminUse(username);
                if (userId != -1) {
                    appManager.handleAdminAction(AdminAction.SEE_LOGIN_FOR_USER, userId);
                    return;
                }
            } else if (choice.equalsIgnoreCase("3")) {
                System.out.println(loginStatusPrompt + am.returnToMain + am.submit);
                String status = ci.lowCaseInput();
                if (ci.checkIfQuit(status)) {
                    return;
                }
                if (ci.checkIfValidChoice(List.of("1", "2", "3"), status)) {
                    appManager.handleAdminAction(AdminAction.SEE_LOGIN_FOR_STATUS, ci.parseInput(status));
                    return;
                }
            }
            co.promptInvalid();
        }
    }

    public void showLogPosts(List<LogPost> logPosts){
        co.printHeader("List of login posts:");
        for (LogPost log : logPosts){
            System.out.println(co.getLogPostString(log));
        }
    }
    private final String logMenuPrompt = "-- Would you like to:\n- 1: See all log in history\n- 2: See log in history for a specific user\n- 3: See log in with specific status\n";
    private final String loginStatusPrompt = "-- Which log in status would you like to get the history for?\n- 1: Success\n- 2: Fail\n- 3: Unknown user\n";
}