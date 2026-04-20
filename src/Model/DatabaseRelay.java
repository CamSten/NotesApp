package Model;

import Control.AppManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseRelay {
    private static Connection c;
    private static AppManager appManager;

    public DatabaseRelay(AppManager appManager) throws SQLException {
        this.appManager = appManager;
        this.c = DriverManager.getConnection(PropertyRetriever.getUrl(), PropertyRetriever.getUser(), PropertyRetriever.getPassword());

    }
}
