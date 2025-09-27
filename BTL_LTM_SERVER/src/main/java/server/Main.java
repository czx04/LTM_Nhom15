package server;

import db.Connector;
import java.sql.SQLException;

public class Main {
    public static void main(String[] agrs) {
        try {
            Connector.init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        new Server().start(8081);
    }
}