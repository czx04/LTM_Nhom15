package handler;

import db.UserDao;

import java.sql.SQLException;

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
}
