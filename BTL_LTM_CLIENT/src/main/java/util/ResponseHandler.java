package util;

import javax.swing.JOptionPane;
import java.util.ArrayList;

public class ResponseHandler {
    
    public static void handleLoginResponse(String response, String username, 
                                         javax.swing.JFrame frame, 
                                         java.io.BufferedReader in, 
                                         java.io.BufferedWriter out) {
        if (Constants.RESPONSE_LOGGEDIN.equals(response)) {
            // Navigate to Home screen
            try {
                Class<?> homeClass = Class.forName("UI.Home");
                Object home = homeClass.getDeclaredConstructor().newInstance();
                homeClass.getMethod("showHome", javax.swing.JFrame.class, 
                                  java.io.BufferedReader.class, 
                                  java.io.BufferedWriter.class, 
                                  String.class)
                        .invoke(home, frame, in, out, username);
            } catch (Exception e) {
                showError(Constants.MSG_CONNECTION_ERROR);
            }
        } else {
            showWarning(Constants.MSG_LOGIN_FAILED);
        }
    }
    
    public static void handleRegisterResponse(String response, String username,
                                            javax.swing.JFrame frame,
                                            java.io.BufferedReader in,
                                            java.io.BufferedWriter out) {
        if (Constants.RESPONSE_REGISTED.equals(response)) {
            // Navigate to Home screen
            try {
                Class<?> homeClass = Class.forName("UI.Home");
                Object home = homeClass.getDeclaredConstructor().newInstance();
                homeClass.getMethod("showHome", javax.swing.JFrame.class,
                                  java.io.BufferedReader.class,
                                  java.io.BufferedWriter.class,
                                  String.class)
                        .invoke(home, frame, in, out, username);
            } catch (Exception e) {
                showError(Constants.MSG_CONNECTION_ERROR);
            }
        } else if (Constants.RESPONSE_EXIST.equals(response)) {
            showWarning(username + " " + Constants.MSG_USER_EXISTS);
        } else {
            showWarning(username + " " + Constants.MSG_INVALID_FORMAT);
        }
    }
    
    public static void handleLogoutResponse(String response, javax.swing.JFrame frame,
                                          java.io.BufferedReader in,
                                          java.io.BufferedWriter out) {
        if (response != null && response.startsWith(Constants.RESPONSE_LOGOUT)) {
            // Navigate back to Login screen
            try {
                Class<?> loginClass = Class.forName("UI.Login");
                Object login = loginClass.getDeclaredConstructor().newInstance();
                loginClass.getMethod("showLogin", javax.swing.JFrame.class,
                                   java.io.BufferedReader.class,
                                   java.io.BufferedWriter.class)
                        .invoke(login, frame, in, out);
            } catch (Exception e) {
                showError(Constants.MSG_CONNECTION_ERROR);
            }
        } else {
            showError(Constants.MSG_LOGOUT_FAILED);
        }
    }
    
    public static java.util.List<String> parseUsersOnline(String response, String currentUser) {
        java.util.List<String> users = new ArrayList<>();
        
        if (response != null && !response.trim().isEmpty()) {
            String[] parts = response.split("\\" + Constants.COMMAND_SEPARATOR, 2);
            String body = parts.length > 1 ? parts[1] : response;
            String[] userArray = body.split(Constants.USER_SEPARATOR);
            
            for (String user : userArray) {
                String trimmedUser = user.trim();
                if (!trimmedUser.isEmpty() && !trimmedUser.equals(currentUser)) {
                    users.add(trimmedUser);
                }
            }
        }
        
        return users;
    }
    
    public static void handleConnectionError() {
        showError(Constants.MSG_CONNECTION_ERROR);
    }
    
    public static void handleLoadUsersError() {
        showError(Constants.MSG_LOAD_USERS_ERROR);
    }
    
    private static void showWarning(String message) {
        JOptionPane.showMessageDialog(null, message, 
                                    Constants.TITLE_WARNING, 
                                    JOptionPane.WARNING_MESSAGE);
    }
    
    private static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, 
                                    Constants.TITLE_ERROR, 
                                    JOptionPane.ERROR_MESSAGE);
    }
}
