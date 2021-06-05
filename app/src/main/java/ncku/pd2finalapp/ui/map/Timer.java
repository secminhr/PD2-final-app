package ncku.pd2finalapp.ui.map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

class Timer extends CountDownTimer {

    private final TextView timer;
    private OnFinishListener onFinish = () -> {};

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

    public void setOnFinish(OnFinishListener onFinish) {
        this.onFinish = onFinish;
    }

    public void onTick(long millisUntilFinished) {
        timer.setText("Will be attacked after about " + millisUntilFinished / 1000 + " seconds");
    }

    public void onFinish() {
        onFinish.onFinish();
    }

    @FunctionalInterface
    interface OnFinishListener {
        void onFinish();
    }
}
