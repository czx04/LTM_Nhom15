package server;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import db.UserDao;
import handler.AuthHandler;
import handler.HomeHanlder;
import util.Logger;
import util.SocketController;

public class Server {
    private ServerSocket serverSocket;
    public Server() {}

    public void start(int port) {
        System.out.println("Server starting!!!");
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            Logger.error("Lỗi khi khởi động server trên port " + port, e);
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                Logger.error("Lỗi khi đóng server socket", e);
            }
        }
    }
    private class ClientHandler extends Thread {
        private final Socket clientSocket;
        private BufferedWriter out;
        private BufferedReader in;

        private UserDao userDao;
        private AuthHandler authHandler;
        private HomeHanlder homeHanlder;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new PrintWriter(clientSocket.getOutputStream()));
                userDao = new UserDao();
                authHandler = new AuthHandler(userDao);
                homeHanlder = new HomeHanlder(userDao);
                System.out.println("Client connected");
                String line;
                while ((line = in.readLine()) != null) {
                    String response = handleCommand(line);
                    if (response != null) {
                        out.write(response);
                        out.newLine();
                        out.flush();
                    }
                }
            } catch (IOException e) {
                Logger.error("Lỗi I/O khi xử lý client", e);
            } catch (SQLException e) {
                Logger.error("Lỗi cơ sở dữ liệu khi xử lý client", e);
            } finally {
                try {
                    SocketController.removeLoggedInUser(clientSocket);
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                    if (clientSocket != null) {
                        clientSocket.close();
                    }
                } catch (Exception e) {
                    Logger.error("Lỗi khi đóng kết nối client", e);
                }
            }

        }

        private String handleCommand(String line) throws SQLException {
            System.out.println("Message From Client: " + line);
            String[] parts = line.split("\\|", 3);
            String action = parts[0];
            return switch (action) {
                case "REGISTER" -> authHandler.handleRegister(parts);
                case "LOGIN" -> authHandler.handleLogin(parts, clientSocket);
                case "LOGOUT" -> authHandler.handleLogout(clientSocket, SocketController.getLoggedInUsers());
                case "GET_USERS_ONLINE" -> homeHanlder.getUserOnl(SocketController.getLoggedInUsers().values());
                default -> "LOI GI DO ROI";
            };
        }
    }
}