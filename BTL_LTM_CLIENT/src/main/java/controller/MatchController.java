package controller;

import UI.MatchUI;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class MatchController {
    private BufferedReader in;
    private BufferedWriter out;
    private MatchUI ui;
    private String matchId;
    private String username;

    public MatchController(BufferedReader in, BufferedWriter out, MatchUI ui) {
        this.in = in;
        this.out = out;
        this.ui = ui;
    }

    public void setMatchInfo(String matchId, String username) {
        System.out.println("MatchController.setMatchInfo: matchId=" + matchId + ", username=" + username);
        this.matchId = matchId;
        this.username = username;
    }

    public void submitAnswer(String expr, int target) {
        try {
            out.write("SUBMIT_ANSWER|" + expr + "|" + target);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onAnswerResult(boolean correct) {
        if (correct) {
            ui.increaseMyScore();
        } else {
            ui.notifyWrong();
        }
    }

    /**
     * Gửi request kết thúc trận đấu khi hết giờ
     */
    public void endMatch(int myScore, int opponentScore) {
        try {
            if (matchId == null || username == null) {
                String error = "Lỗi: matchId hoặc username chưa được set!\n" +
                        "matchId=" + matchId + ", username=" + username;
                System.err.println(error);
                JOptionPane.showMessageDialog(null, error, "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Format mới: MATCH_END|matchId|username|myScore|opponentScore
            String payload = String.join("|",
                    "MATCH_END",
                    matchId,
                    username,
                    String.valueOf(myScore),
                    String.valueOf(opponentScore));

            out.write(payload);
            out.newLine();
            out.flush();

            System.out.println("Sent MATCH_END to server: " + payload);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi gửi kết quả trận đấu: " + e.getMessage());
        }
    }
}
