package ncku.pd2finalapp.ui.network.tasks;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ncku.pd2finalapp.ReceiveAndSend.RegisterCheck;

public class RegisterTask extends NetworkTask<Void, RegisterTask.UsernameExistsException> {

    private final String username;
    private final String nickname;
    private final String password;

    public RegisterTask(String username, String nickname, String password) {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
    }

    @Override
    protected void task() {
        String response = new RegisterCheck().RegisterCheckData(username, nickname, password);
        onReceive(response);
    }

    private void onReceive(String response) {
        Gson gson = new Gson();
        JsonObject json = gson.fromJson(response, JsonObject.class);
        boolean succeed = json.getAsJsonPrimitive("success").getAsBoolean();
        if (succeed) {
            onSuccess(null);
        } else {
            //This should be the only error that we'll receive
            //since we've checked that all field are not empty before sending
            onFailure(new UsernameExistsException());
        }
    }

    public static class UsernameExistsException extends Exception {
        public UsernameExistsException() {
            super("Username already exists");
        }
    }
}
