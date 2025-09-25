package UI;

import controller.Auth;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class Home {
    private final Auth auth = new Auth();
    public void showHome(JFrame frame, BufferedReader in, BufferedWriter out,String username) {
        JPanel container = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel(new BorderLayout());
        JLabel welcomeTitle = new JLabel("Chào mừng đến với trò chơi Đố Chữ!", SwingConstants.CENTER);


        welcomeTitle.setFont(new Font("Arial", Font.BOLD, 22));
        JTextArea welcomeBody = new JTextArea();
        welcomeBody.setEditable(false);
        welcomeBody.setLineWrap(true);
        welcomeBody.setWrapStyleWord(true);
        welcomeBody.setFont(new Font("Arial", Font.PLAIN, 16));
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
        JPanel rightHeader = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Đang online", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        JButton logoutBtn = new JButton("Đăng xuất");
        rightHeader.add(title, BorderLayout.CENTER);
        rightHeader.add(logoutBtn, BorderLayout.EAST);
        rightPanel.add(rightHeader, BorderLayout.NORTH);


        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> usersList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(usersList);
        rightPanel.add(scrollPane, BorderLayout.CENTER);


        JButton refreshBtn = new JButton("Làm mới");
        rightPanel.add(refreshBtn, BorderLayout.SOUTH);


        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.8); // 80% cho panel trái
        splitPane.setContinuousLayout(true);
        container.add(splitPane, BorderLayout.CENTER);


        refreshBtn.addActionListener(e -> {
            refreshBtn.setEnabled(false);
            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    return auth.getUsersOnline(in, out);
                }

                @Override
                protected void done() {
                    refreshBtn.setEnabled(true);
                    try {
                        String response = get();
                        listModel.clear();
                        if (response != null && !response.trim().isEmpty()) {
                            String[] parts = response.split("\\|", 2);
                            String body = parts.length > 1 ? parts[1] : response;
                            String[] users = body.split(",");
                            ArrayList<String> list = new ArrayList<>(Arrays.asList(users));
                            list.remove(username);
                            users = list.toArray(new String[0]);
                            for (String user : users) {
                                user = user.trim();
                                if (!user.isEmpty()) {
                                    listModel.addElement(user);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                                "Lỗi tải danh sách người dùng",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        });

        // chạy lần đầu
        refreshBtn.doClick();

        logoutBtn.addActionListener(e -> {
            logoutBtn.setEnabled(false);
            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    return auth.handleLogout(in, out);
                }

                @Override
                protected void done() {
                    logoutBtn.setEnabled(true);
                    try {
                        String res = get();
                        if (res != null && res.startsWith("LOGOUT")) {
                            new Login().showLogin(frame, in, out);
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    "Đăng xuất thất bại",
                                    "Lỗi",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                                "Lỗi kết nối máy chủ",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        });

        frame.setContentPane(container);
        frame.revalidate();
        frame.repaint();
    }
}
