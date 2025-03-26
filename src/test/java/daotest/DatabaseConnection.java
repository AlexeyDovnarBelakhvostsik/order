package daotest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static String url;
    private static String user;
    private static String password;

    public static void setTestConfig(String testUrl, String testUser, String testPassword) {
        url = testUrl;
        user = testUser;
        password = testPassword;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
