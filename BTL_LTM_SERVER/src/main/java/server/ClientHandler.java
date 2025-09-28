package server;

import db.UserDao;
import handler.AuthHandler;
import handler.HomeHandler;
import util.Logger;
import util.SocketController;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private BufferedWriter out;
    private BufferedReader in;

    private AuthHandler authHandler;
    private HomeHandler homeHandler;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new PrintWriter(clientSocket.getOutputStream()));
            UserDao userDao = new UserDao();
            authHandler = new AuthHandler(userDao);
            homeHandler = new HomeHandler(userDao);
            System.out.println("Client connected");
            String line;
            while ((line = in.readLine()) != null) {
                String response = handleCommand(line);
                if (response != null) {
                    this.writeEvent(response);
                }
            }
        } catch (IOException e) {
            Logger.error("Lỗi I/O khi xử lý client", e);
        } catch (SQLException e) {
            Logger.error("Lỗi cơ sở dữ liệu khi xử lý client", e);
        } finally {
            try {
                SocketController.removeLoggedInUser(this);
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
        Logger.info("Message From Client: " + line);
        String[] parts = line.split("\\|");
        return switch (parts[0]) {
            case "REGISTER" -> authHandler.handleRegister(parts);
            case "LOGIN" -> authHandler.handleLogin(parts, this);
            case "LOGOUT" -> authHandler.handleLogout(this);
            case "GET_USERS_ONLINE" -> homeHandler.getUserOnl(this);
            case "INVITE" -> homeHandler.sendInvite(this, parts);
            default -> "LOI GI DO ROI";
        };
    }
    public void writeEvent(String response) throws IOException {
        this.out.write(response);
        this.out.newLine();
        this.out.flush();
    }
}