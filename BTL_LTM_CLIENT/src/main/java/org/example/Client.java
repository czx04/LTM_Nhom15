package org.example;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new BufferedWriter(new PrintWriter(clientSocket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out.write("Hello server 12345");
            out.newLine();
            out.flush();
            String message = in.readLine();
            System.out.println("Message from Server: " + message);
            out.close();
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

}