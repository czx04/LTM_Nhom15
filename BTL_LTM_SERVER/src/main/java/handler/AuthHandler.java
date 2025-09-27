package handler;

import db.UserDao;

import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AuthHandler {
    private final UserDao userDao;

    public AuthHandler(UserDao userDao) {
        this.userDao = userDao;
    }



    public String handleRegister(String[] parts) throws SQLException {
        if (parts.length < 3) return "SAI FORMAT";
        String username = parts[1];
        String password = parts[2];
        boolean ok = userDao.createUser(username, password);
        return ok ? "REGISTED" : "EXIST";
    }

    public String handleLogin(String[] parts,Socket clientSocket,Map<Socket, String> loggedInUsers) throws SQLException {
        if (parts.length < 3) return "SAI FORMAT";
        System.out.println(loggedInUsers.get(clientSocket));
        String username = parts[1];
        String password = parts[2];
        boolean ok = userDao.verifyLogin(username, password);
        if (ok) {
            loggedInUsers.put(clientSocket, username);
            System.out.println(clientSocket);
            return "LOGGEDIN";
        } else {
            return "FAILLOGIN";
        }
    }
}
