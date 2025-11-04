package util;

import server.ClientHandler;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class SocketController {
    private static final ConcurrentHashMap<ClientHandler, String> socketToUser = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ClientHandler> userToSocket = new ConcurrentHashMap<>();

    // Lưu trữ lời mời đang chờ: key = "invitor:invitee", value = true
    private static final ConcurrentHashMap<String, Boolean> pendingInvites = new ConcurrentHashMap<>();

    // Quản lý người chơi đang trong trận: key = username, value = matchId
    private static final ConcurrentHashMap<String, Integer> playersInMatch = new ConcurrentHashMap<>();

    // Quản lý thành viên của mỗi trận: key = matchId, value = Set<username>
    private static final ConcurrentHashMap<Integer, Set<String>> matchPlayers = new ConcurrentHashMap<>();

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

    // Quản lý lời mời
    public static void addPendingInvite(String invitor, String invitee) {
        String key = invitor + ":" + invitee;
        pendingInvites.put(key, true);
    }

    public static boolean hasPendingInvite(String invitor, String invitee) {
        String key = invitor + ":" + invitee;
        return pendingInvites.containsKey(key);
    }

    public static void removePendingInvite(String invitor, String invitee) {
        String key = invitor + ":" + invitee;
        pendingInvites.remove(key);
    }

    // Quản lý người chơi trong trận

    /**
     * Thêm người chơi vào trận đấu
     */
    public static void addPlayerToMatch(String username, int matchId) {
        playersInMatch.put(username, matchId);
        matchPlayers.computeIfAbsent(matchId, k -> ConcurrentHashMap.newKeySet());
        matchPlayers.get(matchId).add(username);
    }

    /**
     * Xóa người chơi khỏi trận đấu
     */
    public static void removePlayerFromMatch(String username) {
        Integer matchId = playersInMatch.remove(username);
        if (matchId != null) {
            Set<String> players = matchPlayers.get(matchId);
            if (players != null) {
                players.remove(username);
                // Nếu không còn ai trong trận, xóa entry
                if (players.isEmpty()) {
                    matchPlayers.remove(matchId);
                }
            }
        }
    }

    /**
     * Kiểm tra người chơi có đang trong trận không
     */
    public static boolean isPlayerInMatch(String username) {
        return playersInMatch.containsKey(username);
    }

    /**
     * Lấy matchId mà người chơi đang tham gia
     */
    public static Integer getPlayerMatchId(String username) {
        return playersInMatch.get(username);
    }

    /**
     * Lấy danh sách người chơi trong một trận
     */
    public static Set<String> getPlayersInMatch(int matchId) {
        Set<String> players = matchPlayers.get(matchId);
        return players != null ? new HashSet<>(players) : new HashSet<>();
    }

    /**
     * Lấy ClientHandler của đối thủ trong cùng trận
     */
    public static ClientHandler getOpponentInMatch(String username) {
        Integer matchId = playersInMatch.get(username);
        if (matchId == null) return null;

        Set<String> players = matchPlayers.get(matchId);
        if (players == null || players.size() != 2) return null;

        for (String player : players) {
            if (!player.equals(username)) {
                return getClientByUser(player);
            }
        }
        return null;
    }

    /**
     * Lấy username của đối thủ trong cùng trận
     */
    public static String getOpponentUsername(String username) {
        Integer matchId = playersInMatch.get(username);
        if (matchId == null) return null;

        Set<String> players = matchPlayers.get(matchId);
        if (players == null || players.size() != 2) return null;

        for (String player : players) {
            if (!player.equals(username)) {
                return player;
            }
        }
        return null;
    }

    /**
     * Xóa toàn bộ trận đấu và người chơi trong đó
     */
    public static void removeMatch(int matchId) {
        Set<String> players = matchPlayers.remove(matchId);
        if (players != null) {
            for (String player : players) {
                playersInMatch.remove(player);
            }
        }
    }
}

