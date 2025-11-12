package UI;

import util.Constants;
import util.ResponseHandler;
import util.InputValidator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.URL;

public class Register extends BaseUI {

    // --- Bảng màu và hằng số từ Login.java ---
    private static final Color PRIMARY_BLUE = new Color(38, 97, 187);
    private static final Color DARK_BLUE = new Color(4, 39, 227);
    private static final Color LIGHT_BLUE = new Color(200, 230, 255);
    private static final Color TEXT_DARK = new Color(0, 0, 0);
    private static final Color BORDER_GRAY = new Color(180, 180, 190);
    private static final Color PM_DARK_GRAY = new Color(21, 21, 27);
    private static final Color HOVER_BLUE = new Color(15, 115, 215);
    private static final Color TRANSPARENT_WHITE = new Color(255, 255, 255, 150); // Slightly more opaque card panel
    private static final Color PLACEHOLDER_COLOR = new Color(150, 150, 150); // Color for placeholder text
    // ------------------------------------------

    public void showRegister(JFrame frame, BufferedReader in, BufferedWriter out) {
        setupFrame(frame, in, out);
        showUI(frame, in, out);
    }

    public void showUI(JFrame frame, BufferedReader in, BufferedWriter out) {
        // --- Panel chính với hình nền ---
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    // Sử dụng cùng một hình nền
                    URL imageUrl = new URL("https://cdn2.fptshop.com.vn/unsafe/Uploads/images/tin-tuc/184109/Originals/kho-hinh-anh-anime-background-1.png");
                    Image img = new ImageIcon(imageUrl).getImage();
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);

                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                    g2d.setColor(new Color(0, 0, 0, 50));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                } catch (Exception e) {
                    g.setColor(PRIMARY_BLUE);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // --- "Card" mờ ở giữa ---
        JPanel cardPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(TRANSPARENT_WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(new Color(200, 200, 210, 120));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            }
        };
        cardPanel.setOpaque(false);
        // Kích thước card giống hệt Login vì chỉ có 2 trường
        cardPanel.setPreferredSize(new Dimension(380, 450));
        cardPanel.setBorder(new EmptyBorder(36, 42, 36, 42));

        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        cardGbc.weightx = 1.0;
        cardGbc.insets = new Insets(10, 0, 10, 0);

        // --- Tiêu đề (Thay đổi thành "Đăng Ký") ---
        JLabel titleLabel = new JLabel("Đăng Ký"); // THAY ĐỔI
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(PRIMARY_BLUE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        // Giữ lại icon cho nhất quán
        try {
            URL iconUrl = new URL("https://img.icons8.com/color/48/000000/luffy-monkey-d.png");
            ImageIcon icon = new ImageIcon(iconUrl);
            Image image = icon.getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH);
            titleLabel.setIcon(new ImageIcon(image));
            titleLabel.setIconTextGap(10);
        } catch (Exception e) {
            System.err.println("Could not load icon: " + e.getMessage());
        }

        cardGbc.gridx = 0;
        cardGbc.gridy = 0;
        cardGbc.gridwidth = 2;
        cardGbc.insets = new Insets(0, 0, 30, 0);
        cardPanel.add(titleLabel, cardGbc);

        // --- Tên đăng nhập ---
        JLabel usernameLabel = new JLabel("Tên đăng nhập");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        usernameLabel.setForeground(TEXT_DARK);
        cardGbc.gridx = 0;
        cardGbc.gridy = 1;
        cardGbc.gridwidth = 2;
        cardGbc.insets = new Insets(16, 0, 6, 0);
        cardPanel.add(usernameLabel, cardGbc);

        // --- Trường Tên đăng nhập (với placeholder) ---
        JTextField usernameField = new JTextField(Constants.TEXT_FIELD_COLUMNS) {
            private String placeholder = "Nhập tên đăng nhập của bạn";
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (placeholder.length() == 0 || getText().length() > 0) {
                    return;
                }
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(PLACEHOLDER_COLOR);
                g2d.setFont(getFont());
                g2d.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
            }
        };
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(300, 48));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 2, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        usernameField.setForeground(TEXT_DARK);
        usernameField.setBackground(new Color(255, 255, 255, 180));
        cardGbc.gridx = 0;
        cardGbc.gridy = 2;
        cardGbc.gridwidth = 2;
        cardGbc.insets = new Insets(0, 0, 20, 0);
        cardPanel.add(usernameField, cardGbc);

        // --- Mật khẩu ---
        JLabel passwordLabel = new JLabel("Mật khẩu");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordLabel.setForeground(TEXT_DARK);
        cardGbc.gridx = 0;
        cardGbc.gridy = 3;
        cardGbc.gridwidth = 2;
        cardGbc.insets = new Insets(0, 0, 6, 0);
        cardPanel.add(passwordLabel, cardGbc);

        // --- Trường Mật khẩu (với placeholder) ---
        JPasswordField passwordField = new JPasswordField(Constants.TEXT_FIELD_COLUMNS) {
            private String placeholder = "Nhập mật khẩu của bạn";
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (placeholder.length() == 0 || getText().length() > 0) {
                    return;
                }
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(PLACEHOLDER_COLOR);
                g2d.setFont(getFont());
                g2d.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
            }
        };
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(300, 48));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 2, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        passwordField.setForeground(TEXT_DARK);
        passwordField.setBackground(new Color(255, 255, 255, 180));
        cardGbc.gridx = 0;
        cardGbc.gridy = 4;
        cardGbc.gridwidth = 2;
        cardGbc.insets = new Insets(0, 0, 30, 0);
        cardPanel.add(passwordField, cardGbc);

        // --- Nút Đăng Ký (Style nút chính) ---
        JButton regisButton = new JButton("Đăng Ký"); // THAY ĐỔI
        regisButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        regisButton.setForeground(PM_DARK_GRAY);
        regisButton.setBackground(PRIMARY_BLUE); // Style chính
        regisButton.setFocusPainted(false);
        regisButton.setBorderPainted(false);
        regisButton.setPreferredSize(new Dimension(300, 50));
        regisButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        regisButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                regisButton.setBackground(HOVER_BLUE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                regisButton.setBackground(PRIMARY_BLUE);
            }
        });
        cardGbc.gridx = 0;
        cardGbc.gridy = 5; // Vị trí nút chính
        cardGbc.gridwidth = 2;
        cardGbc.insets = new Insets(10, 0, 15, 0);
        cardPanel.add(regisButton, cardGbc);

        // --- Nút Quay Lại (Style nút phụ) ---
        JButton backButton = new JButton("Quay Lại"); // THAY ĐỔI
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        backButton.setForeground(DARK_BLUE); // Style phụ
        backButton.setBackground(LIGHT_BLUE); // Style phụ
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createLineBorder(PRIMARY_BLUE, 0));
        backButton.setPreferredSize(new Dimension(300, 50));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backButton.setBackground(new Color(140, 200, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backButton.setBackground(LIGHT_BLUE);
            }
        });
        cardGbc.gridx = 0;
        cardGbc.gridy = 6; // Vị trí nút phụ
        cardGbc.gridwidth = 2;
        cardGbc.insets = new Insets(0, 0, 0, 0);
        cardPanel.add(backButton, cardGbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(cardPanel, gbc);

        // --- LOGIC (Giữ nguyên từ code Register cũ của bạn) ---
        regisButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());

            // Validate input
            InputValidator.ValidationResult validation = InputValidator.validateLogin(username, password);
            if (!validation.isValid()) {
                JOptionPane.showMessageDialog(null, validation.getMessage(),
                        Constants.TITLE_WARNING, JOptionPane.WARNING_MESSAGE);
                return;
            }

            executeAsyncTask(regisButton,
                    () -> authController.handleRegister(username, password, in, out),
                    result -> {
                        System.out.println("Register request sent: " + result);
                    },
                    ex -> ResponseHandler.handleConnectionError()
            );
        });

        backButton.addActionListener(e -> {
            showLogin(); // Logic từ code Register cũ
        });
        // --- KẾT THÚC LOGIC ---

        refreshFrame(mainPanel);
    }

    // --- Các hàm helper (Sao chép từ Login để nhất quán) ---
    private JTextField createPlaceholderTextField(String placeholderText, int columns) {
        JTextField field = new JTextField(columns) {
            private String placeholder = placeholderText;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (placeholder.length() == 0 || getText().length() > 0) {
                    return;
                }
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(PLACEHOLDER_COLOR);
                g2d.setFont(getFont());
                g2d.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 48));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 2, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        field.setForeground(TEXT_DARK);
        field.setBackground(new Color(255, 255, 255, 180));
        return field;
    }
    private JPasswordField createPlaceholderPasswordField(String placeholderText, int columns) {
        JPasswordField field = new JPasswordField(columns) {
            private String placeholder = placeholderText;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (placeholder.length() == 0 || getText().length() > 0) {
                    return;
                }
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(PLACEHOLDER_COLOR);
                g2d.setFont(getFont());
                g2d.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 48));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 2, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        field.setForeground(TEXT_DARK);
        field.setBackground(new Color(255, 255, 255, 180));
        return field;
    }
}