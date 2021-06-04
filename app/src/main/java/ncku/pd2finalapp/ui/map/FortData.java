package ncku.pd2finalapp.ui.map;

import com.google.android.gms.maps.model.LatLng;

public class FortData {

    private LatLng position;
    private int maxHp;
    private int hp;

    public FortData(LatLng position, int maxHp) {
        this.position = position;
        this.maxHp = maxHp;
        this.hp = maxHp;
    }

    public double getHpRatio() {
        return 1.0 * hp / maxHp;
    }

    public String getHpRepresentation() {
        return hp + "/" + maxHp;
    }

    public LatLng getFortPosition() {
        return position;
    }
}
