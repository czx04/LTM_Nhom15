package org.example;

import java.io.*;
import java.net.Socket;
import javax.swing.*;

import UI.Login;
import handler.EventHandler;
import util.Constants;
import controller.HomeController;

public class Client {
    private Socket clientSocket;
    public BufferedReader in;
    public BufferedWriter out;
    private EventHandler eventHandler = new EventHandler();
    public HomeController homeController = new HomeController();

    private final Login login = new Login();

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Kết nối server thành công.\n");
            startListening();
            System.out.println("Sẵn sàng lắng nghe sự kiện");
            AppSwing();
        } catch (IOException e) {
            e.printStackTrace();
            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            if (this.in != null) {
                this.in.close();
            }
            if (this.out != null) {
                this.out.close();
            }
            if (this.clientSocket != null) {
                this.clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JFrame frame;
    public void AppSwing() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame(Constants.TITLE_GAME);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
            frame.setLocationRelativeTo(null);
            
            // Đóng connection khi đóng ứng dụng
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    closeConnection();
                    System.exit(0);
                }
            });
            // mở màn Login trước
            login.showLogin(frame,in,out);
            frame.setVisible(true);
        });
    }
    private void startListening() {
        Thread listenerThread = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("Server: " + line);
                    handleEvent(line);
                }
            } catch (IOException e) {
                System.out.println("Mất kết nối với server.");
            } finally {
                closeConnection();
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private void handleEvent(String line) {
        System.out.println("Message From Client: " + line);
        try {
            String[] parts = line.split("\\|");
            switch (parts[0]) {
                case "LOGIN" -> eventHandler.handleLoginResponse(this, parts);
                case "REGISTER" -> eventHandler.handleRegisterResponse(this, parts);
                case "LOGOUT" -> eventHandler.handleLogoutResponse(this, parts);
                case "USER_ONLINE" -> eventHandler.parseUsersOnline(this,parts);
                case "INVITE" -> eventHandler.handleInvite(this,parts);
                case "USER_STATUS" -> eventHandler.handleUserStatus(this, parts);
                case "REJECT" -> System.out.println("Invite rejected.");
                default -> System.out.println("Unknown event");
            }
        } catch (Exception e) {
            System.err.println("Error handling event: " + line);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}