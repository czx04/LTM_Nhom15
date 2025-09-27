package util;


import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketController {
    private static SocketController instance;
    private static ConcurrentHashMap<Socket, String> loggedInUsers;

    private SocketController() {
        loggedInUsers = new ConcurrentHashMap<>();
    }
    public static SocketController getInstance() {
        if (instance == null) {
            synchronized (SocketController.class) {
                instance = new SocketController();
            }
        }
        return instance;
    }

    public void addLoggedInUser(Socket sk, String user) {
        loggedInUsers.put(sk, user);
    }

    public void removeLoggedInUser(Socket sk) {
        loggedInUsers.remove(sk);
    }

    public String getUserBySocket(Socket sk) {
        return loggedInUsers.get(sk);
    }

    public void clearLoggedInUsers() {
        loggedInUsers.clear();
    }

    public Map<Socket, String> getLoggedInUsers() {
        return new ConcurrentHashMap<>(loggedInUsers);
    }
}
