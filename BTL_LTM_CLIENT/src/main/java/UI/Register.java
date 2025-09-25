package UI;

import controller.Auth;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;

public class Register {
    private final Auth auth = new Auth();
    private final Home home = new Home();
    public void showRegister(JFrame frame, BufferedReader in , BufferedWriter out) {
        JPanel logoutPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8); // khoảng cách giữa các ô
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Đăng ký", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(title, BorderLayout.CENTER);

        JButton backButton = new JButton("Quay lại");
        headerPanel.add(backButton, BorderLayout.EAST);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        logoutPanel.add(headerPanel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = 1;
        logoutPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField usernameField = new JTextField(15);
        logoutPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        logoutPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JPasswordField passwordField = new JPasswordField(15);
        logoutPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton regisButton = new JButton("Đăng ký");
        logoutPanel.add(regisButton, gbc);

        backButton.addActionListener(e -> {
            new Login().showLogin(frame, in, out);
        });

        regisButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());
            regisButton.setEnabled(false);
            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    return auth.handleRegister(username, password, in, out);
                }
                @Override
                protected void done() {
                    regisButton.setEnabled(true);
                    try {
                        String result = get();
                        if ("REGISTED".equals(result)) {
                            home.showHome(frame, in, out,username);
                        } else {
                            JOptionPane.showMessageDialog(null, "Sai format username hoặc password!",
                                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Lỗi kết nối máy chủ",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        });

        frame.setContentPane(logoutPanel);
        frame.revalidate();
        frame.repaint();
    }
}
