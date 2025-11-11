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

        JLabel titleLabel = new JLabel("📜 Lịch Sử Đấu");
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

        String[] columns = {"Trận #", "Đối thủ", "Điểm", "Thay đổi ELO", "Kết quả", "Thời gian"};
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

        DefaultTableCellRenderer matchNumberRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(249, 250, 251));
                    }
                    setForeground(new Color(99, 110, 250));
                }
                return c;
            }
        };
        
        DefaultTableCellRenderer opponentRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.LEFT);
                setBorder(new EmptyBorder(0, 20, 0, 10));
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(249, 250, 251));
                    }
                    setForeground(new Color(30, 41, 59));
                }
                return c;
            }
        };
        
        DefaultTableCellRenderer scoreRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(249, 250, 251));
                    }
                    setForeground(new Color(139, 92, 246));
                }
                return c;
            }
        };
        
        DefaultTableCellRenderer eloRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(249, 250, 251));
                    }
                    
                    String val = value.toString();
                    if (val.contains("🔼") || val.startsWith("+")) {
                        setForeground(new Color(16, 185, 129));
                    } else if (val.contains("🔽") || val.startsWith("-")) {
                        setForeground(new Color(239, 68, 68));
                    } else {
                        setForeground(new Color(71, 85, 105));
                    }
                }
                return c;
            }
        };
        
        DefaultTableCellRenderer resultRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(249, 250, 251));
                    }
                    
                    String val = value.toString();
                    if (val.contains("✅") || val.toUpperCase().contains("THẮNG")) {
                        setForeground(new Color(16, 185, 129));
                        c.setBackground(new Color(236, 253, 245));
                    } else if (val.contains("❌") || val.toUpperCase().contains("THUA")) {
                        setForeground(new Color(239, 68, 68));
                        c.setBackground(new Color(254, 242, 242));
                    } else if (val.contains("⚖️") || val.toUpperCase().contains("HÒA")) {
                        setForeground(new Color(59, 130, 246));
                        c.setBackground(new Color(239, 246, 255));
                    }
                }
                return c;
            }
        };
        
        DefaultTableCellRenderer timeRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(249, 250, 251));
                    }
                    setForeground(new Color(100, 116, 139));
                }
                return c;
            }
        };

        table.getColumnModel().getColumn(0).setCellRenderer(matchNumberRenderer);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(0).setMaxWidth(100);

        table.getColumnModel().getColumn(1).setCellRenderer(opponentRenderer);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);

        table.getColumnModel().getColumn(2).setCellRenderer(scoreRenderer);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);

        table.getColumnModel().getColumn(3).setCellRenderer(eloRenderer);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);

        table.getColumnModel().getColumn(4).setCellRenderer(resultRenderer);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);

        table.getColumnModel().getColumn(5).setCellRenderer(timeRenderer);
        table.getColumnModel().getColumn(5).setPreferredWidth(200);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        mainCard.add(headerPanel, BorderLayout.NORTH);
        mainCard.add(contentPanel, BorderLayout.CENTER);

        container.add(mainCard, BorderLayout.CENTER);

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
                String result = r[4];
                String resultIcon = "";
                if (result.equalsIgnoreCase("WIN") || result.equalsIgnoreCase("THẮNG")) {
                    resultIcon = "✅ Thắng";
                } else if (result.equalsIgnoreCase("LOSE") || result.equalsIgnoreCase("THUA")) {
                    resultIcon = "❌ Thua";
                } else if (result.equalsIgnoreCase("DRAW") || result.equalsIgnoreCase("HÒA")) {
                    resultIcon = "⚖️ Hòa";
                } else {
                    resultIcon = result;
                }

                String eloChange = r[3];
                if (eloChange.startsWith("+")) {
                    eloChange = "🔼 " + eloChange;
                } else if (eloChange.startsWith("-")) {
                    eloChange = "🔽 " + eloChange;
                }

                model.addRow(new Object[]{
                    "#" + r[0],
                    r[1],
                    r[2],
                    eloChange,
                    resultIcon,
                    r[5]
                });
            }
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

