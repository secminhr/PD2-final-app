package ncku.pd2finalapp.ReceiveAndSend;
import android.icu.text.IDNA;
import android.os.AsyncTask;
import android.util.Log;
import java.net.HttpURLConnection;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

//使用方法：new ReceiveInfoFromBack().execute("URL");僅接受一個url;

import javax.net.ssl.HttpsURLConnection;

public class ReceiveInfoFromBack extends AsyncTask<String, Void, String> {

    private Exception exception;
    protected static String network = "https://fuzzy-cheetah-9.loca.lt/";
    protected String Information = "sasader";
    protected String sendtype = null;
    protected String texttype = null;



    protected String doInBackground(String... urls) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urls[0]);
            URLConnection urlConnection = url.openConnection();
            HttpsURLConnection connect = (HttpsURLConnection) urlConnection;
            connect.setRequestMethod(sendtype);



            //write
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type","application/" + texttype);
            OutputStream Stringforoutput = connect.getOutputStream();
            Stringforoutput.write(Information.getBytes(StandardCharsets.UTF_8));

            //get code
            int response = connect.getResponseCode();
            String c = String.valueOf(response);
            Log.e("URLTRY2", "staticcode" + c);


            //receive
            if( response < 400 ) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connect.getInputStream()));

                String urlString = "";
                String current;
                while ((current = in.readLine()) != null) {
                    urlString += current;
                }
                Log.e("URLTRY2", urlString);
                return urlString;
            }else {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connect.getErrorStream()));

                String urlString = "";
                String current;
                while ((current = in.readLine()) != null) {
                    urlString += current;
                }
                Log.e("URLTRY2", urlString);
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
