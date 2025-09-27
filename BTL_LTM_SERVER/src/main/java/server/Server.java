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
import util.Logger;
import util.SocketController;

public class Server {
    private ServerSocket serverSocket;

    private final Logger logger;

    public Server(Logger logger) {
        this.logger = logger;
    }

    public void start(int port) {
        System.out.println("Server starting!!!");
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            logger.error("Lỗi khi khởi động server trên port " + port, e);
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                logger.error("Lỗi khi đóng server socket", e);
            }
        }
    }
    private class ClientHandler extends Thread {
        private final Socket clientSocket;
        private BufferedWriter out;
        private BufferedReader in;

        private UserDao userDao;
        private AuthHandler authHandler;

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
                logger.error("Lỗi I/O khi xử lý client", e);
            } catch (SQLException e) {
                logger.error("Lỗi cơ sở dữ liệu khi xử lý client", e);
            } finally {
                try {
                    SocketController.getInstance().getLoggedInUsers().remove(clientSocket);
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
                    logger.error("Lỗi khi đóng kết nối client", e);
                }
            }

        }

        private String handleCommand(String line) throws SQLException {
            System.out.println("Message From Client: " + line);
            String[] parts = line.split("\\|", 3);
            String action = parts[0];
            switch (action) {
                case "REGISTER": {
                   return authHandler.handleRegister(parts);
                }
                case "LOGIN": {
                    return authHandler.handleLogin(parts,clientSocket, SocketController.getInstance().getLoggedInUsers());
                }
                case "LOGOUT": {
                    SocketController.getInstance().getLoggedInUsers().remove(clientSocket);
                    return "LOGOUT";
                }
                case "GET_USERS_ONLINE": {
                    java.util.Set<String> names = new java.util.LinkedHashSet<>(SocketController.getInstance().getLoggedInUsers().values());
                    String payload = String.join(",", names);
                    return payload;
                }
                case "DISCONNECT": {
                    SocketController.getInstance().getLoggedInUsers().remove(clientSocket);
                    try {
                        clientSocket.close();
                    } catch (IOException ignored) {}
                    return null;
                }
                default:
                    return "LOI GI DO ROI";
            }
        }
    }
}