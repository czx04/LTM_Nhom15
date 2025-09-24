package db;

import java.sql.*;

public class UserDao {
    public UserDao() throws SQLException {
    }

    public boolean createUser(String username, String passwordPlain) throws SQLException {
        if (findUserIdByUsername(username) != null) {
            return false;
        }
        String insert = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection c = Connect.getConnection();
             PreparedStatement ps = c.prepareStatement(insert)) {
            ps.setString(1, username);
            ps.setString(2, passwordPlain);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean verifyLogin(String username, String passwordPlain) throws SQLException {
        String select = "SELECT password FROM users WHERE username = ?";
        try (Connection c = Connect.getConnection();
             PreparedStatement ps = c.prepareStatement(select)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                String pass = rs.getString(1);
                return pass.equals(passwordPlain);
            }
        }
    }

    public Integer findUserIdByUsername(String username) throws SQLException {
        String select = "SELECT id FROM users WHERE username = ?";
        try (Connection c = Connect.getConnection();
             PreparedStatement ps = c.prepareStatement(select)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return null;
            }
        }
    }

}


