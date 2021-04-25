package ncku.pd2finalapp.login;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ncku.pd2finalapp.ReceiveAndSend.LoginCheck;

class LoginRepoImpl implements LoginRepo {

    //provide empty callback as default to avoid null handling
    private SuccessCallback successCallback = () -> {};
    private FailureCallback failureCallback = (exception) -> {};

    @Override
    public void setOnSuccessCallback(SuccessCallback callback) {
        successCallback = callback;
    }

    @Override
    public void setOnFailureCallback(FailureCallback callback) {
        failureCallback = callback;
    }

    @Override
    public void login(String username, String password) {
        String response = new LoginCheck().LoginCheckData(username, password);
        Gson gson = new Gson();
        JsonObject json = gson.fromJson(response, JsonObject.class);
        boolean succeed = json.getAsJsonPrimitive("success").getAsBoolean();
        if (succeed) {
            successCallback.onSuccess();
        } else {
            failureCallback.onFailure(new LoginFailedException());
        }
    }
}
