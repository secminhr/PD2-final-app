package ncku.pd2finalapp.ReceiveAndSend;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

//使用方法：new ReceiveInfoFromBack().execute("URL");僅接受一個url;

public class ReceiveInfoFromBackForGet extends AsyncTask<String, Void, String> {

    private Exception exception;
    protected static String network = "https://cryptic-island-19755.herokuapp.com/";
    protected String Information = "sasader";
    protected String sendtype = null;
    protected String texttype = null;


    protected String doInBackground(String... urls) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urls[0]);
            URLConnection urlConnection = url.openConnection();
            HttpsURLConnection connect = (HttpsURLConnection) urlConnection;
            if (texttype != null) {
                connect.setRequestProperty("Content-Type", "application/" + texttype);
            }
            //use this header to bypass the localtunnel page
            //in situation where backend is not hosted by localtunnel, this header should have no effect
            connect.setRequestProperty("Bypass-Tunnel-Reminder", "random string");
           // OutputStream Stringforoutput = connect.getOutputStream();
            //Stringforoutput.write(Information.getBytes(StandardCharsets.UTF_8));





            //get code
            int response = connect.getResponseCode();
            String c = String.valueOf(response);
            Log.e("ReceiveInfoFromBackForGet", "status code: " + response);

            //receive
            if( response < 400 ) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connect.getInputStream()));

                String urlString = "";
                String current;
                while ((current = in.readLine()) != null) {
                    urlString += current;
                }
                return urlString;
            }else {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connect.getErrorStream()));

                String urlString = "";
                String current;
                while ((current = in.readLine()) != null) {
                    urlString += current;
                }

                return urlString;
            }


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
