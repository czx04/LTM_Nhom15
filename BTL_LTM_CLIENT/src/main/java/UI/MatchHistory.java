package UI;

import controller.HomeController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.List;

public class MatchHistory extends BaseUI {

    private String username;
    private HomeController homeController;

    public void showMatchHistory(JFrame frame, BufferedReader in, BufferedWriter out, List<String[]> rows, String username, HomeController homeController) {
        setupFrame(frame, in, out);
        this.username = username;
        this.homeController = homeController;
        showUI(frame, in, out);

        SwingUtilities.invokeLater(() -> {
            populateTable(rows);
        });
    }

    private JTable table;

    @Override
    public void showUI(JFrame frame, BufferedReader in, BufferedWriter out) {
        JPanel container = new JPanel(new BorderLayout());

        JButton backBtn = new JButton("Quay lại");
        JPanel header = createHeaderPanel("Lịch sử đấu", backBtn);

        container.add(header, BorderLayout.NORTH);

        String[] columns = {"Trận #", "Đối thủ", "Điểm", "Thay đổi ELO", "Kết quả", "Thời gian"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(60);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);
        
        container.add(new JScrollPane(table), BorderLayout.CENTER);

        backBtn.addActionListener(e -> {
            try {
                Home home = new Home();
                home.showHome(frame, in, out, username != null ? username : "", homeController);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        refreshFrame(container);
    }

    public void populateTable(List<String[]> rows) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        if (rows != null) {
            for (String[] r : rows) {
                model.addRow(new Object[]{r[0], r[1], r[2], r[3], r[4], r[5]});
            }
        }
    }
}

