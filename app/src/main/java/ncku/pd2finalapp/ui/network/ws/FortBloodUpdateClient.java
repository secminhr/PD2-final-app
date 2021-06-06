package ncku.pd2finalapp.ui.network.ws;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import ncku.pd2finalapp.ui.map.model.FortData;

public class FortBloodUpdateClient extends WSClient<FortBloodUpdateClient.BloodUpdate> {
    public FortBloodUpdateClient() {
        super("/updateBlood");
    }

    @Override
    protected BloodUpdate onReceiveString(String content) {
        return BloodUpdate.parse(content);
    }


    public static class BloodUpdate {
        private double lat;
        private double lng;
        private int hp;

        static BloodUpdate parse(String string) {
            Gson gson = new Gson();
            return gson.fromJson(string, BloodUpdate.class);
        }

        public void update(FortData fort) {
            LatLng updateTarget = new LatLng(lat, lng);
            if (updateTarget.equals(fort.getFortPosition())) {
                fort.setHp(hp);
            }
        }
    }
}
