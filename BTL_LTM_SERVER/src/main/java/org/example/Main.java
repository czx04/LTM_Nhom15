package org.example;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] agrs) {
        try (Connection con = db.Connect.getConnection();) {
            System.out.println("Connected to MySql Server.");
        } catch (SQLException ex) {
            System.out.println("Connection Error!");
            ex.printStackTrace();
        }
        new Server().start(8081);
    }
}