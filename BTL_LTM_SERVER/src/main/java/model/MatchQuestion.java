package model;

public class MatchQuestion {
    private int id;
    private int matchId;
    private int targetValue;
    private String numbers;
    private String allowedOps;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public int getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(int targetValue) {
        this.targetValue = targetValue;
    }

    public String getNumbers() {
        return numbers;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }

    public String getAllowedOps() {
        return allowedOps;
    }

    public void setAllowedOps(String allowedOps) {
        this.allowedOps = allowedOps;
    }
}
