package UI;

import controller.AuthController;
import controller.HomeController;
import util.Constants;
import util.ResponseHandler;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;

public abstract class BaseUI {
    
    protected AuthController authController;
    protected HomeController homeController;
    protected JFrame frame;
    protected BufferedReader in;
    protected BufferedWriter out;
    
    public BaseUI() {
        this.authController = new AuthController();
        this.homeController = new HomeController();
    }
    
    protected void setupFrame(JFrame frame, BufferedReader in, BufferedWriter out) {
        this.frame = frame;
        this.in = in;
        this.out = out;
    }
    
    protected JPanel createHeaderPanel(String title, JButton rightButton) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, Constants.FONT_SIZE_HEADER));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        if (rightButton != null) {
            headerPanel.add(rightButton, BorderLayout.EAST);
        }
        
        return headerPanel;
    }
    
    protected JPanel createInputPanel(String[] labels, Component[] components) {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        for (int i = 0; i < labels.length; i++) {
            // Label
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.anchor = GridBagConstraints.EAST;
            inputPanel.add(new JLabel(labels[i]), gbc);
            
            // Component
            gbc.gridx = 1;
            gbc.gridy = i;
            inputPanel.add(components[i], gbc);
        }
        
        return inputPanel;
    }
    
    protected void executeAsyncTask(javax.swing.JButton button, 
                                  java.util.concurrent.Callable<String> task,
                                  java.util.function.Consumer<String> onSuccess,
                                  java.util.function.Consumer<Exception> onError) {
        button.setEnabled(false);
        
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return task.call();
            }
            
            @Override
            protected void done() {
                button.setEnabled(true);
                try {
                    String result = get();
                    onSuccess.accept(result);
                } catch (java.util.concurrent.ExecutionException | InterruptedException ex) {
                    onError.accept(ex);
                }
            }
        };
        worker.execute();
    }
    
    protected void showLogin() {
        try {
            Login login = new Login();
            login.showLogin(frame, in, out);
        } catch (Exception e) {
            ResponseHandler.handleConnectionError();
        }
    }

    protected void showRegister() {
        try {
            Register register = new Register();
            register.showRegister(frame, in, out);
        } catch (Exception e) {
            ResponseHandler.handleConnectionError();
        }
    }
    
    protected void refreshFrame(JPanel contentPanel) {
        frame.setContentPane(contentPanel);
        frame.revalidate();
        frame.repaint();
    }
    
    // Abstract method to be implemented by subclasses
    public abstract void showUI(JFrame frame, BufferedReader in, BufferedWriter out);
}
