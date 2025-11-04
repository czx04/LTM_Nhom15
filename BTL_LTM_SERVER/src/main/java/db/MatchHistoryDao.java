package db;

import java.sql.*;

public class MatchHistoryDao {

    /**
     * Lưu kết quả trận đấu của một người chơi vào match_history
     */
    public boolean saveMatchResult(int matchId, int userId, int score, int eloChange,
            int timeTaken, boolean isWinner) throws SQLException {
        String sql = "INSERT INTO match_history (match_id, user_id, score, elo_change, time_taken, is_winner) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Connector.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, matchId);
            ps.setInt(2, userId);
            ps.setInt(3, score);
            ps.setInt(4, eloChange);
            ps.setInt(5, timeTaken);
            ps.setBoolean(6, isWinner);

            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Cập nhật ELO và total_score của người chơi
     */
    public boolean updateUserStats(int userId, int eloChange, int scoreGained) throws SQLException {
        String sql = "UPDATE users SET elo_rating = elo_rating + ?, total_score = total_score + ? " +
                "WHERE user_id = ?";

        try (Connection conn = Connector.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eloChange);
            ps.setInt(2, scoreGained);
            ps.setInt(3, userId);

            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Cập nhật trạng thái trận đấu thành finished và người thắng
     */
    public boolean finishMatch(int matchId, Integer winnerId) throws SQLException {
        String sql = "UPDATE matches SET status = 'finished', winner_id = ? WHERE match_id = ?";

        try (Connection conn = Connector.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            if (winnerId != null) {
                ps.setInt(1, winnerId);
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setInt(2, matchId);

            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Tạo một trận đấu mới
     */
    public int createMatch() throws SQLException {
        String sql = "INSERT INTO matches (status) VALUES ('playing')";

        try (Connection conn = Connector.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("Không thể tạo match mới");
            }
        }
    }
}
