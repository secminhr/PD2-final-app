package ncku.pd2finalapp.ReceiveAndSend;

import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.ExecutionException;

public class LogoutCheck extends ReceiveInfoFromBack{
    public String LogoutCheckData()
    {
        this.sendtype = "GET";

        AsyncTask<String, Void, String> temp = super.execute(network + "logout");

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
