package util;


import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketController {
    private static final ConcurrentHashMap<Socket, String> loggedInUsers = new ConcurrentHashMap<>();

    public static void addLoggedInUser(Socket sk, String user) {
        loggedInUsers.put(sk, user);
    }

    public static void removeLoggedInUser(Socket sk) {
        loggedInUsers.remove(sk);
    }

    public static String getUserBySocket(Socket sk) {
        return loggedInUsers.get(sk);
    }

    public static void clearLoggedInUsers() {
        loggedInUsers.clear();
    }

    public static Map<Socket, String> getLoggedInUsers() {
        return new ConcurrentHashMap<>(loggedInUsers);
    }
}

