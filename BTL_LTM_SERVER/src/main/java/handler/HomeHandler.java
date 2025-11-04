package handler;

import db.UserDao;
import db.MatchQuestionDao;
import db.MatchDao;
import model.MatchQuestion;
import server.ClientHandler;
import util.Logger;
import util.SocketController;

import java.io.IOException;
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

        return "USER_ONLINE|" + String.join(",", names) + "|" + String.join(",", allUsers);
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

    public String handleInviteAccept(ClientHandler inviteeClient, String[] parts) {
        try {
            if (parts.length < 2) {
                return "INVITE_ACCEPT|ERROR";
            }

            String invitor = parts[1];
            String invitee = SocketController.getUserByClient(inviteeClient);

            if (invitee == null) {
                return "INVITE_ACCEPT|ERROR";
            }

            // Kiểm tra xem có lời mời đang chờ không
            if (!SocketController.hasPendingInvite(invitor, invitee)) {
                inviteeClient.writeEvent("INVITE_ACCEPT|ERROR|Lời mời đã hết hạn");
                return "INVITE_ACCEPT|ERROR";
            }

            // Xóa lời mời đang chờ
            SocketController.removePendingInvite(invitor, invitee);

            // Kiểm tra xem có ai đang trong trận không
            if (SocketController.isPlayerInMatch(invitor)) {
                inviteeClient.writeEvent("INVITE_ACCEPT|ERROR|" + invitor + " đang trong trận khác");
                return "INVITE_ACCEPT|ERROR";
            }
            if (SocketController.isPlayerInMatch(invitee)) {
                inviteeClient.writeEvent("INVITE_ACCEPT|ERROR|Bạn đang trong trận khác");
                return "INVITE_ACCEPT|ERROR";
            }

            // Lấy user_id của cả 2 người
            Integer invitorId = userDao.findUserIdByUsername(invitor);
            Integer inviteeId = userDao.findUserIdByUsername(invitee);

            if (invitorId == null || inviteeId == null) {
                inviteeClient.writeEvent("INVITE_ACCEPT|ERROR|Không tìm thấy người chơi");
                return "INVITE_ACCEPT|ERROR";
            }

            // Tạo match mới
            MatchDao matchDao = new MatchDao();
            int matchId = matchDao.createMatch();

            // Thêm cả 2 người vào match trong database
            matchDao.addPlayerToMatch(matchId, invitorId);
            matchDao.addPlayerToMatch(matchId, inviteeId);

            // Thêm vào quản lý trạng thái in-memory
            SocketController.addPlayerToMatch(invitor, matchId);
            SocketController.addPlayerToMatch(invitee, matchId);

            // Tạo câu hỏi cho match (5 câu hỏi)
            matchDao.generateQuestionsForMatch(matchId, 5);

            // Lấy câu hỏi
            MatchQuestionDao questionDao = new MatchQuestionDao();
            List<MatchQuestion> questions = questionDao.getQuestionsByMatchId(matchId);

            // Chuyển danh sách câu hỏi sang JSON-like string
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

            String matchData = String.join("|",
                    "MATCH_START",
                    "match_id=" + matchId,
                    "questions=" + sb,
                    "time=03:00",
                    "opponent=" + invitee,
                    "scoreOpponent=0");

            String matchDataInvitee = String.join("|",
                    "MATCH_START",
                    "match_id=" + matchId,
                    "questions=" + sb,
                    "time=03:00",
                    "opponent=" + invitor,
                    "scoreOpponent=0");

            // Gửi MATCH_START cho cả 2 người
            ClientHandler invitorClient = SocketController.getClientByUser(invitor);
            if (invitorClient != null) {
                invitorClient.writeEvent(matchData);
            }
            inviteeClient.writeEvent(matchDataInvitee);

            Logger.info("Match " + matchId + " created between " + invitor + " and " + invitee);

            return "INVITE_ACCEPT|OK";

        } catch (Exception e) {
            Logger.error("Error handling invite accept", e);
            try {
                inviteeClient.writeEvent("INVITE_ACCEPT|ERROR|" + e.getMessage());
            } catch (IOException ignored) {}
            return "INVITE_ACCEPT|ERROR";
        }
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

    public String getMatchDisplay(ClientHandler client, String[] parts) {
        try {
            int matchId = 1; // có thể truyền từ client sau
            MatchQuestionDao dao = new MatchQuestionDao();
            List<MatchQuestion> questions = dao.getQuestionsByMatchId(matchId);

            if (questions.isEmpty()) {
                client.writeEvent("MATCH_ERROR|Không tìm thấy câu hỏi cho match_id=" + matchId);
                return "JOIN_MATCH|ERROR";
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
            String username = SocketController.getUserByClient(client);

            int result = evaluateExpression(expr);
            boolean correct = (result == target);

            // Gửi kết quả cho người chơi
            client.writeEvent("ANSWER_RESULT|" + (correct ? "OK" : "FAIL") + "|calc=" + result);

            // Nếu đúng, thông báo cho đối thủ
            if (correct && username != null) {
                ClientHandler opponent = SocketController.getOpponentInMatch(username);
                if (opponent != null) {
                    opponent.writeEvent("OPPONENT_SCORED|" + username);
                }
            }

            return null;
        } catch (Exception e) {
            Logger.error("Error checking answer", e);
            try {
                client.writeEvent("ANSWER_RESULT|ERROR|" + e.getMessage());
            } catch (IOException ignored) {}
            return null;
        }
    }

    /**
     * Xử lý khi người chơi rời khỏi match (tự nguyện hoặc disconnect)
     */
    public String handleLeaveMatch(ClientHandler client) {
        try {
            String username = SocketController.getUserByClient(client);
            if (username == null) {
                return "LEAVE_MATCH|ERROR";
            }

            Integer matchId = SocketController.getPlayerMatchId(username);
            if (matchId == null) {
                return "LEAVE_MATCH|ERROR|Bạn không trong trận nào";
            }

            // Thông báo cho đối thủ
            ClientHandler opponent = SocketController.getOpponentInMatch(username);
            String opponentName = SocketController.getOpponentUsername(username);

            if (opponent != null) {
                opponent.writeEvent("OPPONENT_LEFT|" + username + " đã rời khỏi trận");
            }

            // Xóa người chơi khỏi trạng thái
            SocketController.removePlayerFromMatch(username);
            if (opponentName != null) {
                SocketController.removePlayerFromMatch(opponentName);
            }

            Logger.info("Player " + username + " left match " + matchId);

            return "LEAVE_MATCH|OK";

        } catch (Exception e) {
            Logger.error("Error handling leave match", e);
            return "LEAVE_MATCH|ERROR";
        }
    }

    /**
     * Xử lý khi trận đấu kết thúc
     */
    public String handleFinishMatch(ClientHandler client, String[] parts) {
        try {
            String username = SocketController.getUserByClient(client);
            if (username == null) {
                return "FINISH_MATCH|ERROR";
            }

            Integer matchId = SocketController.getPlayerMatchId(username);
            if (matchId == null) {
                return "FINISH_MATCH|ERROR|Bạn không trong trận nào";
            }

            // Lấy điểm từ parts nếu có
            int myScore = parts.length >= 2 ? Integer.parseInt(parts[1]) : 0;

            // Lấy thông tin đối thủ
            String opponentName = SocketController.getOpponentUsername(username);
            ClientHandler opponent = SocketController.getOpponentInMatch(username);

            // Cập nhật điểm trong database
            Integer myUserId = userDao.findUserIdByUsername(username);
            if (myUserId != null) {
                MatchDao matchDao = new MatchDao();
                matchDao.updatePlayerScore(matchId, myUserId, myScore);
            }

            // Xóa người chơi khỏi trạng thái
            SocketController.removePlayerFromMatch(username);
            if (opponentName != null) {
                SocketController.removePlayerFromMatch(opponentName);
            }

            Logger.info("Match " + matchId + " finished for " + username);

            return "FINISH_MATCH|OK";

        } catch (Exception e) {
            Logger.error("Error handling finish match", e);
            return "FINISH_MATCH|ERROR";
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
                if (current.isEmpty()) {
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
        if (current.isEmpty()) {
            throw new IllegalArgumentException("Thiếu toán hạng sau toán tử cuối cùng");
        }

        numbers.add(Double.parseDouble(current.toString()));

        // Thực hiện nhân chia trước
        for (int i = 0; i < ops.size(); ) {
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
}

