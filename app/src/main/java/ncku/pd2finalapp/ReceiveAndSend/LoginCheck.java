package ncku.pd2finalapp.ReceiveAndSend;

import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.ExecutionException;

public class LoginCheck extends ReceiveInfoFromBack{
    public String LoginCheckData(String username, String password)
    {
        this.sendtype = "POST";
        this.texttype = "json";
        this.Information = "{\n" +
                "  \"username\": \"" + username + "\",\n" +
                "  \"password\": \"" + password + "\"\n" +
                "}";
        AsyncTask<String, Void, String> temp = execute(network + "login");

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
