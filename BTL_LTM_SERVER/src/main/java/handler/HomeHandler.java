package handler;

import db.UserDao;
import db.MatchQuestionDao;
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

            client.writeEvent("ANSWER_RESULT|" + (correct ? "OK" : "FAIL") + "|calc=" + result);

            return null;
        } catch (Exception e) {
            Logger.error("Error checking answer", e);
            try {
                client.writeEvent("ANSWER_RESULT|ERROR|" + e.getMessage());
            } catch (IOException ignored) {}
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
