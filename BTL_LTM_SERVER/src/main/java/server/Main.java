package server;

import db.Connector;
import io.github.cdimascio.dotenv.Dotenv;

public class Main {
    public static void main(String[] agrs) {
        Dotenv dotenv = Dotenv.load();
        Connector.initConnector(dotenv);
        new Server().start(8081);
    }
}