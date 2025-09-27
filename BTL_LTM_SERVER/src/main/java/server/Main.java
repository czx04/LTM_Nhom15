package server;

import db.Connector;
import io.github.cdimascio.dotenv.Dotenv;
import util.Logger;
import java.sql.SQLException;

public class Main {
    public static void main(String[] agrs) {
        Dotenv dotenv = Dotenv.load();
        Connector.initConnector(dotenv);
        new Server(Logger.getInstance()).start(8081);
    }
}