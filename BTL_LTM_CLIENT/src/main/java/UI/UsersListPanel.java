package UI;

import controller.HomeController;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class UsersListPanel extends JPanel {
    HomeController homeController;
    BufferedReader in;
    BufferedWriter out;

    public UsersListPanel(HomeController homeController, BufferedReader in, BufferedWriter out) {
        this.homeController = homeController;
        this.in = in;
        this.out = out;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public void setUsers(List<String> users,List<String> allUsers) {
        removeAll();
        for (String user : users) {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel(user);
            JButton button = new JButton("Solo");
            button.addActionListener(e -> {
                System.out.println("Clicked Solo for " + user);
                try {
                    homeController.sendInvite(user, in, out);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            });

            panel.add(label, BorderLayout.WEST);
            panel.add(button, BorderLayout.EAST);
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

            add(panel);
        }

        for (String user : allUsers) {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel(user);
            panel.add(label, BorderLayout.WEST);
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            add(panel);
        }
        revalidate();
        repaint();
    }

}
