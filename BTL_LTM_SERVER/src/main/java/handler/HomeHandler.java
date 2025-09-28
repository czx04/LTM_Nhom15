package handler;

import db.UserDao;
import server.ClientHandler;
import util.Logger;
import util.SocketController;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class HomeHandler {

    private final UserDao userDao;

    public HomeHandler(UserDao userDao) {
        this.userDao = userDao;
    }

    public String getUserOnl(ClientHandler client) {
        Set<String> names = new HashSet<>(SocketController.getLoggedInUsers().values());
        String currentUser = SocketController.getUserByClient(client);
        names.remove(currentUser);
        return "USER_ONLINE|" + String.join(",",names);
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
}
