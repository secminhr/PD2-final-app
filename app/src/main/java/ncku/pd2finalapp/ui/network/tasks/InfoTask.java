package ncku.pd2finalapp.ui.network.tasks;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ncku.pd2finalapp.ReceiveAndSend.InfoCheck;
import ncku.pd2finalapp.ui.info.UserInfo;

public class InfoTask extends NetworkTask<UserInfo, NoException> {

    @Override
    protected void task() {
        String response = new InfoCheck().InfoCheckData();
        onReceive(response);
    }

    private void onReceive(String response) {
        Gson gson = new Gson();
        JsonObject json = gson.fromJson(response, JsonObject.class);
        JsonElement successField = json.get("success");
        if (successField != null) {
            //This should never happen, since user must be logged in before reaching this task
            onFailure(new NoException());
        } else {
            //Info received successfully
            UserInfo info = gson.fromJson(response, UserInfo.class);
            onSuccess(info);
        }
    }
}
