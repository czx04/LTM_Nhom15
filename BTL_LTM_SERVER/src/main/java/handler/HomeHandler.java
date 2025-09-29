package handler;

import db.UserDao;
import server.ClientHandler;
import util.Logger;
import util.SocketController;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;

public class HomeHandler {

    private final UserDao userDao;

    public HomeHandler(UserDao userDao) {
        this.userDao = userDao;
    }

    public String getUserOnl(ClientHandler client) throws SQLException {
        Set<String> names = new HashSet<>(SocketController.getLoggedInUsers().values());
        String currentUser = SocketController.getUserByClient(client);
        System.out.println(names);
        names.remove(currentUser);
        List<String> allUsers = userDao.getAllUsers();
        allUsers.remove(currentUser);
        allUsers.removeAll(names);
        System.out.println(names);
        System.out.println(allUsers);

        return "USER_ONLINE|" + String.join(",",names) + "|" + String.join(",",allUsers);
    }

    public String sendInvite(ClientHandler invitorClient, String[] receiver) {
        ClientHandler client = SocketController.getClientByUser(receiver[1]);
        String invitor = SocketController.getUserByClient(invitorClient);
        if (client != null && invitor != null) {
            try {
                client.writeEvent(String.join("|", "INVITE", invitor));
            } catch (IOException e) {
                Logger.error(e.getMessage(), e);
                return "INVITE|NOT_OK";
            }
        }
        return "INVITE|OK";
    }

    public void broadcastUserStatus(String username, String status) {
        Map<String, ClientHandler> clients = SocketController.getUsersClients();
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            String otherUser = entry.getKey();
            ClientHandler ch = entry.getValue();
            if (!otherUser.equals(username) && ch != null) {
                try {
                    ch.writeEvent(String.join("|", "USER_STATUS", username, status));
                } catch (IOException e) {
                    Logger.error("Broadcast error: " + e.getMessage(), e);
                }
            }
        }
    }
}
