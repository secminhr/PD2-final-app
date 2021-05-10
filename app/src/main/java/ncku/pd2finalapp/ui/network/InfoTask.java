package ncku.pd2finalapp.ui.network;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ncku.pd2finalapp.ui.info.UserInfo;

class InfoTask extends NetworkTask<UserInfo, NoException> {

    @Override
    protected void task() {
        //String response = new InfoCheck().InfoCheckData();
        String response = "{\n" +
                "    \"username\": \"sssss\",\n" +
                "    \"status\": {\n" +
                "        \"exp\": 0,\n" +
                "        \"level\": 0,\n" +
                "        \"nickname\": \"s1s\",\n" +
                "        \"faction\": \"A\"\n" +
                "    }\n" +
                "}";
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
