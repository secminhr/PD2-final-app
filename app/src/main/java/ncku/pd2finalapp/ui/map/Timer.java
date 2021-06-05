package ncku.pd2finalapp.ui.map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

class Timer extends CountDownTimer {

    private final TextView timer;
    private Block onFinish = () -> {};

    Timer(long millisInFuture, long countDownInterval, Context context) {
        super(millisInFuture, countDownInterval);
        timer = new TextView(context);
        timer.setTextColor(Color.WHITE);
        timer.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        timer.setTextSize(16);
        timer.setTypeface(null, Typeface.BOLD);
    }

    public TextView getTextView() {
        return timer;
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
