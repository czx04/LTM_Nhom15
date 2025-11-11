package model;

public class UserOnlineInfo {
    private String username;
    private String status; // AVAILABLE hoặc IN_MATCH

    public UserOnlineInfo(String username, String status) {
        this.username = username;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return "AVAILABLE".equals(status);
    }

    public boolean isInMatch() {
        return "IN_MATCH".equals(status);
    }

    @Override
    public String toString() {
        return username + " (" + status + ")";
    }
}

