package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import UI.UsersListPanel;

public class HomeController {
    private UsersListPanel usersPanel;

    public void setUsersPanel(UsersListPanel usersPanel) {
        this.usersPanel = usersPanel;
    }

    public void getUsersOnline(BufferedReader in, BufferedWriter out) {
        try {
            sendLine("GET_USERS_ONLINE", out);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void sendInvite(String user, BufferedReader in, BufferedWriter out) throws IOException {
        try {
            sendLine("INVITE|" + user, out);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void onUsersOnlineReceived(List<String> users, List<String> allUsers) {
        System.out.println("HomeController.onUsersOnlineReceived called with users: " + users);
        System.out.println("usersPanel is null: " + (usersPanel == null));

        if (usersPanel != null) {
            System.out.println("Calling usersPanel.setUsers()");
            usersPanel.setUsers(users, allUsers);
        } else {
            System.out.println("usersPanel is null - cannot update UI");
        }
    }

    private void sendLine(String line, BufferedWriter out) throws IOException {
        out.write(line);
        out.newLine();
        out.flush();
    }

    public void getRank(BufferedReader in, BufferedWriter out, String query) {
        try {
            if (query == null)
                query = "";
            sendLine("GET_RANK|" + query, out);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void joinMatch(BufferedReader in, BufferedWriter out) {
        try {
            sendLine("JOIN_MATCH", out);
            System.out.println("Đã gửi yêu cầu vào trận tới server.");
        } catch (IOException e) {
            System.out.println("Lỗi khi gửi yêu cầu vào trận: " + e.getMessage());
        }
    }

}
