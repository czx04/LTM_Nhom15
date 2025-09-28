package handler;

import db.UserDao;
import server.ClientHandler;
import util.SocketController;

import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.SocketHandler;

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
        return "REGISTER|" + (ok ? "REGISTED" : "EXIST") + "|" + username;
    }
    public String handleLogin(String[] parts, ClientHandler clientSocket) throws SQLException {
        if (parts.length < 3) return "SAI FORMAT";
        System.out.println(SocketController.getLoggedInUsers());
        String username = parts[1];
        String password = parts[2];
        boolean ok = userDao.verifyLogin(username, password);
        if (ok) {
            SocketController.addLoggedInUser(clientSocket, username);
            System.out.println(clientSocket);
            return "LOGIN|LOGGEDIN" + "|" + username;
        } else {
            return "LOGIN|FAILLOGIN" + "|" + username;
        }
    }

    public String handleLogout(ClientHandler clientSocket) throws SQLException {
        SocketController.removeLoggedInUser(clientSocket);
        return "LOGOUT";
    }
}
