package ncku.pd2finalapp.ReceiveAndSend;

import android.os.AsyncTask;

import java.util.concurrent.ExecutionException;

public class GetFortData extends ReceiveInfoFromBackForGet {
    private static final String ENDPOINT = "positionAndBlood";


    public String getFortData() {
        sendtype = "GET";

        AsyncTask<String, Void, String> task = execute(network + ENDPOINT);
        try {
            return task.get();
        } catch (ExecutionException|InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
