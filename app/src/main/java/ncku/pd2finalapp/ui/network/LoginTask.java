package ncku.pd2finalapp.ui.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ncku.pd2finalapp.ReceiveAndSend.LoginCheck;

public class LoginTask extends NetworkTask<Void, LoginTask.LoginFailedException> {

    private final String username;
    private final String password;

    LoginTask(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    protected void task() {
        String response = new LoginCheck().LoginCheckData(username, password);
        onReceive(response);
    }

    private void onReceive(String response) {
        Gson gson = new Gson();
        JsonObject json = gson.fromJson(response, JsonObject.class);
        boolean succeed = json.getAsJsonPrimitive("success").getAsBoolean();
        if (succeed) {
            onSuccess(null);
        } else {
            onFailure(new LoginFailedException());
        }
    }

    public static class LoginFailedException extends Exception {
        public LoginFailedException() {
            super("Username or password is incorrect. Register if you haven't.");
        }
    }
}
