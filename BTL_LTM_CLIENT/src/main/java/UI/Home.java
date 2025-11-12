package UI;


import controller.HomeController;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.URL;


public class Home extends BaseUI {
    private String username;
    private HomeController externalHomeController;


    // Color scheme từ Login UI
    private static final Color PRIMARY_BLUE = new Color(38, 97, 187);
    private static final Color DARK_BLUE = new Color(4, 39, 227);
    private static final Color LIGHT_BLUE = new Color(200, 230, 255);
    private static final Color TEXT_DARK = new Color(0, 0, 0);
    private static final Color BORDER_GRAY = new Color(180, 180, 190);
    private static final Color HOVER_BLUE = new Color(15, 115, 215);
    private static final Color TRANSPARENT_WHITE = new Color(255, 255, 255, 150);
    private static final Color LIGHT_GRAY = new Color(240, 242, 245);


    public void showHome(JFrame frame, BufferedReader in, BufferedWriter out, String username,
                         HomeController homeController) {
        setupFrame(frame, in, out);
        this.username = username;
        this.externalHomeController = homeController;
        showUI(frame, in, out);
    }


    public void showUI(JFrame frame, BufferedReader in, BufferedWriter out) {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    URL imageUrl = new URL("https://cdn2.fptshop.com.vn/unsafe/Uploads/images/tin-tuc/184109/Originals/kho-hinh-anh-anime-background-1.png");
                    Image img = new ImageIcon(imageUrl).getImage();
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);


                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                    g2d.setColor(new Color(0, 0, 0, 80));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                } catch (Exception e) {
                    g.setColor(LIGHT_GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setOpaque(true);


        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);


        // ==== LEFT PANEL - Main Content ====
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setBorder(new EmptyBorder(20, 20, 20, 10));


        JPanel headerCard = createModernCard();
        headerCard.setLayout(new BoxLayout(headerCard, BoxLayout.Y_AXIS));
        headerCard.setBorder(new EmptyBorder(35, 30, 35, 30));
        headerCard.setPreferredSize(new Dimension(0, 150));


        JLabel welcomeTitle = new JLabel("Tính nhanh đối kháng");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeTitle.setForeground(PRIMARY_BLUE);
        welcomeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);


        JLabel usernameLabel = new JLabel("Chào mừng, " + username + "!");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        usernameLabel.setForeground(TEXT_DARK);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        headerCard.add(welcomeTitle);
        headerCard.add(Box.createVerticalStrut(12));
        headerCard.add(usernameLabel);


        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(15, 0, 0, 0));


        // Info card
        JPanel infoCard = createModernCard();
        infoCard.setLayout(new BorderLayout(15, 15));
        infoCard.setBorder(new EmptyBorder(22, 22, 22, 22));


//        JLabel guideIcon = new JLabel("📖");
//        guideIcon.setFont(new Font("Segoe UI", Font.PLAIN, 32));
//        guideIcon.setVerticalAlignment(SwingConstants.TOP);


        // ...
        JTextArea welcomeBody = new JTextArea();
        welcomeBody.setEditable(false);
        welcomeBody.setLineWrap(true);
        welcomeBody.setWrapStyleWord(true);
        welcomeBody.setFont(new Font("Segoe UI", Font.PLAIN, 13));

// welcomeBody.setBackground(Color.WHITE); // <-- XÓA HOẶC CHÚ THÍCH DÒNG NÀY
        welcomeBody.setOpaque(false); // <-- THÊM DÒNG NÀY

        welcomeBody.setForeground(TEXT_DARK);
        welcomeBody.setBorder(null);
        welcomeBody.setText(
                "Hướng dẫn chơi:\n\n" +
                        "• Xem danh sách người chơi online bên phải\n" +
                        "• Mời người chơi có trạng thái Sẵn sàng\n" +
                        "• Người có trạng thái đang trong trận\n" +
                        "• Giải đố nhanh để ghi điểm cao\n" +
                        "• Leo rank và trở thành cao thủ!");


//        infoCard.add(guideIcon, BorderLayout.WEST);
        infoCard.add(welcomeBody, BorderLayout.CENTER);


//        JButton joinMatchBtn = createModernButton("🎮 Vào trận ngay", PRIMARY_BLUE, HOVER_BLUE);
//        joinMatchBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
//        joinMatchBtn.setPreferredSize(new Dimension(220, 50));
//        joinMatchBtn.setMaximumSize(new Dimension(220, 50));
//        joinMatchBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
//        joinMatchBtn.setForeground(Color.WHITE);


        contentPanel.add(infoCard);
        contentPanel.add(Box.createVerticalStrut(20));
//        contentPanel.add(joinMatchBtn);
        contentPanel.add(Box.createVerticalGlue());


        leftPanel.add(headerCard, BorderLayout.NORTH);
        leftPanel.add(contentPanel, BorderLayout.CENTER);


        // ==== RIGHT PANEL - Users List ====
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(20, 10, 20, 20));


        JPanel rightHeaderCard = createModernCard();
        rightHeaderCard.setLayout(new BorderLayout(10, 0));
        rightHeaderCard.setBorder(new EmptyBorder(16, 20, 16, 20));


        JLabel onlineLabel = new JLabel("Người chơi Online");
        onlineLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        onlineLabel.setForeground(PRIMARY_BLUE);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        buttonPanel.setOpaque(false);


        JButton historyBtn = createSmallModernButton("Lịch sử", PRIMARY_BLUE);
        JButton rankBtn = createSmallModernButton("BXH", PRIMARY_BLUE);
        JButton logoutBtn = createSmallModernButton("Thoát", new Color(239, 68, 68));


        buttonPanel.add(historyBtn);
        buttonPanel.add(rankBtn);
        buttonPanel.add(logoutBtn);


        rightHeaderCard.add(onlineLabel, BorderLayout.WEST);
        rightHeaderCard.add(buttonPanel, BorderLayout.EAST);


        // Users list container
        JPanel usersContainer = new JPanel(new BorderLayout());
        usersContainer.setOpaque(false);
        usersContainer.setBorder(new EmptyBorder(12, 0, 0, 0));


        HomeController controllerToUse = externalHomeController != null ? externalHomeController : homeController;


        UsersListPanel usersPanel = new UsersListPanel(controllerToUse, in, out);
        controllerToUse.setUsersPanel(usersPanel);


        JScrollPane scrollPane = new JScrollPane(usersPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        usersContainer.add(scrollPane, BorderLayout.CENTER);


        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(12, 0, 0, 0));


        JButton refreshBtn = createModernButton("Làm mới", PRIMARY_BLUE, HOVER_BLUE);
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshBtn.setPreferredSize(new Dimension(0, 44));
        refreshBtn.setForeground(Color.WHITE);


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


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(container, gbc);


        // ==== ACTIONS ====
        refreshBtn.addActionListener(e -> {
            refreshBtn.setEnabled(false);
            refreshBtn.setText("Đang tải...");
            controllerToUse.getUsersOnline(in, out);
            new Thread(() -> {
                try {
                    Thread.sleep(200);
                    SwingUtilities.invokeLater(() -> {
                        refreshBtn.setEnabled(true);
                        refreshBtn.setText("Làm mới");
                    });
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
        });
        refreshBtn.doClick();


        logoutBtn.addActionListener(e -> authController.handleLogout(in, out));
        rankBtn.addActionListener(e -> controllerToUse.getRank(in, out, ""));
        historyBtn.addActionListener(e -> controllerToUse.getMatchHistory(in, out));
//        joinMatchBtn.addActionListener(e -> controllerToUse.joinMatch(in, out));


        refreshFrame(mainPanel);
    }


    private JPanel createModernCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


                // Shadow effect
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, 18, 18);
                g2d.fillRoundRect(1, 3, getWidth() - 2, getHeight() - 2, 18, 18);


                // Card background with transparency
                g2d.setColor(TRANSPARENT_WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);


                // Card border
                g2d.setColor(new Color(200, 200, 210, 120));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
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
                    g2d.setColor(new Color(0, 0, 0, isHovered ? 30 : 15));
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
        button.setPreferredSize(new Dimension(95, 36));
        return button;
    }
}

