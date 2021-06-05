package ncku.pd2finalapp.ui.map;

import android.widget.LinearLayout;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import static android.widget.LinearLayout.LayoutParams.*;

public class FortData {

    private final LatLng position;
    private final int maxHp;
    private final int hp;
    private ArrayList<Timer> attackTimers = new ArrayList<>();

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
            timer.getTextView().setText("");
        });
        timer.start();
    }

    public void setList(LinearLayout list) {
        list.removeAllViews();
        for (Timer time: attackTimers) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            list.addView(time.getTextView(), layoutParams);
        }
    }
}
