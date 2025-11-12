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
    private int opponentScore = 0; // Track điểm đối thủ
    private MatchController matchController;
    private Set<Integer> answeredQuestions = new HashSet<>();
    private int currentQuestionIndex = 0; // Câu hỏi hiện tại đang làm (câu mới nhất chưa hoàn thành)
    private int viewingQuestionIndex = 0; // Câu hỏi đang xem trên màn hình
    private Set<String> allowedNumbers = new HashSet<>(); // Danh sách số được phép sử dụng
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
        opponentNameLabel = new JLabel("👤 Đối thủ: " + scoreOpponent);
        opponentScore = 0;
        opponentScoreLabel = new JLabel("🎯 Điểm đối thủ: 0");
        myScoreLabel = new JLabel("⭐ Điểm của bạn: 0");

        topPanel.add(timeLabel);
        topPanel.add(opponentNameLabel);
        topPanel.add(myScoreLabel);
        topPanel.add(opponentScoreLabel);

        container.add(topPanel, BorderLayout.NORTH);

        // ====== CENTER (Câu hỏi + chọn câu dạng buttons) ======
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        // Panel chứa các button câu hỏi (1 2 3 4 5 ...)
        questionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        questionButtonsPanel.setBorder(BorderFactory.createTitledBorder("Chọn câu hỏi"));
        questionButtons = new JButton[questions.length()];

        for (int i = 0; i < questions.length(); i++) {
            final int index = i;
            JButton btn = new JButton(String.valueOf(i + 1));
            btn.setPreferredSize(new Dimension(50, 50));
            btn.setFont(new Font("Arial", Font.BOLD, 16));

            // Chỉ enable câu đầu tiên, các câu khác disable
            if (i == 0) {
                btn.setBackground(new Color(255, 215, 0)); // Màu vàng cho câu hiện tại
                btn.setEnabled(true);
            } else {
                btn.setBackground(Color.LIGHT_GRAY);
                btn.setEnabled(false);
            }

            btn.addActionListener(e -> {
                // Cho phép xem lại câu đã trả lời đúng
                updateQuestion(index);
            });

            questionButtons[i] = btn;
            questionButtonsPanel.add(btn);
        }

        centerPanel.add(questionButtonsPanel, BorderLayout.NORTH);

        // Label hiển thị câu hỏi và feedback
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

        // Hiển thị câu hỏi đầu tiên
        updateQuestion(0);
        refreshFrame(container);
    }

    /**
     * Hiển thị dữ liệu câu hỏi tương ứng
     */
    private void updateQuestion(int index) {
        viewingQuestionIndex = index; // Cập nhật câu đang xem

        JSONObject q = questions.getJSONObject(index);
        int qid = q.getInt("id");

        // Hiển thị trạng thái câu hỏi
        String statusText = "";
        if (answeredQuestions.contains(qid)) {
            statusText = " ✅ [Đã hoàn thành]";
        } else if (index == currentQuestionIndex) {
            statusText = " 🎯 [Đang làm]";
        } else {
            statusText = " 🔒 [Chưa mở]";
        }

        questionLabel.setText("Câu " + (index + 1) + statusText + " - Mục tiêu: " + q.getInt("target"));

        // Lưu danh sách số được phép sử dụng
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

        // Xóa feedback khi chuyển câu hỏi
        feedbackLabel.setText("");
        feedbackLabel.setBackground(null);
    }

    /**
     * Khi nhấn nút "Kiểm tra"
     */
    private void handleSubmit() {
        try {
            // Lấy câu hỏi đang xem trên màn hình
            JSONObject q = questions.getJSONObject(viewingQuestionIndex);
            int qid = q.getInt("id");
            int target = q.getInt("target");
            String expr = expressionField.getText().trim();

            if (expr.isEmpty()) {
                showFeedback("⚠️ Bạn chưa nhập biểu thức nào!", new Color(255, 165, 0));
                return;
            }

            // Validate chống gian lận
            if (!validateExpression(expr)) {
                showFeedback("🚫 Gian lận phát hiện! Bạn đang sử dụng số không có trong danh sách.",
                        new Color(220, 38, 38));
                return;
            }

            // Kiểm tra xem câu đang xem đã trả lời đúng chưa
            if (answeredQuestions.contains(qid)) {
                showFeedback("☑ Bạn đã trả lời đúng câu hỏi này rồi! Không thể submit lại.", new Color(59, 130, 246));
                return;
            }

            // Chỉ cho phép submit câu hiện tại đang làm
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
        JSONObject q = questions.getJSONObject(currentQuestionIndex);
        answeredQuestions.add(q.getInt("id"));

        myScoreLabel.setText("⭐ Điểm của bạn: " + myScore);

        // Hiển thị thông báo đúng
        showFeedback("✅ Đúng rồi! Chuyển sang câu tiếp theo...", new Color(16, 185, 129));

        // Đánh dấu button câu hiện tại là đã hoàn thành (màu xanh lá)
        // KHÔNG disable để vẫn có thể xem lại
        questionButtons[currentQuestionIndex].setBackground(new Color(34, 197, 94));
        questionButtons[currentQuestionIndex].setForeground(Color.WHITE);

        // Chuyển sang câu tiếp theo sau 1.5 giây
        Timer delayTimer = new Timer(1500, e -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.length()) {
                // Enable và highlight câu tiếp theo
                questionButtons[currentQuestionIndex].setEnabled(true);
                questionButtons[currentQuestionIndex].setBackground(new Color(255, 215, 0));
                questionButtons[currentQuestionIndex].setForeground(Color.BLACK);

                // Hiển thị câu hỏi mới (cũng cập nhật viewingQuestionIndex)
                updateQuestion(currentQuestionIndex);
            } else {
                // Đã hoàn thành tất cả câu hỏi
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

        // Tăng điểm đối thủ
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

        // Tự động ẩn feedback sau vài giây
        // - Warning (cam) và Info (xanh dương): 3 giây
        // - Error (đỏ): 2.5 giây
        // - Cheat (đỏ đậm): 4 giây (để người chơi nhận ra lỗi nghiêm trọng)
        Color orange = new Color(255, 165, 0);
        Color blue = new Color(59, 130, 246);
        Color red = new Color(239, 68, 68);
        Color darkRed = new Color(220, 38, 38);

        int delay = 3000; // mặc định 3 giây
        if (bgColor.equals(red)) {
            delay = 2500; // lỗi sai thì 2.5 giây
        } else if (bgColor.equals(darkRed)) {
            delay = 4000; // gian lận thì 4 giây để cảnh báo nghiêm trọng
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
            return false; // Biểu thức rỗng không hợp lệ để submit
        }

        // Tách các số ra khỏi biểu thức (bỏ qua toán tử +, -, *, /)
        String[] tokens = expr.split("[+\\-*/]");

        // Dùng for loop để check từng số
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();

            // Bỏ qua token rỗng (ví dụ: "5+" sẽ split thành ["5", ""])
            if (token.isEmpty()) {
                continue;
            }

            // Kiểm tra xem số này có trong danh sách cho phép không
            if (!allowedNumbers.contains(token)) {
                // Gian lận phát hiện!
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
        showFeedback("⏰ Hết thời gian! Đang gửi kết quả...", new Color(239, 68, 68));

        // Disable tất cả các nút
        submitBtn.setEnabled(false);
        clearBtn.setEnabled(false);
        backspaceBtn.setEnabled(false);

        // Gửi kết quả về server
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
