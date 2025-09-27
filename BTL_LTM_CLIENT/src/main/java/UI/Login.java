package UI;

import controller.Auth;
import util.Constants;
import util.ResponseHandler;
import util.InputValidator;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;

public class Login extends BaseUI {
    private Register register = new Register();
    
    public void showLogin(JFrame frame, BufferedReader in, BufferedWriter out) {
        setupFrame(frame, in, out);
        showUI(frame, in, out);
    }
    
    public void showUI(JFrame frame, BufferedReader in, BufferedWriter out) {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8); // khoảng cách giữa các ô
        gbc.fill = GridBagConstraints.HORIZONTAL;


        JButton registerButton = new JButton("Đăng ký");
        JPanel headerPanel = createHeaderPanel("Đăng nhập", registerButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(headerPanel, gbc);

        JTextField usernameField = new JTextField(Constants.TEXT_FIELD_COLUMNS);
        JPasswordField passwordField = new JPasswordField(Constants.TEXT_FIELD_COLUMNS);
        
        String[] labels = {"Username:", "Password:"};
        Component[] components = {usernameField, passwordField};
        JPanel inputPanel = createInputPanel(labels, components);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(inputPanel, gbc);

        JButton loginButton = new JButton("Đăng nhập");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginButton, gbc);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());
            
            // Validate input
            InputValidator.ValidationResult validation = InputValidator.validateLogin(username, password);
            if (!validation.isValid()) {
                JOptionPane.showMessageDialog(null, validation.getMessage(),
                        Constants.TITLE_WARNING, JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            executeAsyncTask(loginButton, 
                () -> auth.handleLogin(username, password, in, out),
                result -> ResponseHandler.handleLoginResponse(result, username, frame, in, out),
                ex -> ResponseHandler.handleConnectionError()
            );
        });

        registerButton.addActionListener(e -> {
            showRegister();
        });

        refreshFrame(loginPanel);
    }
}
