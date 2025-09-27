package UI;

import controller.Auth;
import util.Constants;
import util.ResponseHandler;
import util.InputValidator;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;

public class Register extends BaseUI {
    
    public void showRegister(JFrame frame, BufferedReader in, BufferedWriter out) {
        setupFrame(frame, in, out);
        showUI(frame, in, out);
    }
    
    public void showUI(JFrame frame, BufferedReader in, BufferedWriter out) {
        JPanel logoutPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8); // khoảng cách giữa các ô
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton backButton = new JButton("Quay lại");
        JPanel headerPanel = createHeaderPanel("Đăng ký", backButton);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        logoutPanel.add(headerPanel, gbc);

        JTextField usernameField = new JTextField(Constants.TEXT_FIELD_COLUMNS);
        JPasswordField passwordField = new JPasswordField(Constants.TEXT_FIELD_COLUMNS);
        
        String[] labels = {"Username:", "Password:"};
        Component[] components = {usernameField, passwordField};
        JPanel inputPanel = createInputPanel(labels, components);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        logoutPanel.add(inputPanel, gbc);

        JButton regisButton = new JButton("Đăng ký");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        logoutPanel.add(regisButton, gbc);

        backButton.addActionListener(e -> {
            showLogin();
        });

        regisButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());
            
            // Validate input
            InputValidator.ValidationResult validation = InputValidator.validateLogin(username, password);
            if (!validation.isValid()) {
                JOptionPane.showMessageDialog(null, validation.getMessage(),
                        Constants.TITLE_WARNING, JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            executeAsyncTask(regisButton,
                () -> auth.handleRegister(username, password, in, out),
                result -> ResponseHandler.handleRegisterResponse(result, username, frame, in, out),
                ex -> ResponseHandler.handleConnectionError()
            );
        });

        refreshFrame(logoutPanel);
    }
}
