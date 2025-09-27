package org.example;

import java.io.*;
import java.net.Socket;
import java.awt.*;
import javax.swing.*;

import UI.Login;
import util.Constants;

public class Client {
    private Socket clientSocket;
    public BufferedReader in;
    public BufferedWriter out;

    private Login login = new Login();

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Kết nối server thành công.\n");
//            menu();
            AppSwing();
        } catch (IOException e) {
            e.printStackTrace();
            // Chỉ đóng connection khi có lỗi kết nối
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

}