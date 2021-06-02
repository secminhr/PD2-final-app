package ncku.pd2finalapp.ReceiveAndSend;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class AttackCheck extends ReceiveInfoFromBackForGet {
    private final static String LAT_PARAM_NAME = "lat";
    private final static String LON_PARAM_NAME = "lon";
    private final static String TIME_PARAM_NAME = "time";
    private final static String TARGET_PARAM_NAME = "purpose";

    public void sendAttackData(List<LatLng> points, long durationInMinutes, LatLng target) {
        sendtype = "GET";
        String parameters = "?";
        parameters += parse(points, durationInMinutes, target);
        AsyncTask<String, Void, String> task = execute(network + "attack" + parameters);

        try {
            task.get();
        } catch (ExecutionException|InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String parse(List<LatLng> points, long duration, LatLng target) {
        StringBuilder params = new StringBuilder();
        for (LatLng point: points) {
            params.append(LAT_PARAM_NAME).append("=").append(point.latitude).append("&")
                  .append(LON_PARAM_NAME).append("=").append(point.longitude).append("&");
        }
        params.append(TIME_PARAM_NAME).append("=").append(duration).append("&")
              .append(TARGET_PARAM_NAME).append("=").append(target.latitude).append("&")
              .append(TARGET_PARAM_NAME).append("=").append(target.longitude);
        return params.toString();
    }
}
