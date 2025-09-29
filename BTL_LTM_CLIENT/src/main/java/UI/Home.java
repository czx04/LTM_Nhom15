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

    public void showHome(JFrame frame, BufferedReader in, BufferedWriter out, String username, HomeController homeController) {
        setupFrame(frame, in, out);
        this.username = username;
        this.externalHomeController = homeController;
        showUI(frame, in, out);
    }
    
    public void showUI(JFrame frame, BufferedReader in, BufferedWriter out) {
        JPanel container = new JPanel(new BorderLayout());

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
                "- Xem ngừoi onl ở bên phải\n" +
                "- Mời bạn bè và so tài.\n" +
                "- Leo rank nào kekeke"
        );

        JPanel welcomeTextWrapper = new JPanel(new BorderLayout());
        welcomeTextWrapper.add(welcomeTitle, BorderLayout.NORTH);
        welcomeTextWrapper.add(new JScrollPane(welcomeBody), BorderLayout.CENTER);
        leftPanel.add(welcomeTextWrapper, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        JButton logoutBtn = new JButton("Đăng xuất");
        JPanel rightHeader = createHeaderPanel("Người chơi", logoutBtn);
        rightPanel.add(rightHeader, BorderLayout.NORTH);

        HomeController controllerToUse = externalHomeController != null ? externalHomeController : homeController;
        
        UsersListPanel usersPanel = new UsersListPanel(controllerToUse, in, out);
        controllerToUse.setUsersPanel(usersPanel);
        JScrollPane scrollPane = new JScrollPane(usersPanel);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Làm mới");
        rightPanel.add(refreshBtn, BorderLayout.SOUTH);


        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(Constants.SPLIT_PANE_WEIGHT / 100.0);
        splitPane.setContinuousLayout(true);
        container.add(splitPane, BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> {
            controllerToUse.getUsersOnline(in, out);
        });

        // chạy lần đầu
        refreshBtn.doClick();

        logoutBtn.addActionListener(e -> {
                authController.handleLogout(in, out);
        });

        refreshFrame(container);

    }
}
