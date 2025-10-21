package UI;

import controller.HomeController;
import util.Constants;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;

public class Home extends BaseUI {
    private String username;
    private HomeController externalHomeController; // HomeController từ Client

    public void showHome(JFrame frame, BufferedReader in, BufferedWriter out, String username,
            HomeController homeController) {
        setupFrame(frame, in, out);
        this.username = username;
        this.externalHomeController = homeController;
        showUI(frame, in, out);
    }

    public void showUI(JFrame frame, BufferedReader in, BufferedWriter out) {
        JPanel container = new JPanel(new BorderLayout());

        // ==== LEFT PANEL ====
        JPanel leftPanel = new JPanel(new BorderLayout());
        JLabel welcomeTitle = new JLabel("Chào mừng đến với trò chơi Đố Chữ!", SwingConstants.CENTER);
        welcomeTitle.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_TITLE));

        JTextArea welcomeBody = new JTextArea();
        welcomeBody.setEditable(false);
        welcomeBody.setLineWrap(true);
        welcomeBody.setWrapStyleWord(true);
        welcomeBody.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, Constants.FONT_SIZE_BODY));
        welcomeBody.setText(
                "Hello anh em đến với trò chơi xếp chữ của tui!\n" +
                        "- Xem người onl ở bên phải\n" +
                        "- Mời bạn bè và so tài.\n" +
                        "- Leo rank nào kekeke");

        // === Thêm nút "Vào trận" ở giữa khung chính ===
        JButton joinMatchBtn = new JButton("Vào trận");
        joinMatchBtn.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_BODY));
        joinMatchBtn.setPreferredSize(new Dimension(120, 40));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(new JScrollPane(welcomeBody));
        centerPanel.add(Box.createVerticalStrut(15)); // khoảng cách
        centerPanel.add(joinMatchBtn);
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel welcomeTextWrapper = new JPanel(new BorderLayout());
        welcomeTextWrapper.add(welcomeTitle, BorderLayout.NORTH);
        welcomeTextWrapper.add(centerPanel, BorderLayout.CENTER);
        leftPanel.add(welcomeTextWrapper, BorderLayout.CENTER);
        // ============================================

        // ==== RIGHT PANEL ====
        JPanel rightPanel = new JPanel(new BorderLayout());
        JButton logoutBtn = new JButton("Đăng xuất");
        JButton rankBtn = new JButton("Xem BXH");
        JPanel rightHeader = createHeaderPanel("Người chơi", logoutBtn);
        rightHeader.add(rankBtn, BorderLayout.WEST);
        rightPanel.add(rightHeader, BorderLayout.NORTH);

        HomeController controllerToUse = externalHomeController != null ? externalHomeController : homeController;

        UsersListPanel usersPanel = new UsersListPanel(controllerToUse, in, out);
        controllerToUse.setUsersPanel(usersPanel);
        JScrollPane scrollPane = new JScrollPane(usersPanel);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Làm mới");
        rightPanel.add(refreshBtn, BorderLayout.SOUTH);

        // ==== SPLIT ====
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(Constants.SPLIT_PANE_WEIGHT / 100.0);
        splitPane.setContinuousLayout(true);
        container.add(splitPane, BorderLayout.CENTER);

        // ==== ACTIONS ====
        refreshBtn.addActionListener(e -> controllerToUse.getUsersOnline(in, out));
        refreshBtn.doClick(); // chạy lần đầu

        logoutBtn.addActionListener(e -> authController.handleLogout(in, out));
        rankBtn.addActionListener(e -> controllerToUse.getRank(in, out, ""));

        // Khi bấm “Vào trận”
        joinMatchBtn.addActionListener(e -> controllerToUse.joinMatch(in, out));

        refreshFrame(container);
    }
}
