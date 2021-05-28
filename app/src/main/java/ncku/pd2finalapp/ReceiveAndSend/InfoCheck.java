package ncku.pd2finalapp.ReceiveAndSend;

import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.ExecutionException;

public class InfoCheck extends ReceiveInfoFromBackForGet{
    public String InfoCheckData()
    {
        this.sendtype = "GET";
        Log.e("type",this.sendtype);

        AsyncTask<String, Void, String> temp = super.execute(network + "info");

        Log.e("type2",this.sendtype);
        try {
            Log.e("tet",temp.get());
            Log.e("INFO",  temp.get());
            return temp.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}


//String response = new LoginCheck().LoginCheckData(username, password);
//get 會拿到空的東西 導致強制關掉