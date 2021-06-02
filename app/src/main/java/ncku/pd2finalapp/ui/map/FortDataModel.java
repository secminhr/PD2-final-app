package ncku.pd2finalapp.ui.map;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class FortDataModel {

    private List<LatLng> positions;

    public String toSendingForm() {
        return "";
    }

    public static FortDataModel fromString(String data) throws IllegalArgumentException {
        return new FortDataModel();
    }

    public List<LatLng> getFortPositions() {
//        return positions;
        return new ArrayList<LatLng>() {{
            this.add(new LatLng(23.00001987612374, 120.22004864131343));
            this.add(new LatLng(23.00248883954706, 120.21774194156964));
            this.add(new LatLng(22.995059188124603, 120.22279061577365));
        }};
    }
}
