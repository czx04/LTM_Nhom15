package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;
    private boolean running = true;

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new BufferedWriter(new PrintWriter(clientSocket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Kết nối server thành công.\n");
            menu();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }

                if (out != null) {
                    out.close();
                }

                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void close() throws IOException {
        if (this.in != null) {
            this.in.close();
        }
        if (this.out != null) {
            this.close();
        }

        if (this.clientSocket != null) {
            this.clientSocket.close();
        }
    }

    private void menu() throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (running) {
            System.out.println("1. Đăng ký");
            System.out.println("2. Đăng nhập");
            System.out.println("3. Đăng xuất");
            System.out.println("4. Xem số người online");
            System.out.println("0. Thoát");
            System.out.print("Chọn: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    handleRegister(scanner);
                    break;
                case "2":
                    handleLogin(scanner);
                    break;
                case "3":
                    sendLine("LOGOUT");
                    System.out.println(in.readLine());
                    break;
                case "4":
                    sendLine("GET_USERS_ONLINE");
                    System.out.println(in.readLine());
                    break;
                case "0":
                    sendLine("DISCONNECT");
                    running = false;
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ");
            }
        }
    }

    private void handleRegister(Scanner scanner) throws IOException {
        System.out.print("Tên đăng nhập: ");
        String username = scanner.nextLine();
        System.out.print("Mật khẩu: ");
        String password = scanner.nextLine();
        sendLine("REGISTER|" + username + "|" + password);
        System.out.println(in.readLine());
    }

    private void handleLogin(Scanner scanner) throws IOException {
        System.out.print("Tên đăng nhập: ");
        String username = scanner.nextLine();
        System.out.print("Mật khẩu: ");
        String password = scanner.nextLine();
        sendLine("LOGIN|" + username + "|" + password);
        System.out.println(in.readLine());
    }

    private void sendLine(String line) throws IOException {
        out.write(line);
        out.newLine();
        out.flush();
    }

}