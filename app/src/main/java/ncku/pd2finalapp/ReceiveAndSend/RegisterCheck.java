package ncku.pd2finalapp.ReceiveAndSend;

import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.ExecutionException;

public class RegisterCheck extends ReceiveInfoFromBack {

    public String RegisterCheckData(String username, String nickname, String password, String faction) {
        sendtype = "POST";
        texttype = "json";
        Information = "{\n" +
                "  \"username\": \"" + username + "\",\n" +
                "  \"nickname\": \"" + nickname + "\",\n" +
                "  \"password\": \"" + password + "\",\n" +
                "  \"faction\": \"" + faction + "\"\n" +
                "}";
        AsyncTask<String, Void, String> temp = super.execute(network + "register");

        try {
            Log.e("TRY", temp.get());
            return temp.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


}


