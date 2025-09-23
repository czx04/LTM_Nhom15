package org.example;
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

public class Server {
    //...
    private ServerSocket serverSocket;
    public void start(int port) {
        System.out.println("Server starting123!!!");
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

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new PrintWriter(clientSocket.getOutputStream()));
                System.out.println("Client connected");
                String message = in.readLine();
                System.out.println("Message From Client: " + message);
                out.write("Hello Client 12345");
                out.newLine();
                out.flush();
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
}