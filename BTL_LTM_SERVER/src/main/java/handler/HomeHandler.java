package handler;

import db.UserDao;
import db.MatchHistoryDao;
import server.ClientHandler;
import util.Logger;
import util.SocketController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class HomeHandler {

    private final UserDao userDao;
    private final MatchHistoryDao matchHistoryDao;

    public HomeHandler(UserDao userDao) {
        this.userDao = userDao;
        this.matchHistoryDao = new MatchHistoryDao();
    }

    public String getUserOnl(ClientHandler client) throws SQLException {
        Set<String> onlineUsers = new HashSet<>(SocketController.getLoggedInUsers().values());
        String currentUser = SocketController.getUserByClient(client);

        // Loại bỏ user hiện tại
        onlineUsers.remove(currentUser);

        // Tạo danh sách với thông tin trạng thái
        List<String> usersWithStatus = new ArrayList<>();

        for (String username : onlineUsers) {
            boolean inMatch = SocketController.isPlayerInMatch(username);
            String status = inMatch ? "IN_MATCH" : "AVAILABLE";
            // Format: username:status
            usersWithStatus.add(username + ":" + status);
        }

        Logger.info("Sending online users list to " + currentUser + ": " + usersWithStatus);

        return "USER_ONLINE|" + String.join(",", usersWithStatus);
    }

    public String sendInvite(ClientHandler invitorClient, String[] receiver) {
        ClientHandler client = SocketController.getClientByUser(receiver[1]);
        String invitor = SocketController.getUserByClient(invitorClient);
        if (client != null && invitor != null) {
            try {
                // Lưu lời mời đang chờ
                SocketController.addPendingInvite(invitor, receiver[1]);
                client.writeEvent(String.join("|", "INVITE", invitor));
            } catch (IOException e) {
                Logger.error(e.getMessage(), e);
                return "INVITE|NOT_OK";
            }
        }
        return "INVITE|OK";
    }

    public String handleInviteReject(ClientHandler inviteeClient, String[] parts) {
        try {
            if (parts.length < 2) {
                return "INVITE_REJECT|ERROR";
            }

            String invitor = parts[1];
            String invitee = SocketController.getUserByClient(inviteeClient);

            if (invitee == null) {
                return "INVITE_REJECT|ERROR";
            }

            // Xóa lời mời đang chờ
            SocketController.removePendingInvite(invitor, invitee);

            // Thông báo cho người mời
            ClientHandler invitorClient = SocketController.getClientByUser(invitor);
            if (invitorClient != null) {
                invitorClient.writeEvent("INVITE_REJECTED|" + invitee + " đã từ chối lời mời");
            }

            Logger.info(invitee + " rejected invite from " + invitor);

            return "INVITE_REJECT|OK";

        } catch (Exception e) {
            Logger.error("Error handling invite reject", e);
            return "INVITE_REJECT|ERROR";
        }
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

    public String getLeaderboard(String[] parts) {
        try {
            String q = (parts != null && parts.length >= 2) ? parts[1] : null;
            java.util.List<String[]> rows = userDao.getLeaderboardAll(q);
            java.util.List<String> mapped = new java.util.ArrayList<>();
            for (String[] r : rows) {
                // username:elo:total:matches
                mapped.add(String.join(":", r[0], r[1], r[2], r[3]));
            }
            return "RANK|" + String.join(",", mapped);
        } catch (Exception e) {
            Logger.error("getLeaderboard error", e);
            return "RANK|ERROR";
        }
    }

    public String getMatchHistory(ClientHandler client) {
        try {
            String username = SocketController.getUserByClient(client);
            if (username == null) {
                return "MATCH_HISTORY|ERROR";
            }

            java.util.List<String[]> rows = matchHistoryDao.getMatchHistoryByUsername(username);
            java.util.List<String> mapped = new java.util.ArrayList<>();
            for (String[] r : rows) {
                mapped.add(String.join("~", r[0], r[1], r[2], r[3], r[4], r[5]));
            }
            return "MATCH_HISTORY|" + String.join(",", mapped);
        } catch (Exception e) {
            Logger.error("getMatchHistory error", e);
            return "MATCH_HISTORY|ERROR";
        }
    }
}

