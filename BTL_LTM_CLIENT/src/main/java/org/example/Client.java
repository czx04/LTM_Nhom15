package org.example;

import java.io.*;
import java.net.Socket;
import java.awt.*;
import javax.swing.*;

import UI.Login;

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
            frame = new JFrame("Game tính nhanh");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
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

//    private void menu() throws IOException {
//        Scanner scanner = new Scanner(System.in);
//        while (running) {
//            System.out.println("1. Đăng ký");
//            System.out.println("2. Đăng nhập");
//            System.out.println("3. Đăng xuất");
//            System.out.println("4. Xem số người online");
//            System.out.println("0. Thoát");
//            System.out.print("Chọn: ");
//            String choice = scanner.nextLine();
//            switch (choice) {
//                case "1":
//                    handleRegister(scanner);
//                    break;
//                case "2":
//                    handleLogin(scanner);
//                    break;
//                case "3":
//                    sendLine("LOGOUT");
//                    System.out.println(in.readLine());
//                    break;
//                case "4":
//                    sendLine("GET_USERS_ONLINE");
//                    System.out.println(in.readLine());
//                    break;
//                case "0":
//                    sendLine("DISCONNECT");
//                    running = false;
//                    break;
//                default:
//                    System.out.println("Lựa chọn không hợp lệ");
//            }
//        }
//    }

}