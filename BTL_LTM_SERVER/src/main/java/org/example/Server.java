package org.example;
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private ServerSocket serverSocket;
    private final Map<Socket, String> loggedInUsers = new HashMap<>();

    public void start(int port) {
        System.out.println("Server starting!!!");
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private BufferedWriter out;
        private BufferedReader in;
        private db.UserDao userDao;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new PrintWriter(clientSocket.getOutputStream()));
                userDao = new db.UserDao();
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
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    loggedInUsers.remove(clientSocket);
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
                    e.printStackTrace();
                }
            }

        }

        private String handleCommand(String line) throws SQLException {
            System.out.println("Message From Client: " + line);
            String[] parts = line.split("\\|", 3);
            String action = parts[0];
            switch (action) {
                case "REGISTER": {
                    if (parts.length < 3) return "SAI FORMAT";
                    String username = parts[1];
                    String password = parts[2];
                    boolean ok = userDao.createUser(username, password);
                    return ok ? "REGISTED" : "EXIST";
                }
                case "LOGIN": {
                    if (parts.length < 3) return "SAI FORMAT";
                    System.out.println(loggedInUsers.get(clientSocket));
                    String username = parts[1];
                    String password = parts[2];
                    boolean ok = userDao.verifyLogin(username, password);
                    if (ok) {
                        loggedInUsers.put(clientSocket, username);
                        System.out.println(clientSocket);
                        return "LOGGEDIN";
                    } else {
                        return "FAILLOGIN";
                    }
                }
                case "LOGOUT": {
                    loggedInUsers.remove(clientSocket);
                    return "Da Dang Xuat";
                }
                case "GET_USERS_ONLINE": {
                    java.util.Set<String> names = new java.util.LinkedHashSet<>(loggedInUsers.values());
                    String payload = String.join(",", names);
                    return payload;
                }
                case "DISCONNECT": {
                    loggedInUsers.remove(clientSocket);
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