package UI;

import controller.HomeController;
import model.UserOnlineInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class UsersListPanel extends JPanel {
    HomeController homeController;
    BufferedReader in;
    BufferedWriter out;
    private List<UserOnlineInfo> onlineUsers = new ArrayList<>();

    public UsersListPanel(HomeController homeController, BufferedReader in, BufferedWriter out) {
        this.homeController = homeController;
        this.in = in;
        this.out = out;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
    }

    // Method cũ để tương thích - deprecated
    @Deprecated
    public void setUsers(List<String> users, List<String> allUsers) {
        // Chuyển đổi sang format mới (tất cả đều AVAILABLE vì đây là format cũ)
        List<UserOnlineInfo> userInfoList = new ArrayList<>();
        for (String user : users) {
            userInfoList.add(new UserOnlineInfo(user, "AVAILABLE"));
        }
        setUsersWithStatus(userInfoList);
    }

    // Method mới để nhận danh sách với trạng thái
    public void setUsersWithStatus(List<UserOnlineInfo> users) {
        this.onlineUsers = new ArrayList<>(users);
        refreshUI();
    }

    // Cập nhật trạng thái của một user cụ thể
    public void updateUserStatus(String username, String status) {
        boolean found = false;
        for (UserOnlineInfo user : onlineUsers) {
            if (user.getUsername().equals(username)) {
                user.setStatus(status);
                found = true;
                break;
            }
        }

        // Nếu status là OFFLINE, xóa khỏi danh sách
        if ("OFFLINE".equals(status)) {
            onlineUsers.removeIf(u -> u.getUsername().equals(username));
        }
        // Nếu status là ONLINE hoặc AVAILABLE và chưa có trong danh sách
        else if (("ONLINE".equals(status) || "AVAILABLE".equals(status)) && !found) {
            onlineUsers.add(new UserOnlineInfo(username, "AVAILABLE"));
        }

        refreshUI();
    }

    private void refreshUI() {
        removeAll();

        if (onlineUsers.isEmpty()) {
            JPanel emptyCard = createUserCard();
            emptyCard.setLayout(new BorderLayout());
            emptyCard.setBorder(new EmptyBorder(30, 20, 30, 20));

            JLabel emptyLabel = new JLabel("Không có người chơi online", SwingConstants.CENTER);
            emptyLabel.setForeground(new Color(148, 163, 184));
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            emptyCard.add(emptyLabel, BorderLayout.CENTER);

            add(emptyCard);
        } else {
            for (UserOnlineInfo user : onlineUsers) {
                add(createUserRow(user));
                add(Box.createVerticalStrut(10));
            }
        }

        revalidate();
        repaint();
    }

    private JPanel createUserCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(0, 0, 0, 6));
                g2d.fillRoundRect(2, 3, getWidth() - 4, getHeight() - 3, 14, 14);

// Card background
// ĐÂY LÀ CHỖ CẦN SỬA
// Thay vì dùng Color.WHITE, hãy dùng new Color với 4 tham số (R, G, B, Alpha)
                g2d.setColor(new Color(255, 255, 255, 100)); // <-- SỬA DÒNG NÀY

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            }
        };
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        return card;
    }

    private JPanel createUserRow(UserOnlineInfo user) {
        JPanel card = createUserCard();
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Left panel - Avatar + Info
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);

        // Avatar circle
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient circle
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(99, 110, 250),
                        getWidth(), getHeight(), new Color(139, 92, 246)
                );
                g2d.setPaint(gradient);
                g2d.fillOval(2, 2, 44, 44);
            }
        };
        avatarPanel.setPreferredSize(new Dimension(48, 48));
        avatarPanel.setOpaque(false);
        avatarPanel.setLayout(new BorderLayout());

        JLabel avatarLabel = new JLabel("👤", SwingConstants.CENTER);
        avatarLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        avatarPanel.add(avatarLabel, BorderLayout.CENTER);

        // Name + Status
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(user.getUsername());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nameLabel.setForeground(new Color(30, 41, 59));

        JLabel statusLabel = new JLabel();
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        if (user.isInMatch()) {
            statusLabel.setText("🎮 Đang trong trận");
            statusLabel.setForeground(new Color(245, 158, 11));
        } else {
            statusLabel.setText("🟢 Sẵn sàng");
            statusLabel.setForeground(new Color(16, 185, 129));
        }

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(statusLabel);

        leftPanel.add(avatarPanel);
        leftPanel.add(infoPanel);

        // Right panel - Invite button
        JButton inviteBtn = createInviteButton(user);

        card.add(leftPanel, BorderLayout.WEST);
        card.add(inviteBtn, BorderLayout.EAST);

        return card;
    }

    private JButton createInviteButton(UserOnlineInfo user) {
        JButton button = new JButton() {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isEnabled()) {
                    // Shadow
                    if (isHovered) {
                        g2d.setColor(new Color(0, 0, 0, 15));
                        g2d.fillRoundRect(1, 2, getWidth() - 2, getHeight() - 2, 10, 10);
                    }

                    // Button background
                    Color bgColor = isHovered ? new Color(5, 150, 105) : new Color(16, 185, 129);
                    g2d.setColor(bgColor);
                } else {
                    g2d.setColor(new Color(226, 232, 240));
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                super.paintComponent(g);
            }
        };

        button.setPreferredSize(new Dimension(100, 38));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));

        if (user.isAvailable()) {
            button.setText("⚔️ Mời");
            button.setForeground(Color.WHITE);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.addActionListener(e -> {
                try {
                    homeController.sendInvite(user.getUsername(), in, out);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            });

            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    try {
                        java.lang.reflect.Field field = button.getClass().getDeclaredField("isHovered");
                        field.setAccessible(true);
                        field.set(button, true);
                        button.repaint();
                    } catch (Exception ignored) {}
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    try {
                        java.lang.reflect.Field field = button.getClass().getDeclaredField("isHovered");
                        field.setAccessible(true);
                        field.set(button, false);
                        button.repaint();
                    } catch (Exception ignored) {}
                }
            });
        } else {
            button.setText("Đang chơi");
            button.setForeground(new Color(100, 116, 139));
            button.setEnabled(false);
        }

        return button;
    }
}
