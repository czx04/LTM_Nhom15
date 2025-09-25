package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class Auth {

    public String handleRegister(String username,String password,BufferedReader in, BufferedWriter out) {
        try {
            sendLine("REGISTER|" + username + "|" + password,out);
            String response = in.readLine();
            System.out.println(response);
            return response;
        }
        catch (Exception e) {
            System.out.println(e);
            return "fail";
        }

    }
    public String handleLogin(String username,String password,BufferedReader in, BufferedWriter out) {
        String res;
        try {
            sendLine("LOGIN|" + username + "|" + password,out);
            res = in.readLine();
            return res;
        }
        catch (Exception e) {
            System.out.println(e);
            return "fail";
        }
    }

    public String handleLogout(BufferedReader in, BufferedWriter out) {
        try {
            sendLine("LOGOUT", out);
            String response = in.readLine();
            System.out.println(response);
            return response;
        } catch (IOException e) {
            System.out.println(e);
            return "fail";
        }
    }

    public String getUsersOnline(BufferedReader in, BufferedWriter out) {
        try {
            sendLine("GET_USERS_ONLINE", out);
            String response = in.readLine();
            System.out.println(response);
            return response;
        } catch (IOException e) {
            System.out.println(e);
            return "";
        }
    }

    private void sendLine(String line,BufferedWriter out) throws IOException {
        out.write(line);
        out.newLine();
        out.flush();
    }
}
