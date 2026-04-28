package View.UserIO;

import java.sql.SQLException;

@FunctionalInterface
public interface SQLRunnableVoid {
    void run() throws SQLException;
}