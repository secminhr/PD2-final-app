package ncku.pd2finalapp.ui.map.model;

import android.widget.LinearLayout;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Objects;

import ncku.pd2finalapp.ui.map.tools.Timer;

public class FortData {

    private final LatLng position;
    private final int maxHp;
    private final int hp;
    private final ArrayList<Timer> attackTimers = new ArrayList<>();

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

    public void addTimer(Timer timer) {
        attackTimers.add(timer);
        timer.setOnFinish(() -> {
            attackTimers.remove(timer);
            timer.clearText();
        });
        timer.start();
    }

    public void setList(LinearLayout list) {
        list.removeAllViews();
        attackTimers.forEach(timer -> timer.addToList(list));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FortData fortData = (FortData) o;
        return Objects.equals(position, fortData.position);
    }
}
