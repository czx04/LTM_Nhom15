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

public class Login extends BaseUI {
    private Register register = new Register();
    private static final Color PRIMARY_BLUE = new Color(38, 97, 187);
    private static final Color DARK_BLUE = new Color(4, 39, 227);
    private static final Color LIGHT_BLUE = new Color(200, 230, 255);
    private static final Color TEXT_DARK = new Color(0, 0, 0);
    private static final Color BORDER_GRAY = new Color(180, 180, 190);
    private static final Color PM_DARK_GRAY = new Color(21, 21, 27);
    private static final Color HOVER_BLUE = new Color(15, 115, 215);
    private static final Color TRANSPARENT_WHITE = new Color(255, 255, 255, 150); // Slightly more opaque card panel
    private static final Color PLACEHOLDER_COLOR = new Color(150, 150, 150); // Color for placeholder text

    public void showLogin(JFrame frame, BufferedReader in, BufferedWriter out) {
        setupFrame(frame, in, out);
        showUI(frame, in, out);
    }

    public void showUI(JFrame frame, BufferedReader in, BufferedWriter out) {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
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

        JPanel cardPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(TRANSPARENT_WHITE); // Use slightly more opaque white
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Slightly larger rounded corners
                g2d.setColor(new Color(200, 200, 210, 120)); // Slightly darker border
                g2d.setStroke(new BasicStroke(1.5f)); // Slightly thicker border
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setPreferredSize(new Dimension(380, 450)); // Slightly larger card
        cardPanel.setBorder(new EmptyBorder(36, 42, 36, 42)); // Increased padding

        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        cardGbc.weightx = 1.0;
        cardGbc.insets = new Insets(10, 0, 10, 0); // Adjusted vertical spacing

        // Title Label with an Icon
        JLabel titleLabel = new JLabel("Đăng Nhập");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30)); // Larger, bolder font
        titleLabel.setForeground(PRIMARY_BLUE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        // Load an icon for the title
        try {
            URL iconUrl = new URL("https://img.icons8.com/color/48/000000/luffy-monkey-d.png"); // Example Luffy icon
            ImageIcon icon = new ImageIcon(iconUrl);
            Image image = icon.getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH); // Scale it down
            titleLabel.setIcon(new ImageIcon(image));
            titleLabel.setIconTextGap(10); // Space between icon and text
        } catch (Exception e) {
            System.err.println("Could not load icon: " + e.getMessage());
        }

        cardGbc.gridx = 0;
        cardGbc.gridy = 0;
        cardGbc.gridwidth = 2;
        cardGbc.insets = new Insets(0, 0, 30, 0); // More space below title
        cardPanel.add(titleLabel, cardGbc);

        JLabel usernameLabel = new JLabel("Tên đăng nhập");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        usernameLabel.setForeground(TEXT_DARK);
        cardGbc.gridx = 0;
        cardGbc.gridy = 1;
        cardGbc.gridwidth = 2;
        cardGbc.insets = new Insets(16, 0, 6, 0);
        cardPanel.add(usernameLabel, cardGbc);

        // Custom JTextField with placeholder
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
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Larger font inside field
        usernameField.setPreferredSize(new Dimension(300, 48)); // Taller field
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 2, true), // Thicker, rounded border
                BorderFactory.createEmptyBorder(10, 14, 10, 14) // Increased padding
        ));
        usernameField.setForeground(TEXT_DARK);
        usernameField.setBackground(new Color(255, 255, 255, 180)); // Slightly transparent background
        cardGbc.gridx = 0;
        cardGbc.gridy = 2;
        cardGbc.gridwidth = 2;
        cardGbc.insets = new Insets(0, 0, 20, 0);
        cardPanel.add(usernameField, cardGbc);

        JLabel passwordLabel = new JLabel("Mật khẩu");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15)); // Slightly larger font
        passwordLabel.setForeground(TEXT_DARK); // Darker text for labels
        cardGbc.gridx = 0;
        cardGbc.gridy = 3;
        cardGbc.gridwidth = 2;
        cardGbc.insets = new Insets(0, 0, 6, 0);
        cardPanel.add(passwordLabel, cardGbc);

        // Custom JPasswordField with placeholder
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
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Larger font inside field
        passwordField.setPreferredSize(new Dimension(300, 48)); // Taller field
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 2, true), // Thicker, rounded border
                BorderFactory.createEmptyBorder(10, 14, 10, 14) // Increased padding
        ));
        passwordField.setForeground(TEXT_DARK);
        passwordField.setBackground(new Color(255, 255, 255, 180)); // Slightly transparent background
        cardGbc.gridx = 0;
        cardGbc.gridy = 4;
        cardGbc.gridwidth = 2;
        cardGbc.insets = new Insets(0, 0, 30, 0); // More space below password field
        cardPanel.add(passwordField, cardGbc);

        JButton loginButton = new JButton("Đăng Nhập");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Bolder and larger font
        loginButton.setForeground(PM_DARK_GRAY);
        loginButton.setBackground(PRIMARY_BLUE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setPreferredSize(new Dimension(300, 50)); // Taller button
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(HOVER_BLUE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(PRIMARY_BLUE);
            }
        });
        cardGbc.gridx = 0;
        cardGbc.gridy = 5;
        cardGbc.gridwidth = 2;
        cardGbc.insets = new Insets(10, 0, 15, 0);
        cardPanel.add(loginButton, cardGbc);

        JButton registerButton = new JButton("Đăng Ký");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Bolder and larger font
        registerButton.setForeground(DARK_BLUE);
        registerButton.setBackground(LIGHT_BLUE);
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createLineBorder(PRIMARY_BLUE, 0)); // Thicker border
        registerButton.setPreferredSize(new Dimension(300, 50)); // Taller button
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(new Color(140, 200, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(LIGHT_BLUE);
            }
        });
        cardGbc.gridx = 0;
        cardGbc.gridy = 6;
        cardGbc.gridwidth = 2;
        cardGbc.insets = new Insets(0, 0, 0, 0);
        cardPanel.add(registerButton, cardGbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(cardPanel, gbc);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());

            InputValidator.ValidationResult validation = InputValidator.validateLogin(username, password);
            if (!validation.isValid()) {
                JOptionPane.showMessageDialog(null, validation.getMessage(),
                        Constants.TITLE_WARNING, JOptionPane.WARNING_MESSAGE);
                return;
            }

            executeAsyncTask(loginButton,
                    () -> authController.handleLogin(username, password, in, out),
                    result -> {
                        System.out.println("Login request sent: " + result);
                    },
                    ex -> ResponseHandler.handleConnectionError()
            );
        });

        registerButton.addActionListener(e -> {
            showRegister();
        });

        refreshFrame(mainPanel);
    }

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