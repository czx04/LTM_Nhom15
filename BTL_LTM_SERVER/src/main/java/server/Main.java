package server;

import db.Connector;
import util.Logger;
import util.SocketControl;
import java.sql.SQLException;

public class Main {
    public static void main(String[] agrs) {
        try {
            Connector.init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        new Server(Logger.getInstance(), SocketControl.init()).start(8081);
    }
}