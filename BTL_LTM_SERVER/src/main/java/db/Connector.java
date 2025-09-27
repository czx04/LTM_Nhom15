package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connector {
    private static Connection conn;
    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            init();
        }
        return conn;
    }
    public static void init() throws SQLException {
        if (conn == null || conn.isClosed()) {
            String url = System.getenv("DB_URL");
            String user = System.getenv("DB_USER");
            String password = System.getenv("DB_PASSWORD");
            conn = DriverManager.getConnection(url, user, password);
        }
    }
}