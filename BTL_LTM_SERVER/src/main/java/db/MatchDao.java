package db;

import java.sql.*;
import java.util.Random;
import util.Logger;

public class MatchDao {

    /**
     * Tạo một match mới và trả về match_id
     */
    public int createMatch() throws SQLException {
        String sql = "INSERT INTO matches (status) VALUES ('playing')";

        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int matchId = rs.getInt(1);
                        Logger.info("Created new match with ID: " + matchId);
                        return matchId;
                    }
                }
            }
        }
        throw new SQLException("Failed to create match");
    }

    /**
     * Thêm người chơi vào match_history
     */
    public void addPlayerToMatch(int matchId, int userId) throws SQLException {
        String sql = "INSERT INTO match_history (match_id, user_id, score) VALUES (?, ?, 0)";

        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, matchId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            Logger.info("Added user " + userId + " to match " + matchId);
        }
    }

    /**
     * Tạo câu hỏi cho match
     */
    public void generateQuestionsForMatch(int matchId, int numQuestions) throws SQLException {
        String sql = "INSERT INTO match_questions (match_id, target_value, numbers, allowed_ops) VALUES (?, ?, ?, ?)";

        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            Random rand = new Random();

            for (int i = 0; i < numQuestions; i++) {
                // Tạo số target ngẫu nhiên từ 10-100
                int target = rand.nextInt(91) + 10;

                // Tạo mảng 6 số ngẫu nhiên từ 1-20
                int[] numbers = new int[6];
                for (int j = 0; j < 6; j++) {
                    numbers[j] = rand.nextInt(20) + 1;
                }

                // Convert mảng thành JSON string
                String numbersJson = "[" + numbers[0];
                for (int j = 1; j < numbers.length; j++) {
                    numbersJson += "," + numbers[j];
                }
                numbersJson += "]";

                stmt.setInt(1, matchId);
                stmt.setInt(2, target);
                stmt.setString(3, numbersJson);
                stmt.setString(4, "+-*/");
                stmt.executeUpdate();
            }

            Logger.info("Generated " + numQuestions + " questions for match " + matchId);
        }
    }

    /**
     * Cập nhật điểm cho người chơi trong match
     */
    public void updatePlayerScore(int matchId, int userId, int score) throws SQLException {
        String sql = "UPDATE match_history SET score = ? WHERE match_id = ? AND user_id = ?";

        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, score);
            stmt.setInt(2, matchId);
            stmt.setInt(3, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Kết thúc match và xác định winner
     */
    public void finishMatch(int matchId, int winnerId) throws SQLException {
        String sql = "UPDATE matches SET status = 'finished', winner_id = ? WHERE match_id = ?";

        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, winnerId);
            stmt.setInt(2, matchId);
            stmt.executeUpdate();

            // Cập nhật is_winner trong match_history
            String updateHistorySql = "UPDATE match_history SET is_winner = TRUE WHERE match_id = ? AND user_id = ?";
            try (PreparedStatement historyStmt = conn.prepareStatement(updateHistorySql)) {
                historyStmt.setInt(1, matchId);
                historyStmt.setInt(2, winnerId);
                historyStmt.executeUpdate();
            }

            Logger.info("Match " + matchId + " finished. Winner: " + winnerId);
        }
    }
}

