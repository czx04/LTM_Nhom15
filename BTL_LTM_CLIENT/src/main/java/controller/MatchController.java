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

    public MatchController(BufferedReader in, BufferedWriter out, MatchUI ui) {
        this.in = in;
        this.out = out;
        this.ui = ui;
    }

    public void submitAnswer(String expr, int target) {
        try {
            // Gửi tới server
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
            JOptionPane.showMessageDialog(null, "🎉 Chính xác! +1 điểm");
        } else {
            ui.notifyWrong();
        }
    }
}
