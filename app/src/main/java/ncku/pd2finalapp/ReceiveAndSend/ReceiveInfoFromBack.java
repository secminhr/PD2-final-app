package ncku.pd2finalapp.ReceiveAndSend;
import android.os.AsyncTask;
import android.util.Log;
import java.net.HttpURLConnection;
import java.net.*;
import java.io.*;

//使用方法：new ReceiveInfoFromBack().execute("URL");僅接受一個url;

import javax.net.ssl.HttpsURLConnection;

public class ReceiveInfoFromBack extends AsyncTask<String, Void, Void> {

    private Exception exception;

    protected Void doInBackground(String... urls) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urls[0]);
            URLConnection urlConnection = url.openConnection();
            HttpsURLConnection connect = (HttpsURLConnection) urlConnection;
            int response = connect.getResponseCode();
            String c = String.valueOf(response);
            Log.e("URLTRY2", "staticcode" + c);
            //bug
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connect.getErrorStream()));
            String urlString = "";
            String current;
            while((current = in.readLine()) != null)
            {
                urlString += current;
            }
            Log.e("URLTRY2","prefix" + urlString);


        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    protected void onPostExecute(Void feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}
