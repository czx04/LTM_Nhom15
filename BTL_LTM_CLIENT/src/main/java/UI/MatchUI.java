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
    private JLabel timeLabel, opponentNameLabel, opponentScoreLabel, myScoreLabel, questionLabel, feedbackLabel;
    private JPanel numberPanel, operatorPanel, questionButtonsPanel;
    private JButton[] questionButtons;
    private JTextField expressionField;
    private JButton submitBtn, clearBtn, backspaceBtn;
    private JSONArray questions;
    private int myScore = 0;
    private int opponentScore = 0;
    private MatchController matchController;
    private Set<Integer> answeredQuestions = new HashSet<>();
    private int currentQuestionIndex = 0;
    private int viewingQuestionIndex = 0;
    private Set<String> allowedNumbers = new HashSet<>();
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

        JPanel topPanel = new JPanel(new GridLayout(3, 2));
        timeLabel = new JLabel("⏱ Thời gian: " + time);
        opponentNameLabel = new JLabel("👤 Đối thủ: " + scoreOpponent);
        opponentScore = 0;
        opponentScoreLabel = new JLabel("🎯 Điểm đối thủ: 0");
        myScoreLabel = new JLabel("⭐ Điểm của bạn: 0");

        topPanel.add(timeLabel);
        topPanel.add(opponentNameLabel);
        topPanel.add(myScoreLabel);
        topPanel.add(opponentScoreLabel);

        container.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        questionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        questionButtonsPanel.setBorder(BorderFactory.createTitledBorder("Chọn câu hỏi"));
        questionButtons = new JButton[questions.length()];

        for (int i = 0; i < questions.length(); i++) {
            final int index = i;
            JButton btn = new JButton(String.valueOf(i + 1));
            btn.setPreferredSize(new Dimension(50, 50));
            btn.setFont(new Font("Arial", Font.BOLD, 16));

            if (i == 0) {
                btn.setBackground(new Color(255, 215, 0));
                btn.setEnabled(true);
            } else {
                btn.setBackground(Color.LIGHT_GRAY);
                btn.setEnabled(false);
            }

            btn.addActionListener(e -> updateQuestion(index));

            questionButtons[i] = btn;
            questionButtonsPanel.add(btn);
        }

        centerPanel.add(questionButtonsPanel, BorderLayout.NORTH);

        JPanel questionAndFeedbackPanel = new JPanel(new BorderLayout(5, 5));
        questionLabel = new JLabel("", SwingConstants.LEFT);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 20));

        feedbackLabel = new JLabel("", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("Arial", Font.BOLD, 16));
        feedbackLabel.setOpaque(true);
        feedbackLabel.setPreferredSize(new Dimension(0, 40));

        questionAndFeedbackPanel.add(questionLabel, BorderLayout.CENTER);
        questionAndFeedbackPanel.add(feedbackLabel, BorderLayout.SOUTH);

        centerPanel.add(questionAndFeedbackPanel, BorderLayout.CENTER);
        container.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(3, 1, 5, 5));

        numberPanel = new JPanel(new FlowLayout());
        operatorPanel = new JPanel(new FlowLayout());
        numberPanel.setBorder(BorderFactory.createTitledBorder("List Number"));
        operatorPanel.setBorder(BorderFactory.createTitledBorder("List Operator"));
        bottomPanel.add(numberPanel);
        bottomPanel.add(operatorPanel);

        JPanel exprPanel = new JPanel(new BorderLayout());
        expressionField = new JTextField();
        expressionField.setEditable(false);

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

        submitBtn.addActionListener(e -> handleSubmit());

        backspaceBtn.addActionListener(e -> {
            String current = expressionField.getText();
            if (!current.isEmpty()) {
                expressionField.setText(current.substring(0, current.length() - 1));
            }
        });

        clearBtn.addActionListener(e -> expressionField.setText(""));

        updateQuestion(0);
        refreshFrame(container);
    }

    /**
     * Hiển thị dữ liệu câu hỏi tương ứng
     */
    private void updateQuestion(int index) {
        viewingQuestionIndex = index;

        JSONObject q = questions.getJSONObject(index);
        int qid = q.getInt("id");

        String statusText = "";
        if (answeredQuestions.contains(qid)) {
            statusText = " ✅ [Đã hoàn thành]";
        } else if (index == currentQuestionIndex) {
            statusText = " 🎯 [Đang làm]";
        } else {
            statusText = " 🔒 [Chưa mở]";
        }

        questionLabel.setText("Câu " + (index + 1) + statusText + " - Mục tiêu: " + q.getInt("target"));

        allowedNumbers.clear();
        String[] nums = q.getString("numbers").split(",");
        for (String n : nums) {
            allowedNumbers.add(n.trim());
        }

        numberPanel.removeAll();
        for (String n : nums) {
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

        feedbackLabel.setText("");
        feedbackLabel.setBackground(null);
    }

    /**
     * Khi nhấn nút "Kiểm tra"
     */
    private void handleSubmit() {
        try {
            JSONObject q = questions.getJSONObject(viewingQuestionIndex);
            int qid = q.getInt("id");
            int target = q.getInt("target");
            String expr = expressionField.getText().trim();

            if (expr.isEmpty()) {
                showFeedback("⚠️ Bạn chưa nhập biểu thức nào!", new Color(255, 165, 0));
                return;
            }

            if (!validateExpression(expr)) {
                showFeedback("🚫 Gian lận phát hiện! Bạn đang sử dụng số không có trong danh sách.",
                        new Color(220, 38, 38));
                return;
            }

            if (answeredQuestions.contains(qid)) {
                showFeedback("☑ Bạn đã trả lời đúng câu hỏi này rồi! Không thể submit lại.", new Color(59, 130, 246));
                return;
            }

            if (viewingQuestionIndex != currentQuestionIndex) {
                showFeedback("⚠️ Bạn chỉ có thể làm câu " + (currentQuestionIndex + 1) + "!", new Color(255, 165, 0));
                return;
            }

            matchController.submitAnswer(expr, target);

        } catch (Exception ex) {
            showFeedback("❌ Lỗi: " + ex.getMessage(), new Color(239, 68, 68));
        }
    }

    /**
     * Server gửi MATCH_UPDATE (thời gian)
     */
    public void updateTimeAndScore(String time, String oppScore) {
        timeLabel.setText("⏱ Thời gian: " + time);
    }

    /**
     * Khi người chơi trả lời đúng
     */
    public void increaseMyScore() {
        if (myScoreLabel == null)
            return;
        myScore++;

        JSONObject q = questions.getJSONObject(currentQuestionIndex);
        answeredQuestions.add(q.getInt("id"));

        myScoreLabel.setText("⭐ Điểm của bạn: " + myScore);

        showFeedback("✅ Đúng rồi! Chuyển sang câu tiếp theo...", new Color(16, 185, 129));

        questionButtons[currentQuestionIndex].setBackground(new Color(34, 197, 94));
        questionButtons[currentQuestionIndex].setForeground(Color.WHITE);

        Timer delayTimer = new Timer(1500, e -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.length()) {
                questionButtons[currentQuestionIndex].setEnabled(true);
                questionButtons[currentQuestionIndex].setBackground(new Color(255, 215, 0));
                questionButtons[currentQuestionIndex].setForeground(Color.BLACK);

                updateQuestion(currentQuestionIndex);
            } else {
                showFeedback("🎉 Bạn đã hoàn thành tất cả câu hỏi!", new Color(16, 185, 129));
            }
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    /**
     * Khi đối thủ trả lời đúng
     */
    public void increaseOpponentScore() {
        if (opponentScoreLabel == null)
            return;

        opponentScore++;
        opponentScoreLabel.setText("🎯 Điểm đối thủ: " + opponentScore);
    }

    /**
     * Khi người chơi trả lời sai
     */
    public void notifyWrong() {
        showFeedback("❌ Sai rồi! Hãy thử lại!", new Color(239, 68, 68));
    }

    /**
     * Hiển thị feedback trong màn chơi với màu sắc phân biệt
     * - Xanh lá (16, 185, 129): Trả lời đúng
     * - Đỏ (239, 68, 68): Trả lời sai
     * - Đỏ đậm (220, 38, 38): Gian lận
     * - Cam (255, 165, 0): Cảnh báo
     * - Xanh dương (59, 130, 246): Thông tin (đã làm rồi)
     */
    private void showFeedback(String message, Color bgColor) {
        if (feedbackLabel == null)
            return;

        feedbackLabel.setText(message);
        feedbackLabel.setBackground(bgColor);
        feedbackLabel.setForeground(Color.WHITE);

        Color orange = new Color(255, 165, 0);
        Color blue = new Color(59, 130, 246);
        Color red = new Color(239, 68, 68);
        Color darkRed = new Color(220, 38, 38);

        int delay = 3000;
        if (bgColor.equals(red)) {
            delay = 2500;
        } else if (bgColor.equals(darkRed)) {
            delay = 4000;
        }

        if (bgColor.equals(orange) || bgColor.equals(blue) || bgColor.equals(red) || bgColor.equals(darkRed)) {
            Timer clearTimer = new Timer(delay, e -> {
                feedbackLabel.setText("");
                feedbackLabel.setBackground(null);
            });
            clearTimer.setRepeats(false);
            clearTimer.start();
        }
    }

    /**
     * Validate biểu thức để ngăn chặn gian lận
     * Kiểm tra xem các số trong biểu thức có nằm trong danh sách cho phép không
     */
    private boolean validateExpression(String expr) {
        if (expr == null || expr.isEmpty()) {
            return false;
        }

        String[] tokens = expr.split("[+\\-*/]");

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();

            if (token.isEmpty()) {
                continue;
            }

            if (!allowedNumbers.contains(token)) {
                System.out.println(
                        "⚠️ Gian lận phát hiện: Số '" + token + "' không có trong danh sách: " + allowedNumbers);
                return false;
            }
        }

        return true;
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
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }

        remainingSeconds = parseTimeToSeconds(timeStr);

        countdownTimer = new Timer(1000, e -> {
            if (remainingSeconds > 0) {
                remainingSeconds--;
                String formattedTime = formatSecondsToTime(remainingSeconds);
                timeLabel.setText("⏱ Thời gian: " + formattedTime);
            } else {
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
            return 180;
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
        showFeedback("⏰ Hết thời gian! Đang gửi kết quả...", new Color(239, 68, 68));

        submitBtn.setEnabled(false);
        clearBtn.setEnabled(false);
        backspaceBtn.setEnabled(false);

        if (matchController != null) {
            matchController.endMatch(myScore, opponentScore);
        } else {
            showFeedback("❌ Lỗi: Không thể gửi kết quả trận đấu!", new Color(239, 68, 68));
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
