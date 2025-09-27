package handler;

import db.UserDao;

import java.util.Collection;

public class HomeHanlder {

    private final UserDao userDao;

    public HomeHanlder(UserDao userDao) {
        this.userDao = userDao;
    }

    public String getUserOnl(Collection<String> users) {
        java.util.Set<String> names = new java.util.LinkedHashSet<>(users);
        String payload = String.join(",", names);
        return payload;
    }

}
