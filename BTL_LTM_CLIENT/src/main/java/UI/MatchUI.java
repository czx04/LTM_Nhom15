package UI;

import controller.MatchController;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.HashSet;
import java.util.Set;

public class MatchUI extends BaseUI {
    private JLabel timeLabel, opponentNameLabel, opponentScoreLabel, myScoreLabel, questionLabel;
    private JPanel numberPanel, operatorPanel;
    private JComboBox<String> questionCombo;
    private JTextField expressionField;
    private JButton submitBtn, clearBtn, backspaceBtn;
    private JSONArray questions;
    private int myScore = 0;
    private int opponentScore = 0; // Track điểm đối thủ
    private MatchController matchController;
    private Set<Integer> answeredQuestions = new HashSet<>();
    private Timer countdownTimer;
    private int remainingSeconds = 0;
    private String matchId;
    private String username;

    /**
     * Hàm override bắt buộc (từ BaseUI)
     * — Giúp class không bị lỗi abstract
     */
    @Override
    public void showUI(JFrame frame, BufferedReader in, BufferedWriter out) {
        // Gọi UI mặc định khi chưa có dữ liệu match
        showUI(frame, in, out, "03:00", "0");
    }

    /**
     * Hàm được gọi khi nhận được dữ liệu MATCH_START từ server
     */
    public void showMatch(JFrame frame, BufferedReader in, BufferedWriter out,
            String matchId, String questionsJson, String time, String scoreOpponent) {
        System.out.println("MatchUI.showMatch called: matchId=" + matchId);
        setupFrame(frame, in, out);
        this.questions = new JSONArray(questionsJson);
        this.matchId = matchId;
        this.matchController = new MatchController(in, out, this);

        // Set matchInfo ngay sau khi tạo matchController
        if (this.username != null) {
            System.out.println("Username already set, calling setMatchInfo: " + this.username);
            this.matchController.setMatchInfo(this.matchId, this.username);
        } else {
            System.out.println("Username not set yet, will be set later");
        }

        showUI(frame, in, out, time, scoreOpponent);
        startCountdown(time);
    }

    /**
     * Set username cho trận đấu
     */
    public void setUsername(String username) {
        System.out.println("MatchUI.setUsername called: username=" + username);
        this.username = username;
        if (matchController != null) {
            System.out.println("Setting matchInfo: matchId=" + matchId + ", username=" + username);
            matchController.setMatchInfo(matchId, username);
        } else {
            System.out.println("matchController is null, will set later");
        }
    }

    /**
     * Hàm chính để hiển thị giao diện trận đấu
     */
    public void showUI(JFrame frame, BufferedReader in, BufferedWriter out,
            String time, String scoreOpponent) {
        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ====== HEADER ======
        JPanel topPanel = new JPanel(new GridLayout(3, 2));
        timeLabel = new JLabel("⏱ Thời gian: " + time);
        opponentNameLabel = new JLabel("👤 Đối thủ: " + scoreOpponent); // scoreOpponent là tên đối thủ
        opponentScoreLabel = new JLabel("🎯 Điểm đối thủ: 0");
        myScoreLabel = new JLabel("⭐ Điểm của bạn: 0");

        topPanel.add(timeLabel);
        topPanel.add(opponentNameLabel);
        topPanel.add(myScoreLabel);
        topPanel.add(opponentScoreLabel);

        container.add(topPanel, BorderLayout.NORTH);

        // ====== CENTER (Câu hỏi + chọn câu) ======
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        questionCombo = new JComboBox<>();
        for (int i = 0; i < questions.length(); i++) {
            questionCombo.addItem("Câu " + (i + 1));
        }
        questionLabel = new JLabel("", SwingConstants.LEFT);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 20));
        centerPanel.add(questionCombo, BorderLayout.NORTH);
        centerPanel.add(questionLabel, BorderLayout.CENTER);
        container.add(centerPanel, BorderLayout.CENTER);

        // ====== BOTTOM (List Number + Operator + Nhập biểu thức) ======
        JPanel bottomPanel = new JPanel(new GridLayout(3, 1, 5, 5));

        numberPanel = new JPanel(new FlowLayout());
        operatorPanel = new JPanel(new FlowLayout());
        numberPanel.setBorder(BorderFactory.createTitledBorder("List Number"));
        operatorPanel.setBorder(BorderFactory.createTitledBorder("List Operator"));
        bottomPanel.add(numberPanel);
        bottomPanel.add(operatorPanel);

        JPanel exprPanel = new JPanel(new BorderLayout());
        expressionField = new JTextField();
        expressionField.setEditable(false); // Không cho nhập trực tiếp

        // Panel chứa các nút điều khiển
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        backspaceBtn = new JButton("⌫ Xóa");
        clearBtn = new JButton("🗑 Xóa hết");
        submitBtn = new JButton("✅ Kiểm tra");

        buttonPanel.add(backspaceBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(submitBtn);

        exprPanel.add(expressionField, BorderLayout.CENTER);
        exprPanel.add(buttonPanel, BorderLayout.EAST);
        bottomPanel.add(exprPanel);

        container.add(bottomPanel, BorderLayout.SOUTH);

        // ====== LOGIC ======
        questionCombo.addActionListener(e -> updateQuestion(questionCombo.getSelectedIndex()));
        submitBtn.addActionListener(e -> handleSubmit());

        // Xóa từng ký tự (backspace)
        backspaceBtn.addActionListener(e -> {
            String current = expressionField.getText();
            if (!current.isEmpty()) {
                expressionField.setText(current.substring(0, current.length() - 1));
            }
        });

        // Xóa tất cả
        clearBtn.addActionListener(e -> expressionField.setText(""));

        updateQuestion(0);
        refreshFrame(container);
    }

    /**
     * Hiển thị dữ liệu câu hỏi tương ứng khi chọn combobox
     */
    private void updateQuestion(int index) {
        JSONObject q = questions.getJSONObject(index);
        questionLabel.setText("🎯 Mục tiêu: " + q.getInt("target"));

        numberPanel.removeAll();
        for (String n : q.getString("numbers").split(",")) {
            JButton btn = new JButton(n.trim());
            btn.addActionListener(e -> expressionField.setText(expressionField.getText() + n.trim()));
            numberPanel.add(btn);
        }

        operatorPanel.removeAll();
        for (String op : q.getString("ops").split("")) {
            JButton btn = new JButton(op);
            btn.addActionListener(e -> expressionField.setText(expressionField.getText() + op));
            operatorPanel.add(btn);
        }

        numberPanel.revalidate();
        operatorPanel.revalidate();
        numberPanel.repaint();
        operatorPanel.repaint();
        expressionField.setText("");
    }

    /**
     * Khi nhấn nút “Kiểm tra”
     */
    private void handleSubmit() {
        try {
            int index = questionCombo.getSelectedIndex();
            JSONObject q = questions.getJSONObject(index);
            int qid = q.getInt("id");
            int target = q.getInt("target");
            String expr = expressionField.getText().trim();

            if (expr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "⚠️ Bạn chưa nhập biểu thức nào!");
                return;
            }

            if (answeredQuestions.contains(qid)) {
                JOptionPane.showMessageDialog(null, "⚠️ Bạn đã trả lời đúng câu này rồi!");
                return;
            }

            matchController.submitAnswer(expr, target);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Lỗi khi gửi kết quả: " + ex.getMessage());
        }
    }

    /**
     * Server gửi MATCH_UPDATE (thời gian)
     */
    public void updateTimeAndScore(String time, String oppScore) {
        timeLabel.setText("⏱ Thời gian: " + time);
        // Không cập nhật điểm đối thủ ở đây nữa, dùng increaseOpponentScore() riêng
    }

    /**
     * Khi người chơi trả lời đúng
     */
    public void increaseMyScore() {
        if (myScoreLabel == null)
            return;
        myScore++;

        // ✅ Đánh dấu câu hiện tại là đã trả lời đúng
        int index = questionCombo.getSelectedIndex();
        JSONObject q = questions.getJSONObject(index);
        answeredQuestions.add(q.getInt("id"));

        myScoreLabel.setText("⭐ Điểm của bạn: " + myScore);
    }

    /**
     * Khi đối thủ trả lời đúng
     */
    public void increaseOpponentScore() {
        if (opponentScoreLabel == null)
            return;

        // Tăng điểm đối thủ
        opponentScore++;
        opponentScoreLabel.setText("🎯 Điểm đối thủ: " + opponentScore);
    }

    /**
     * Khi người chơi trả lời sai
     */
    public void notifyWrong() {
        JOptionPane.showMessageDialog(null, "❌ Sai rồi! Hãy thử lại!");
    }

    /**
     * Dùng cho controller gọi ngược lại
     */
    public MatchController getMatchController() {
        return matchController;
    }

    /**
     * Bắt đầu đếm ngược thời gian
     */
    private void startCountdown(String timeStr) {
        // Dừng timer cũ nếu có
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }

        // Parse thời gian từ format "MM:SS"
        remainingSeconds = parseTimeToSeconds(timeStr);

        // Tạo timer mới đếm ngược mỗi giây
        countdownTimer = new Timer(1000, e -> {
            if (remainingSeconds > 0) {
                remainingSeconds--;
                String formattedTime = formatSecondsToTime(remainingSeconds);
                timeLabel.setText("⏱ Thời gian: " + formattedTime);
            } else {
                // Hết thời gian
                countdownTimer.stop();
                handleTimeUp();
            }
        });

        countdownTimer.start();
    }

    /**
     * Chuyển đổi thời gian từ format "MM:SS" thành số giây
     */
    private int parseTimeToSeconds(String timeStr) {
        try {
            String[] parts = timeStr.split(":");
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            return minutes * 60 + seconds;
        } catch (Exception e) {
            return 180; // Mặc định 3 phút nếu parse lỗi
        }
    }

    /**
     * Chuyển đổi số giây thành format "MM:SS"
     */
    private String formatSecondsToTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Dừng timer khi trận đấu kết thúc
     */
    public void stopTimer() {
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }
    }

    /**
     * Xử lý khi hết thời gian
     */
    private void handleTimeUp() {
        JOptionPane.showMessageDialog(null, "⏰ Hết thời gian!\nĐang gửi kết quả...",
                "Hết Giờ", JOptionPane.INFORMATION_MESSAGE);

        // Gửi kết quả về server
        if (matchController != null) {
            matchController.endMatch(myScore);
        } else {
            JOptionPane.showMessageDialog(null, "Lỗi: Không thể gửi kết quả trận đấu!");
        }
    }

    /**
     * Lấy điểm hiện tại của người chơi
     */
    public int getMyScore() {
        return myScore;
    }

    /**
     * Lấy điểm hiện tại của đối thủ
     */
    public int getOpponentScore() {
        return opponentScore;
    }
}
