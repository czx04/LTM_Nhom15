package org.example;



public class Main {
    public static void main(String[] agrs) {
        Client client = new Client();
        client.startConnection("127.0.0.1", 8080);
    }
}