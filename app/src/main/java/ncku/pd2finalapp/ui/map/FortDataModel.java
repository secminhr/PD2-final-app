package ncku.pd2finalapp.ui.map;

import com.google.android.gms.maps.model.LatLng;

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
        return positions;
//        return new ArrayList<LatLng>() {{
//            //this.add(new LatLng())
//        }};
    }
}
