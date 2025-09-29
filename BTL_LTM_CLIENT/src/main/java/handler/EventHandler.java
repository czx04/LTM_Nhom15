package handler;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.example.Client;

import util.Constants;

public class EventHandler {

    public void handleInvite(Client client, String[] parts){
        if (parts.length > 1) {
            if (parts[1].equals("OK") || parts[1].equals("NOT_OK")) {
                return;
            }
            String invitor = parts[1];
            SwingUtilities.invokeLater(() -> {
                int choice = JOptionPane.showConfirmDialog(
                        client.frame,
                        invitor + " đã mời bạn solo. Bạn có chấp nhận không?",
                        "Lời mời từ " + invitor,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (choice == JOptionPane.YES_OPTION) {
                    // TODO: gửi phản hồi ACCEPT về server
                    try {
                        client.out.write("INVITE_ACCEPT|" + invitor);
                        client.out.newLine();
                        client.out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // TODO: gửi phản hồi REJECT về server
                    try {
                        client.out.write("INVITE_REJECT|" + invitor);
                        client.out.newLine();
                        client.out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    public void handleLoginResponse(Client client, String[] response) {
        if (Constants.RESPONSE_LOGGEDIN.equals(response[1])) {
            System.out.println("LOGIN OK");
            try {
                Class<?> homeClass = Class.forName("UI.Home");
                Object home = homeClass.getDeclaredConstructor().newInstance();
                homeClass.getMethod("showHome", javax.swing.JFrame.class,
                                java.io.BufferedReader.class,
                                java.io.BufferedWriter.class,
                                String.class,
                                controller.HomeController.class)
                        .invoke(home, client.frame, client.in, client.out, response[2], client.homeController);
            } catch (Exception e) {
                System.err.println("Lỗi khi navigate đến Home screen: " + e.getMessage());
                e.printStackTrace();
                showError(Constants.MSG_CONNECTION_ERROR);
            }
        } else {
            showWarning(Constants.MSG_LOGIN_FAILED);
        }
    }

    public void handleRegisterResponse(Client client, String[] response) {
        if (Constants.RESPONSE_REGISTED.equals(response[1])) {
            // Navigate to Home screen
            try {
                Class<?> homeClass = Class.forName("UI.Home");
                Object home = homeClass.getDeclaredConstructor().newInstance();
                homeClass.getMethod("showHome", javax.swing.JFrame.class,
                                java.io.BufferedReader.class,
                                java.io.BufferedWriter.class,
                                String.class,
                                controller.HomeController.class)
                        .invoke(home, client.frame, client.in, client.out, response[2], client.homeController);
            } catch (Exception e) {
                showError(Constants.MSG_CONNECTION_ERROR);
            }
        } else if (Constants.RESPONSE_EXIST.equals(response[1])) {
            showWarning(response[2] + " " + Constants.MSG_USER_EXISTS);
        } else {
            showWarning(response[2] + " " + Constants.MSG_INVALID_FORMAT);
        }
    }

    public void handleLogoutResponse(Client client, String[] response) {
        if (response != null && response[0].startsWith(Constants.RESPONSE_LOGOUT)) {
            // Navigate back to Login screen
            try {
                Class<?> loginClass = Class.forName("UI.Login");
                Object login = loginClass.getDeclaredConstructor().newInstance();
                loginClass.getMethod("showLogin", javax.swing.JFrame.class,
                                java.io.BufferedReader.class,
                                java.io.BufferedWriter.class)
                        .invoke(login, client.frame, client.in, client.out);
            } catch (Exception e) {
                showError(Constants.MSG_CONNECTION_ERROR);
            }
        } else {
            showError(Constants.MSG_LOGOUT_FAILED);
        }
    }

    public java.util.List<String> parseUsersOnline(Client client,String[] response) {
        java.util.List<String> users = new ArrayList<>();
        java.util.List<String> allUser = new ArrayList<>();

        System.out.println("Parsing users online: " + java.util.Arrays.toString(response));

        if (response[1] != null && !response[1].trim().isEmpty()) {
            String body = response[1];
            String[] userArray = body.split(",");
            for (String user : userArray) {
                if (!user.trim().isEmpty()) {
                    users.add(user.trim());
                }
            }
        } else {
            System.out.println("No users onl data in response or response too short");
        }

        if (response[2] != null && !response[2].trim().isEmpty()) {
            String body = response[2];
            String[] userArray = body.split(",");
            for (String user : userArray) {
                if (!user.trim().isEmpty()) {
                    allUser.add(user.trim());
                }
            }

        } else {
            System.out.println("No users onl data in response or response too short");
        }
        
        System.out.println("Parsed users: " + users);
        System.out.println("Parsed all users: " + allUser);
        
        SwingUtilities.invokeLater(() -> {
            try {
                if (client.homeController != null) {
                    System.out.println("Calling homeController.onUsersOnlineReceived with " + users.size() + " users");
                    client.homeController.onUsersOnlineReceived(users,allUser);
                } else {
                    System.out.println("homeController is null!");
                }
            } catch (Exception e) {
                System.err.println("Error updating users online: " + e.getMessage());
                e.printStackTrace();
            }
        });
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
