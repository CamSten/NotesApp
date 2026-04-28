import Control.AppManager;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args)  throws SQLException {
        AppManager appManager = new AppManager();
        appManager.run();
    }
}