package ncku.pd2finalapp.ui.network.tasks;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.List;

import ncku.pd2finalapp.ReceiveAndSend.GetFortData;
import ncku.pd2finalapp.ui.map.FortData;

public class GetFortDataTask extends NetworkTask<List<FortData>, NoException> {

    @Override
    protected void task() {
        String response = new GetFortData().getFortData();
        onSuccess(parse(response));
    }

    private List<FortData> parse(String response) {
        Gson gson = new Gson();
        JsonArray arr = gson.fromJson(response, JsonArray.class);
        ArrayList<FortData> forts = new ArrayList<>();
        for (int i = 0; i < arr.get(0).getAsJsonArray().size(); i++) {
            double lat = arr.get(0).getAsJsonArray().get(i).getAsDouble();
            double lng = arr.get(1).getAsJsonArray().get(i).getAsDouble();
            double blood = arr.get(2).getAsJsonArray().get(i).getAsDouble();
            LatLng position = new LatLng(lat, lng);
            forts.add(new FortData(position, (int) blood));
        }
        return forts;
    }
}
