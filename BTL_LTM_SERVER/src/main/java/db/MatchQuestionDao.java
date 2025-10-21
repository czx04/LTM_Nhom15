package db;

import model.MatchQuestion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchQuestionDao {
    public List<MatchQuestion> getQuestionsByMatchId(int matchId) throws SQLException {
        String sql = """
                    SELECT match_question_id, match_id, target_value, numbers, allowed_ops
                    FROM match_questions
                    WHERE match_id = ?
                    ORDER BY match_question_id
                """;

        List<MatchQuestion> list = new ArrayList<>();
        try (Connection conn = Connector.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, matchId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MatchQuestion q = new MatchQuestion();
                    q.setId(rs.getInt("match_question_id"));
                    q.setMatchId(rs.getInt("match_id"));
                    q.setTargetValue(rs.getInt("target_value"));
                    q.setNumbers(rs.getString("numbers"));
                    q.setAllowedOps(rs.getString("allowed_ops"));
                    list.add(q);
                }
            }
        }
        return list;
    }
}
