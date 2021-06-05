package ncku.pd2finalapp.ui.info;

public class UserInfo {
    private String username;
    private Status status;

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return status.nickname;
    }

    static class Status {
        private String nickname;
    }
}
