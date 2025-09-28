package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class AuthController {

    public String handleRegister(String username,String password,BufferedReader in, BufferedWriter out) {
        try {
            sendLine("REGISTER|" + username + "|" + password,out);
            return "SENT";
        }
        catch (Exception e) {
            System.out.println(e);
            return "fail";
        }

    }
    public String handleLogin(String username,String password,BufferedReader in, BufferedWriter out) {
        try {
            sendLine("LOGIN|" + username + "|" + password,out);
            return "SENT";
        }
        catch (Exception e) {
            System.out.println(e);
            return "fail";
        }
    }

    public String handleLogout(BufferedReader in, BufferedWriter out) {
        try {
            sendLine("LOGOUT", out);
            return "SENT";
        } catch (IOException e) {
            System.out.println(e);
            return "fail";
        }
    }

    private void sendLine(String line,BufferedWriter out) throws IOException {
        out.write(line);
        out.newLine();
        out.flush();
    }
}
