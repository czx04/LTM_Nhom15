package db;

import util.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    public UserDao() throws SQLException {
    }

    public boolean createUser(String username, String passwordPlain) throws SQLException {
        if (findUserIdByUsername(username) != null) {
            return false;
        }
        String insert = "INSERT INTO users(username, password_hash) VALUES(?, ?)";
        try (Connection c = Connector.getConnection();
             PreparedStatement ps = c.prepareStatement(insert)) {
            ps.setString(1, username);
            ps.setString(2, passwordPlain);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean verifyLogin(String username, String passwordPlain) throws SQLException {
        String select = "SELECT password_hash FROM users WHERE username = ?";
        try (Connection c = Connector.getConnection();
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
        String select = "SELECT user_id FROM users WHERE username = ?";
        try (Connection c = Connector.getConnection();
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

    public List<String> getAllUsers() throws SQLException {
        String sql = "SELECT username FROM users";
        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<String> usernames = new ArrayList<>();
            while (rs.next()) {
                usernames.add(rs.getString(1));
            }
            return usernames;
        }
    }

}