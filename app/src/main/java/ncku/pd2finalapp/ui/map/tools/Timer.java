package ncku.pd2finalapp.ui.map.tools;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ncku.pd2finalapp.ui.map.Block;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class Timer extends CountDownTimer {

    private final TextView timer;
    private Block onFinish = () -> {};

    public Timer(long millisInFuture, long countDownInterval, Context context) {
        super(millisInFuture, countDownInterval);
        timer = new TextView(context);
        timer.setTextColor(Color.WHITE);
        timer.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        timer.setTextSize(16);
        timer.setTypeface(null, Typeface.BOLD);
    }

    public void clearText() {
        timer.setText("");
    }

    public void addToList(LinearLayout layout) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        layout.addView(timer, layoutParams);
    }

    public void onTick(long millisUntilFinished) {
        long minutes = millisUntilFinished / 1000 / 60;
        timer.setText(minutes < 1 ?
                        "Attack will arrive under a minute." :
                        "Attack will arrive in about " + minutes + " minutes."
        );
    }

    public void setOnFinish(Block onFinish) {
        this.onFinish = onFinish;
    }
    public void onFinish() {
        onFinish.execute();
    }
}
