package ncku.pd2finalapp.ReceiveAndSend;

import android.os.AsyncTask;

import java.util.concurrent.ExecutionException;

public class FortData extends ReceiveInfoFromBackForGet {
    private static final String ENDPOINT = "";


    public String getFortData() {
        //TODO: adapt to backend
        sendtype = "GET";

        AsyncTask<String, Void, String> task = execute(ENDPOINT);
        try {
            return task.get();
        } catch (ExecutionException|InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
