package UI;

import controller.HomeController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.List;

public class Rank extends BaseUI {

    public void showRank(JFrame frame, BufferedReader in, BufferedWriter out, List<String[]> rows) {
        setupFrame(frame, in, out);
        showUI(frame, in, out);

        SwingUtilities.invokeLater(() -> {
            this.allRows = rows;
            populateTable(rows);
        });
    }

    private JTable table;
    private JTextField searchField;
    private JButton searchBtn;
    private List<String[]> allRows;

    @Override
    public void showUI(JFrame frame, BufferedReader in, BufferedWriter out) {
        JPanel container = new JPanel(new BorderLayout());

        JButton backBtn = new JButton("Quay lại");
        JPanel header = createHeaderPanel("Bảng xếp hạng", backBtn);

        JPanel searchPanel = new JPanel(new BorderLayout());
        JLabel searchLabel = new JLabel("Tìm kiếm: ");
        searchField = new JTextField();
        searchBtn = new JButton("Tìm");
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);

        JPanel topBox = new JPanel(new BorderLayout());
        topBox.add(header, BorderLayout.NORTH);
        topBox.add(searchPanel, BorderLayout.SOUTH);

        container.add(topBox, BorderLayout.NORTH);

        String[] columns = {"#", "Người chơi", "ELO", "Tổng điểm", "Số trận"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        container.add(new JScrollPane(table), BorderLayout.CENTER);

        backBtn.addActionListener(e -> {
            try {
                Home home = new Home();
                home.showHome(frame, in, out, "", new HomeController());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        searchBtn.addActionListener(e -> doSearch());
        searchField.addActionListener(e -> doSearch());

        refreshFrame(container);
    }

    public void populateTable(List<String[]> rows) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        int rank = 1;
        if (rows != null) {
            for (String[] r : rows) {
                model.addRow(new Object[]{rank++, r[0], r[1], r[2], (r.length > 3 ? r[3] : "0")});
            }
        }
    }

    private void doSearch() {
        String q = searchField.getText();
        try {
            HomeController controller = new HomeController();
            controller.getRank(in, out, q);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}


