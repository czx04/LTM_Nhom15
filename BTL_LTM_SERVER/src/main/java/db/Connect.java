package db;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Connect {
    private static Connection connection;
    private static String url = "jdbc:mysql://localhost:3312/nam4";
    private static String user = "root";
    private static String password = "123456";

    public static Connection getConnection() throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
        return connection;
    }

    public static Connection getConnection(String dbConfigFile) throws SQLException {
        try (FileInputStream f = new FileInputStream(dbConfigFile)) {
            Properties pros = new Properties();
            pros.load(f);

            url = pros.getProperty("url");
            user = pros.getProperty("user");
            password = pros.getProperty("password");
            return getConnection();
        } catch (IOException e) {
            return null;
        }
    }
}