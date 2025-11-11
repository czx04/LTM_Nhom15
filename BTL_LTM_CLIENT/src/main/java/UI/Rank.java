package UI;

import controller.HomeController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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
        container.setBackground(new Color(240, 242, 245));
        container.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel mainCard = createModernCard();
        mainCard.setLayout(new BorderLayout(0, 20));
        mainCard.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(99, 110, 250),
                    0, getHeight(), new Color(139, 92, 246)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        headerPanel.setPreferredSize(new Dimension(0, 120));

        JLabel titleLabel = new JLabel("🏆 Bảng Xếp Hạng");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        JButton backBtn = createModernButton("← Quay lại", new Color(255, 255, 255, 30), new Color(255, 255, 255, 50));
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backBtn.setPreferredSize(new Dimension(120, 40));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(backBtn, BorderLayout.EAST);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 30, 30, 30));

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);

        searchField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(248, 250, 252));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        searchField.setBorder(new EmptyBorder(10, 15, 10, 15));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setOpaque(false);
        searchField.setForeground(new Color(30, 41, 59));

        searchBtn = createModernButton("🔍 Tìm kiếm", new Color(99, 110, 250), new Color(79, 90, 230));
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchBtn.setPreferredSize(new Dimension(130, 44));

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);

        String[] columns = {"#", "Người chơi", "ELO", "Tổng điểm", "Số trận"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(48);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(241, 245, 249));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(243, 244, 246));
        table.setSelectionForeground(new Color(30, 41, 59));
        table.setIntercellSpacing(new Dimension(10, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(new Color(71, 85, 105));
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(226, 232, 240)));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(0).setMaxWidth(80);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        leftRenderer.setBorder(new EmptyBorder(0, 15, 0, 0));
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);

        for (int i = 2; i < 5; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        contentPanel.add(searchPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        mainCard.add(headerPanel, BorderLayout.NORTH);
        mainCard.add(contentPanel, BorderLayout.CENTER);

        container.add(mainCard, BorderLayout.CENTER);

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
                String rankEmoji = "";
                if (rank == 1) rankEmoji = "🥇 ";
                else if (rank == 2) rankEmoji = "🥈 ";
                else if (rank == 3) rankEmoji = "🥉 ";
                
                model.addRow(new Object[]{
                    rankEmoji + rank,
                    r[0],
                    r[1],
                    r[2],
                    (r.length > 3 ? r[3] : "0")
                });
                rank++;
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

    private JPanel createModernCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(0, 0, 0, 8));
                g2d.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, 16, 16);
                g2d.fillRoundRect(1, 3, getWidth() - 2, getHeight() - 2, 16, 16);

                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            }
        };
        card.setOpaque(false);
        return card;
    }

    private JButton createModernButton(String text, Color color, Color hoverColor) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isEnabled()) {
                    g2d.setColor(new Color(0, 0, 0, isHovered ? 25 : 15));
                    g2d.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, 12, 12);
                }

                if (isEnabled()) {
                    g2d.setColor(isHovered ? hoverColor : color);
                } else {
                    g2d.setColor(new Color(203, 213, 225));
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                super.paintComponent(g);
            }
        };

        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                try {
                    java.lang.reflect.Field field = button.getClass().getDeclaredField("isHovered");
                    field.setAccessible(true);
                    field.set(button, true);
                    button.repaint();
                } catch (Exception ignored) {}
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                try {
                    java.lang.reflect.Field field = button.getClass().getDeclaredField("isHovered");
                    field.setAccessible(true);
                    field.set(button, false);
                    button.repaint();
                } catch (Exception ignored) {}
            }
        });

        return button;
    }
}


