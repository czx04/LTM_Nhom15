package UI;

import controller.HomeController;
import util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;

public class Home extends BaseUI {
    private String username;
    private HomeController externalHomeController;

    public void showHome(JFrame frame, BufferedReader in, BufferedWriter out, String username,
            HomeController homeController) {
        setupFrame(frame, in, out);
        this.username = username;
        this.externalHomeController = homeController;
        showUI(frame, in, out);
    }

    public void showUI(JFrame frame, BufferedReader in, BufferedWriter out) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(240, 242, 245));

        // ==== LEFT PANEL ====
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(240, 242, 245));
        leftPanel.setBorder(new EmptyBorder(25, 25, 25, 15));

        // Header với gradient và shadow
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(99, 110, 250),
                    0, getHeight(), new Color(139, 92, 246)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(40, 30, 40, 30));
        headerPanel.setPreferredSize(new Dimension(0, 160));

        JLabel welcomeTitle = new JLabel("🎯 Đố Chữ Online");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeTitle.setForeground(Color.WHITE);
        welcomeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel usernameLabel = new JLabel("Chào mừng, " + username + "!");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        usernameLabel.setForeground(new Color(255, 255, 255, 200));
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(welcomeTitle);
        headerPanel.add(Box.createVerticalStrut(12));
        headerPanel.add(usernameLabel);

        // Content panel với card style
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Info card
        JPanel infoCard = createModernCard();
        infoCard.setLayout(new BorderLayout(15, 15));
        infoCard.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel guideIcon = new JLabel("📖");
        guideIcon.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        guideIcon.setVerticalAlignment(SwingConstants.TOP);

        JTextArea welcomeBody = new JTextArea();
        welcomeBody.setEditable(false);
        welcomeBody.setLineWrap(true);
        welcomeBody.setWrapStyleWord(true);
        welcomeBody.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcomeBody.setBackground(Color.WHITE);
        welcomeBody.setForeground(new Color(71, 85, 105));
        welcomeBody.setBorder(null);
        welcomeBody.setText(
                "Hướng dẫn chơi:\n\n" +
                        "• Xem danh sách người chơi online bên phải\n" +
                        "• Mời người chơi có trạng thái 🟢 Sẵn sàng\n" +
                        "• Người có trạng thái 🎮 đang trong trận\n" +
                        "• Giải đố nhanh để ghi điểm cao\n" +
                        "• Leo rank và trở thành cao thủ!");

        infoCard.add(guideIcon, BorderLayout.WEST);
        infoCard.add(welcomeBody, BorderLayout.CENTER);

        // Play button với hiệu ứng hiện đại
        JButton joinMatchBtn = createModernButton("🎮 Vào trận ngay", new Color(16, 185, 129), new Color(5, 150, 105));
        joinMatchBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        joinMatchBtn.setPreferredSize(new Dimension(220, 56));
        joinMatchBtn.setMaximumSize(new Dimension(220, 56));
        joinMatchBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(infoCard);
        contentPanel.add(Box.createVerticalStrut(25));
        contentPanel.add(joinMatchBtn);
        contentPanel.add(Box.createVerticalGlue());

        leftPanel.add(headerPanel, BorderLayout.NORTH);
        leftPanel.add(contentPanel, BorderLayout.CENTER);

        // ==== RIGHT PANEL ====
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(240, 242, 245));
        rightPanel.setBorder(new EmptyBorder(25, 15, 25, 25));

        // Right header card
        JPanel rightHeaderCard = createModernCard();
        rightHeaderCard.setLayout(new BorderLayout(10, 0));
        rightHeaderCard.setBorder(new EmptyBorder(18, 22, 18, 22));

        JLabel onlineLabel = new JLabel("👥 Người chơi Online");
        onlineLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        onlineLabel.setForeground(new Color(30, 41, 59));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);

        JButton rankBtn = createSmallModernButton("🏆 BXH", new Color(59, 130, 246));
        JButton logoutBtn = createSmallModernButton("🚪 Thoát", new Color(239, 68, 68));

        buttonPanel.add(rankBtn);
        buttonPanel.add(logoutBtn);

        rightHeaderCard.add(onlineLabel, BorderLayout.WEST);
        rightHeaderCard.add(buttonPanel, BorderLayout.EAST);

        // Users list container
        JPanel usersContainer = new JPanel(new BorderLayout());
        usersContainer.setOpaque(false);
        usersContainer.setBorder(new EmptyBorder(15, 0, 0, 0));

        HomeController controllerToUse = externalHomeController != null ? externalHomeController : homeController;

        UsersListPanel usersPanel = new UsersListPanel(controllerToUse, in, out);
        controllerToUse.setUsersPanel(usersPanel);

        JScrollPane scrollPane = new JScrollPane(usersPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        usersContainer.add(scrollPane, BorderLayout.CENTER);

        // Footer với refresh button
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton refreshBtn = createModernButton("🔄 Làm mới", new Color(99, 110, 250), new Color(79, 90, 230));
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refreshBtn.setPreferredSize(new Dimension(0, 44));

        footerPanel.add(refreshBtn, BorderLayout.CENTER);

        JPanel rightContent = new JPanel(new BorderLayout());
        rightContent.setOpaque(false);
        rightContent.add(rightHeaderCard, BorderLayout.NORTH);
        rightContent.add(usersContainer, BorderLayout.CENTER);
        rightContent.add(footerPanel, BorderLayout.SOUTH);

        rightPanel.add(rightContent, BorderLayout.CENTER);

        // ==== SPLIT ====
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.50);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(0);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);
        container.add(splitPane, BorderLayout.CENTER);

        // ==== ACTIONS ====
        refreshBtn.addActionListener(e -> {
            refreshBtn.setEnabled(false);
            refreshBtn.setText("⏳ Đang tải...");
            controllerToUse.getUsersOnline(in, out);
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    SwingUtilities.invokeLater(() -> {
                        refreshBtn.setEnabled(true);
                        refreshBtn.setText("🔄 Làm mới");
                    });
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
        });
        refreshBtn.doClick();

        logoutBtn.addActionListener(e -> authController.handleLogout(in, out));
        rankBtn.addActionListener(e -> controllerToUse.getRank(in, out, ""));
        joinMatchBtn.addActionListener(e -> controllerToUse.joinMatch(in, out));

        refreshFrame(container);
    }

    // Helper methods để tạo các component hiện đại
    private JPanel createModernCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2d.setColor(new Color(0, 0, 0, 8));
                g2d.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, 16, 16);
                g2d.fillRoundRect(1, 3, getWidth() - 2, getHeight() - 2, 16, 16);

                // Card background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            }
        };
        card.setOpaque(false);
        return card;
    }

    private JButton createModernButton(String text, Color color, Color hoverColor) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                if (isEnabled()) {
                    g2d.setColor(new Color(0, 0, 0, isHovered ? 25 : 15));
                    g2d.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, 12, 12);
                }

                // Button background
                if (isEnabled()) {
                    g2d.setColor(isHovered ? hoverColor : color);
                } else {
                    g2d.setColor(new Color(203, 213, 225));
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                super.paintComponent(g);
            }
        };

        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

        return button;
    }

    private JButton createSmallModernButton(String text, Color color) {
        JButton button = createModernButton(text, color, color.darker());
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(90, 32));
        return button;
    }
}
