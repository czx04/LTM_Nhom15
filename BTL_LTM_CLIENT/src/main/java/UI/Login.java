package UI;

import controller.Auth;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;

public class Login {
    private Auth auth = new Auth();
    private Home home = new Home();
    private Register register = new Register();
    public void showLogin(JFrame frame, BufferedReader in , BufferedWriter out) {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8); // khoảng cách giữa các ô
        gbc.fill = GridBagConstraints.HORIZONTAL;


        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Đăng nhập", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(title, BorderLayout.CENTER);

        JButton registerButton = new JButton("Đăng ký");
        headerPanel.add(registerButton, BorderLayout.EAST);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(headerPanel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField usernameField = new JTextField(15);
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JPasswordField passwordField = new JPasswordField(15);
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton loginButton = new JButton("Đăng nhập");
        loginPanel.add(loginButton, gbc);


        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());
            loginButton.setEnabled(false);
            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    return auth.handleLogin(username, password, in, out);
                }

                @Override
                protected void done() {
                    loginButton.setEnabled(true);
                    try {
                        String result = get();
                        if ("LOGGEDIN".equals(result)) {
                            home.showHome(frame, in, out,username);
                        } else {
                            JOptionPane.showMessageDialog(null, "Sai username hoặc password!",
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

        registerButton.addActionListener(e -> {
            register.showRegister(frame,in,out);
        });



        frame.setContentPane(loginPanel);
        frame.revalidate();
        frame.repaint();
    }

}
