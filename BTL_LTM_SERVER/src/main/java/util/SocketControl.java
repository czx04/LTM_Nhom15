package util;


import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SocketControl {
    private static Map<Socket, String> loggedInUsers;

    public static void init(){
        if (loggedInUsers == null){
            loggedInUsers = new HashMap<>();
        }
    }

    public Map<Socket, String> getLoggedInUsers() {
        return loggedInUsers;
    }
    public void addLoggedInUsers(Socket sk, String user) {
        loggedInUsers.put(sk, user);
    }

    public void removeLoggedInUsers(Socket sk) {
        loggedInUsers.remove(sk);
    }

    public String getUserBySocket(Socket sk){
        return loggedInUsers.get(sk);
    }

    public void clearLoggedInUsers(){
        loggedInUsers.clear();
    }
}
