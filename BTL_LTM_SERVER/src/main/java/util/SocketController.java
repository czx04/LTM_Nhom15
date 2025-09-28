package util;

import server.ClientHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketController {
    private static final ConcurrentHashMap<ClientHandler, String> socketToUser = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ClientHandler> userToSocket = new ConcurrentHashMap<>();

    public static void addLoggedInUser(ClientHandler socket, String username) {
        socketToUser.put(socket, username);
        userToSocket.put(username, socket);
    }

    public static void removeLoggedInUser(ClientHandler socket) {
        String username = socketToUser.remove(socket);
        if (username != null) {
            userToSocket.remove(username);
        }
    }

    public static void removeLoggedInUser(String username) {
        ClientHandler socket = userToSocket.remove(username);
        if (socket != null) {
            socketToUser.remove(socket);
        }
    }

    public static String getUserByClient(ClientHandler socket) {
        return socketToUser.get(socket);
    }

    public static ClientHandler getClientByUser(String username) {
        return userToSocket.get(username);
    }

    public static void clearLoggedInUsers() {
        socketToUser.clear();
        userToSocket.clear();
    }

    public static Map<ClientHandler, String> getLoggedInUsers() {
        return new ConcurrentHashMap<>(socketToUser);
    }

    public static Map<String, ClientHandler> getUsersClients() {
        return new ConcurrentHashMap<>(userToSocket);
    }
}
