package server;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import util.Logger;

public class Server {
    private ServerSocket serverSocket;

    private volatile boolean running = false;
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final Set<ClientHandler> clients = Collections.synchronizedSet(new HashSet<>());

    public Server() {}

    public void start(int port) {
        System.out.println("Server starting!!!");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Ctrl+C detected. Shutting down server...");
            stop();
        }));
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

    public void stop() {
        running = false;

        // Đóng server socket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            Logger.error("Error closing server socket", e);
        }

//        // Đóng tất cả client
//        synchronized (clients) {
//            for (ClientHandler client : clients) {
//                client.stop();
//            }
//            clients.clear();
//        }

        // Shutdown thread pool
        pool.shutdownNow();

        System.out.println("Server stopped.");
    }
}