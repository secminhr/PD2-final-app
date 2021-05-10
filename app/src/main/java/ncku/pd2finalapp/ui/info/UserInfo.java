package ncku.pd2finalapp.ui.info;

public class UserInfo {
    private String username;
    private Status status;

    public String getUsername() {
        return username;
    }

    public int getExp() {
        return status.exp;
    }

    public int getLevel() {
        return status.level;
    }

    public String getNickname() {
        return status.nickname;
    }

    public String getFaction() {
        return status.faction;
    }

    static class Status {
        private int exp;
        private int level;
        private String nickname;
        private String faction;
    }
}
