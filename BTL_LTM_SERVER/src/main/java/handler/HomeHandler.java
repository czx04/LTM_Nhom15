package handler;

import db.UserDao;
import db.MatchQuestionDao;
import db.MatchHistoryDao;
import model.MatchQuestion;
import server.ClientHandler;
import util.Logger;
import util.SocketController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class HomeHandler {

    private final UserDao userDao;
    private final MatchHistoryDao matchHistoryDao;

    // Track các trận đấu đang diễn ra: matchId -> Map<username, score>
    private final Map<Integer, Map<String, Integer>> activeMatches = new HashMap<>();
    // Track thời gian bắt đầu trận: matchId -> startTime
    private final Map<Integer, Long> matchStartTimes = new HashMap<>();

    public HomeHandler(UserDao userDao) {
        this.userDao = userDao;
        this.matchHistoryDao = new MatchHistoryDao();
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

        return "USER_ONLINE|" + String.join(",", names) + "|" + String.join(",", allUsers);
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

    public String getMatchDisplay(ClientHandler client, String[] parts) {
        try {
            int matchId = 1; // có thể truyền từ client sau
            MatchQuestionDao dao = new MatchQuestionDao();
            List<MatchQuestion> questions = dao.getQuestionsByMatchId(matchId);

            if (questions.isEmpty()) {
                client.writeEvent("MATCH_ERROR|Không tìm thấy câu hỏi cho match_id=" + matchId);
                return "JOIN_MATCH|ERROR";
            }

            // Khởi tạo tracking cho trận đấu này
            String username = SocketController.getUserByClient(client);
            if (username != null) {
                activeMatches.putIfAbsent(matchId, new HashMap<>());
                activeMatches.get(matchId).put(username, 0);
                matchStartTimes.putIfAbsent(matchId, System.currentTimeMillis());
            }

            // Chuyển danh sách sang JSON-like string
            StringBuilder sb = new StringBuilder();
            sb.append("[");

            for (int i = 0; i < questions.size(); i++) {
                MatchQuestion q = questions.get(i);
                sb.append(String.format(
                        "{\"id\":%d,\"target\":%d,\"numbers\":\"%s\",\"ops\":\"%s\"}",
                        q.getId(),
                        q.getTargetValue(),
                        q.getNumbers().replaceAll("[\\[\\]\\s]", ""),
                        q.getAllowedOps()));
                if (i < questions.size() - 1)
                    sb.append(",");
            }
            sb.append("]");

            // Gửi toàn bộ question list về client
            client.writeEvent(String.join("|",
                    "MATCH_START",
                    "match_id=" + matchId,
                    "questions=" + sb,
                    "time=03:00",
                    "scoreOpponent=0"));

            return "JOIN_MATCH|OK";

        } catch (Exception e) {
            Logger.error("getMatchDisplay error", e);
            return "JOIN_MATCH|ERROR|" + e.getMessage();
        }
    }

    public String checkAnswer(ClientHandler client, String[] parts) {
        try {
            String expr = parts[1];
            int target = Integer.parseInt(parts[2]);

            int result = evaluateExpression(expr);
            boolean correct = (result == target);

            // Nếu đúng, cộng điểm cho người chơi
            if (correct) {
                String username = SocketController.getUserByClient(client);
                if (username != null) {
                    int matchId = 1; // TODO: có thể lấy từ context
                    Map<String, Integer> scores = activeMatches.get(matchId);
                    if (scores != null && scores.containsKey(username)) {
                        scores.put(username, scores.get(username) + 1);
                    }
                }
            }

            client.writeEvent("ANSWER_RESULT|" + (correct ? "OK" : "FAIL") + "|calc=" + result);

            return null;
        } catch (Exception e) {
            Logger.error("Error checking answer", e);
            try {
                client.writeEvent("ANSWER_RESULT|ERROR|" + e.getMessage());
            } catch (IOException ignored) {
            }
            return null;
        }
    }

    private int evaluateExpression(String expr) {
        expr = expr.replaceAll("\\s+", ""); // bỏ khoảng trắng

        if (expr.isEmpty()) {
            throw new IllegalArgumentException("Biểu thức rỗng!");
        }

        List<Double> numbers = new ArrayList<>();
        List<Character> ops = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (char c : expr.toCharArray()) {
            if (Character.isDigit(c)) {
                current.append(c);
            } else if ("+-*/".indexOf(c) >= 0) {
                // Không cho phép 2 toán tử liền nhau
                if (current.length() == 0) {
                    throw new IllegalArgumentException("Thiếu toán hạng trước toán tử '" + c + "'");
                }
                numbers.add(Double.parseDouble(current.toString()));
                current.setLength(0);
                ops.add(c);
            } else {
                throw new IllegalArgumentException("Ký tự không hợp lệ: " + c);
            }
        }

        // Kiểm tra kết thúc biểu thức có hợp lệ không
        if (current.length() == 0) {
            throw new IllegalArgumentException("Thiếu toán hạng sau toán tử cuối cùng");
        }

        numbers.add(Double.parseDouble(current.toString()));

        // Thực hiện nhân chia trước
        for (int i = 0; i < ops.size();) {
            char op = ops.get(i);
            if (op == '*' || op == '/') {
                double a = numbers.get(i);
                double b = numbers.get(i + 1);
                if (op == '/' && b == 0) {
                    throw new ArithmeticException("Không thể chia cho 0");
                }
                double res = (op == '*') ? a * b : a / b;
                numbers.set(i, res);
                numbers.remove(i + 1);
                ops.remove(i);
            } else {
                i++;
            }
        }

        // Cộng trừ
        double result = numbers.get(0);
        for (int i = 0; i < ops.size(); i++) {
            char op = ops.get(i);
            double val = numbers.get(i + 1);
            result = (op == '+') ? result + val : result - val;
        }

        return (int) Math.round(result);
    }

    /**
     * Lấy username từ user_id (helper method)
     */
    private String getUsernameById(int userId) {
        try {
            String sql = "SELECT username FROM users WHERE user_id = ?";
            try (java.sql.Connection conn = db.Connector.getConnection();
                    java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("username");
                    }
                }
            }
        } catch (Exception e) {
            Logger.error("Error getting username by id: " + userId, e);
        }
        return null;
    }

    /**
     * Xử lý khi trận đấu kết thúc
     * Format: MATCH_END|matchId|username|score
     */
    public String handleMatchEnd(ClientHandler client, String[] parts) {
        try {
            int matchId = Integer.parseInt(parts[1]);
            String username = parts[2];
            int myScore = Integer.parseInt(parts[3]);

            // Kiểm tra username có hợp lệ không
            String loggedUser = SocketController.getUserByClient(client);
            Logger.info("MATCH_END: username from client=" + username + ", loggedUser=" + loggedUser);

            if (loggedUser == null) {
                Logger.error("User not logged in! Username: " + username);
                return "MATCH_END|ERROR|User not logged in";
            }

            if (!username.equals(loggedUser)) {
                Logger.error("Username mismatch! Client sent: " + username + ", Expected: " + loggedUser);
                return "MATCH_END|ERROR|Invalid user";
            }

            // Lấy thông tin trận đấu
            Map<String, Integer> scores = activeMatches.get(matchId);
            if (scores == null) {
                return "MATCH_END|ERROR|Match not found";
            }

            // Cập nhật điểm của người chơi hiện tại
            scores.put(username, myScore);

            // Tính thời gian đã chơi (giây)
            Long startTime = matchStartTimes.get(matchId);
            int timeTaken = 180; // Mặc định 3 phút
            if (startTime != null) {
                timeTaken = (int) ((System.currentTimeMillis() - startTime) / 1000);
            }

            // Lấy user ID
            Integer userId = userDao.findUserIdByUsername(username);
            if (userId == null) {
                return "MATCH_END|ERROR|User not found";
            }

            // Xác định người thắng và tính ELO
            String winner = null;
            String loser = null;
            int winnerScore = myScore;
            int loserScore = 0;

            // TODO: Mock data đối thủ (sẽ thay bằng logic thật sau)
            String opponentUsername = null;
            int opponentScore = 0;

            // Tìm đối thủ trong activeMatches
            boolean foundOpponent = false;
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                if (!entry.getKey().equals(username)) {
                    opponentUsername = entry.getKey();
                    opponentScore = entry.getValue();
                    foundOpponent = true;
                    Logger.info("Found opponent in activeMatches: " + opponentUsername + ", score=" + opponentScore);
                    break;
                }
            }

            // Nếu không tìm thấy đối thủ thật, dùng mock data để test
            if (!foundOpponent) {
                Logger.info("No opponent found in activeMatches, using mock data");
                // Mock: Lấy username của user_id = 2 từ database
                try {
                    opponentUsername = getUsernameById(2); // Mock user_id = 2
                    if (opponentUsername == null) {
                        opponentUsername = "nguyenth"; // Fallback nếu không tìm thấy
                    }
                    opponentScore = 1; // Mock điểm đối thủ = 1
                    foundOpponent = true;
                    Logger.info("Mock opponent: " + opponentUsername + " (user_id=2), score=" + opponentScore);
                } catch (Exception e) {
                    Logger.error("Error getting mock opponent", e);
                }
            }

            // So sánh điểm
            if (foundOpponent && opponentUsername != null) {
                if (myScore > opponentScore) {
                    winner = username;
                    loser = opponentUsername;
                    loserScore = opponentScore;
                    winnerScore = myScore;
                } else if (myScore < opponentScore) {
                    winner = opponentUsername;
                    loser = username;
                    loserScore = myScore;
                    winnerScore = opponentScore;
                } else {
                    // Hòa
                    winner = null;
                    loser = null;
                }
                Logger.info("Match result: winner=" + winner + ", winnerScore=" + winnerScore +
                        ", loser=" + loser + ", loserScore=" + loserScore);
            } else {
                // Không có đối thủ, người chơi hiện tại tự động thắng
                winner = username;
                winnerScore = myScore;
                loser = null;
                loserScore = 0;
                Logger.info("No opponent, player wins by default");
            }

            // Tính ELO change (đơn giản: thắng +20, thua -10, hòa 0)
            int eloChange = 0;
            boolean isWinner = false;
            if (winner != null && winner.equals(username)) {
                eloChange = 20;
                isWinner = true;
            } else if (loser != null && loser.equals(username)) {
                eloChange = -10;
                isWinner = false;
            }

            // Lưu kết quả của người chơi hiện tại vào database
            matchHistoryDao.saveMatchResult(matchId, userId, myScore, eloChange, timeTaken, isWinner);
            matchHistoryDao.updateUserStats(userId, eloChange, myScore);

            // Lưu kết quả của đối thủ (nếu có)
            if (opponentUsername != null) {
                Integer opponentId = userDao.findUserIdByUsername(opponentUsername);
                if (opponentId != null) {
                    int opponentEloChange = 0;
                    boolean opponentIsWinner = false;

                    if (winner != null && winner.equals(opponentUsername)) {
                        opponentEloChange = 20;
                        opponentIsWinner = true;
                    } else if (loser != null && loser.equals(opponentUsername)) {
                        opponentEloChange = -10;
                        opponentIsWinner = false;
                    }

                    matchHistoryDao.saveMatchResult(matchId, opponentId, opponentScore,
                            opponentEloChange, timeTaken, opponentIsWinner);
                    matchHistoryDao.updateUserStats(opponentId, opponentEloChange, opponentScore);
                    Logger.info("Saved opponent match result: " + opponentUsername +
                            ", score=" + opponentScore + ", eloChange=" + opponentEloChange);
                }
            }

            // Đánh dấu trận đấu là finished
            Integer winnerId = null;
            if (winner != null) {
                winnerId = userDao.findUserIdByUsername(winner);
            }
            matchHistoryDao.finishMatch(matchId, winnerId);

            // Xóa tracking
            activeMatches.remove(matchId);
            matchStartTimes.remove(matchId);

            // Gửi kết quả về client
            String result;
            if (winner != null) {
                result = String.format("MATCH_END|winner=%s|winnerScore=%d|loser=%s|loserScore=%d|eloChange=%d",
                        winner, winnerScore, loser, loserScore, eloChange);
            } else {
                result = String.format("MATCH_END|draw=true|score=%d|eloChange=%d", myScore, eloChange);
            }

            try {
                client.writeEvent(result);
            } catch (IOException e) {
                Logger.error("Error sending match result", e);
            }

            return null;

        } catch (Exception e) {
            Logger.error("Error handling match end", e);
            try {
                client.writeEvent("MATCH_END|ERROR|" + e.getMessage());
            } catch (IOException ignored) {
            }
            return null;
        }
    }

}
