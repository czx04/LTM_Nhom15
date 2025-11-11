package handler;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.example.Client;

import util.Constants;

public class EventHandler {

    public void handleInvite(Client client, String[] parts) {
        if (parts.length > 1) {
            if (parts[1].equals("OK") || parts[1].equals("NOT_OK")) {
                return;
            }
            String invitor = parts[1];
            SwingUtilities.invokeLater(() -> {
                int choice = JOptionPane.showConfirmDialog(
                        client.frame,
                        invitor + " đã mời bạn solo. Bạn có chấp nhận không?",
                        "Lời mời từ " + invitor,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (choice == JOptionPane.YES_OPTION) {
                    // TODO: gửi phản hồi ACCEPT về server
                    try {
                        client.out.write("INVITE_ACCEPT|" + invitor);
                        client.out.newLine();
                        client.out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // TODO: gửi phản hồi REJECT về server
                    try {
                        client.out.write("INVITE_REJECT|" + invitor);
                        client.out.newLine();
                        client.out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void handleInviteRejected(Client client, String[] parts) {
        // format: INVITE_REJECTED|message
        if (parts.length >= 2) {
            String message = parts[1];
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        client.frame,
                        message,
                        "Lời mời bị từ chối",
                        JOptionPane.INFORMATION_MESSAGE);
            });
        }
    }

    public void handleLoginResponse(Client client, String[] response) {
        if (Constants.RESPONSE_LOGGEDIN.equals(response[1])) {
            System.out.println("LOGIN OK");
            // Lưu username vào client
            client.currentUsername = response[2];
            try {
                Class<?> homeClass = Class.forName("UI.Home");
                Object home = homeClass.getDeclaredConstructor().newInstance();
                homeClass.getMethod("showHome", javax.swing.JFrame.class,
                        java.io.BufferedReader.class,
                        java.io.BufferedWriter.class,
                        String.class,
                        controller.HomeController.class)
                        .invoke(home, client.frame, client.in, client.out, response[2], client.homeController);
            } catch (Exception e) {
                System.err.println("Lỗi khi navigate đến Home screen: " + e.getMessage());
                e.printStackTrace();
                showError(Constants.MSG_CONNECTION_ERROR);
            }
        } else {
            showWarning(Constants.MSG_LOGIN_FAILED);
        }
    }

    public void handleRegisterResponse(Client client, String[] response) {
        if (Constants.RESPONSE_REGISTED.equals(response[1])) {
            // Lưu username vào client
            client.currentUsername = response[2];
            // Navigate to Home screen
            try {
                Class<?> homeClass = Class.forName("UI.Home");
                Object home = homeClass.getDeclaredConstructor().newInstance();
                homeClass.getMethod("showHome", javax.swing.JFrame.class,
                        java.io.BufferedReader.class,
                        java.io.BufferedWriter.class,
                        String.class,
                        controller.HomeController.class)
                        .invoke(home, client.frame, client.in, client.out, response[2], client.homeController);
            } catch (Exception e) {
                showError(Constants.MSG_CONNECTION_ERROR);
            }
        } else if (Constants.RESPONSE_EXIST.equals(response[1])) {
            showWarning(response[2] + " " + Constants.MSG_USER_EXISTS);
        } else {
            showWarning(response[2] + " " + Constants.MSG_INVALID_FORMAT);
        }
    }

    public void handleLogoutResponse(Client client, String[] response) {
        if (response != null && response[0].startsWith(Constants.RESPONSE_LOGOUT)) {
            // Navigate back to Login screen
            try {
                Class<?> loginClass = Class.forName("UI.Login");
                Object login = loginClass.getDeclaredConstructor().newInstance();
                loginClass.getMethod("showLogin", javax.swing.JFrame.class,
                        java.io.BufferedReader.class,
                        java.io.BufferedWriter.class)
                        .invoke(login, client.frame, client.in, client.out);
            } catch (Exception e) {
                showError(Constants.MSG_CONNECTION_ERROR);
            }
        } else {
            showError(Constants.MSG_LOGOUT_FAILED);
        }
    }

    public java.util.List<String> parseUsersOnline(Client client, String[] response) {
        java.util.List<String> users = new ArrayList<>();
        java.util.List<model.UserOnlineInfo> userInfoList = new ArrayList<>();

        System.out.println("Parsing users online: " + java.util.Arrays.toString(response));

        if (response.length > 1 && response[1] != null && !response[1].trim().isEmpty()) {
            String body = response[1];
            String[] userArray = body.split(",");

            for (String userEntry : userArray) {
                if (!userEntry.trim().isEmpty()) {
                    // Parse format: username:status
                    String[] parts = userEntry.split(":");
                    if (parts.length == 2) {
                        String username = parts[0].trim();
                        String status = parts[1].trim();
                        userInfoList.add(new model.UserOnlineInfo(username, status));
                        users.add(username); // Để tương thích với code cũ
                    } else {
                        // Format cũ - không có status
                        userInfoList.add(new model.UserOnlineInfo(userEntry.trim(), "AVAILABLE"));
                        users.add(userEntry.trim());
                    }
                }
            }
        } else {
            System.out.println("No users online data in response");
        }

        System.out.println("Parsed " + userInfoList.size() + " users with status");

        SwingUtilities.invokeLater(() -> {
            try {
                if (client.homeController != null) {
                    System.out.println("Calling homeController.onUsersOnlineReceivedWithStatus");
                    client.homeController.onUsersOnlineReceivedWithStatus(userInfoList);
                } else {
                    System.out.println("homeController is null!");
                }
            } catch (Exception e) {
                System.err.println("Error updating users online: " + e.getMessage());
                e.printStackTrace();
            }
        });
        return users;
    }

    public void handleUserStatus(Client client, String[] parts) {
        // format: USER_STATUS|username|status
        // status có thể là: ONLINE, OFFLINE, AVAILABLE, IN_MATCH
        if (parts.length >= 3) {
            String username = parts[1];
            String status = parts[2];

            System.out.println("Received USER_STATUS: " + username + " -> " + status);

            SwingUtilities.invokeLater(() -> {
                if (client != null && client.homeController != null) {
                    try {
                        // Cập nhật trạng thái của user trong UI
                        client.homeController.updateUserStatus(username, status);
                    } catch (Exception e) {
                        System.err.println("Error updating user status: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void handleConnectionError() {
        showError(Constants.MSG_CONNECTION_ERROR);
    }

    public static void handleLoadUsersError() {
        showError(Constants.MSG_LOAD_USERS_ERROR);
    }

    private static void showWarning(String message) {
        JOptionPane.showMessageDialog(null, message,
                Constants.TITLE_WARNING,
                JOptionPane.WARNING_MESSAGE);
    }

    private static void showError(String message) {
        JOptionPane.showMessageDialog(null, message,
                Constants.TITLE_ERROR,
                JOptionPane.ERROR_MESSAGE);
    }

    public void handleRank(Client client, String[] parts) {
        // format: RANK|username:elo:total:matches,username2:elo:total:matches
        java.util.List<String[]> rows = new java.util.ArrayList<>();
        if (parts.length >= 2 && parts[1] != null && !"ERROR".equals(parts[1])) {
            String[] entries = parts[1].split(",");
            for (String entry : entries) {
                if (entry == null || entry.isEmpty())
                    continue;
                String[] cols = entry.split(":");
                if (cols.length >= 4) {
                    rows.add(new String[] { cols[0], cols[1], cols[2], cols[3] });
                }
            }
        }
        SwingUtilities.invokeLater(() -> {
            try {
                Class<?> rankClass = Class.forName("UI.Rank");
                Object rank = rankClass.getDeclaredConstructor().newInstance();
                rankClass.getMethod("showRank", javax.swing.JFrame.class,
                        java.io.BufferedReader.class,
                        java.io.BufferedWriter.class,
                        java.util.List.class)
                        .invoke(rank, client.frame, client.in, client.out, rows);
            } catch (Exception e) {
                e.printStackTrace();
                handleConnectionError();
            }
        });
    }

    public void handleJoinMatch(Client client, String[] parts) {
        // format: JOIN_MATCH|OK
        if (parts.length >= 2 && "OK".equalsIgnoreCase(parts[1])) {
            System.out.println("Join match success — waiting for MATCH_START data...");
        } else {
            System.out.println("Join match failed!");
        }
    }

    public void handleMatchStart(Client client, String[] parts) {
        // format: MATCH_START|match_id=1|questions=[{...}]|time=03:00|scoreOpponent=0
        try {
            String matchId = parts[1].split("=")[1];
            String questionsJson = parts[2].substring(parts[2].indexOf('=') + 1);
            String time = parts[3].split("=")[1];
            String scoreOpponent = parts[4].split("=")[1];

            // 🔹 Dùng 1 instance duy nhất
            UI.MatchUI matchUI = new UI.MatchUI();
            client.currentUI = matchUI;

            // 🔹 Set username cho MatchUI
            if (client.currentUsername != null) {
                matchUI.setUsername(client.currentUsername);
            }

            // 🔹 Hiển thị giao diện trên luồng Swing
            SwingUtilities.invokeLater(() -> {
                matchUI.showMatch(client.frame, client.in, client.out,
                        matchId, questionsJson, time, scoreOpponent);
            });

            System.out.println("Match started: " + matchId);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi khởi tạo trận đấu: " + e.getMessage());
        }
    }

    public void handleAnswerResult(Client client, String[] parts) {
        // format: ANSWER_RESULT|OK hoặc ANSWER_RESULT|FAIL
        if (parts.length < 2)
            return;

        boolean correct = "OK".equalsIgnoreCase(parts[1]);

        if (client.currentUI instanceof UI.MatchUI matchUI) {
            if (correct) {
                matchUI.increaseMyScore();
                JOptionPane.showMessageDialog(null, "🎉 Chính xác! +1 điểm");
            } else {
                matchUI.notifyWrong();
            }
        }
    }

    public void handleMatchEnd(Client client, String[] parts) {
        // format:
        // MATCH_END|winner=username|winnerScore=5|loser=username2|loserScore=3|eloChange=20
        // hoặc: MATCH_END|draw=true|score=5|eloChange=0
        try {
            if (parts.length < 2) {
                JOptionPane.showMessageDialog(null, "Lỗi nhận kết quả trận đấu");
                return;
            }

            StringBuilder resultMessage = new StringBuilder();
            resultMessage.append("=== KẾT QUẢ TRẬN ĐẤU ===\n\n");

            // Parse các trường kết quả
            String winner = null;
            String loser = null;
            int winnerScore = 0;
            int loserScore = 0;
            int eloChange = 0;
            boolean isDraw = false;

            for (int i = 1; i < parts.length; i++) {
                String[] keyValue = parts[i].split("=");
                if (keyValue.length != 2)
                    continue;

                String key = keyValue[0];
                String value = keyValue[1];

                switch (key) {
                    case "winner":
                        winner = value;
                        break;
                    case "winnerScore":
                        winnerScore = Integer.parseInt(value);
                        break;
                    case "loser":
                        loser = value;
                        break;
                    case "loserScore":
                        loserScore = Integer.parseInt(value);
                        break;
                    case "eloChange":
                        eloChange = Integer.parseInt(value);
                        break;
                    case "draw":
                        isDraw = "true".equalsIgnoreCase(value);
                        break;
                    case "score":
                        winnerScore = Integer.parseInt(value);
                        break;
                }
            }

            // Hiển thị kết quả
            if (isDraw) {
                resultMessage.append("🤝 HÒA!\n\n");
                resultMessage.append("Điểm: ").append(winnerScore).append("\n");
                resultMessage.append("Thay đổi ELO: ").append(eloChange >= 0 ? "+" : "").append(eloChange);
            } else if (winner != null) {
                resultMessage.append("🏆 NGƯỜI THẮNG: ").append(winner).append("\n");
                resultMessage.append("   Điểm: ").append(winnerScore).append("\n\n");
                resultMessage.append("😢 NGƯỜI THUA: ").append(loser).append("\n");
                resultMessage.append("   Điểm: ").append(loserScore).append("\n\n");
                resultMessage.append("Thay đổi ELO của bạn: ").append(eloChange >= 0 ? "+" : "").append(eloChange);
            } else {
                resultMessage.append("Trận đấu kết thúc!");
            }

            // Dừng timer nếu đang chạy
            if (client.currentUI instanceof UI.MatchUI matchUI) {
                matchUI.stopTimer();
            }

            // Hiển thị kết quả
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, resultMessage.toString(),
                        "Kết Quả Trận Đấu", JOptionPane.INFORMATION_MESSAGE);
            });

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi hiển thị kết quả: " + e.getMessage());
        }
    }

    public void handleOpponentScored(Client client, String[] parts) {
        // format: OPPONENT_SCORED|username
        if (parts.length < 2)
            return;

        String opponentName = parts[1];

        if (client.currentUI instanceof UI.MatchUI matchUI) {
            SwingUtilities.invokeLater(() -> {
                matchUI.increaseOpponentScore();
                System.out.println("Đối thủ " + opponentName + " đã ghi điểm!");
            });
        }
    }

    public void handleOpponentLeft(Client client, String[] parts) {
        // format: OPPONENT_LEFT|message
        if (parts.length < 2)
            return;

        String message = parts[1];

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    client.frame,
                    message + "\nBạn thắng do đối thủ rời trận!",
                    "Đối thủ rời trận",
                    JOptionPane.INFORMATION_MESSAGE);

            // Có thể quay về màn hình Home hoặc cho người chơi tiếp tục
            if (client.currentUI instanceof UI.MatchUI matchUI) {
                // Đánh dấu là thắng do đối thủ rời trận
                System.out.println("Match ended - opponent left");
            }
        });
    }

}
