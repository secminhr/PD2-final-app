package ncku.pd2finalapp.ui.login;

import android.view.View;
import android.view.View.OnFocusChangeListener;

import com.google.android.material.textfield.TextInputLayout;

/**
 * A {@link OnFocusChangeListener} for {@link TextInputLayout} that will clear the error message when clicked.
 *
 * @author secminhr
 */
class ClearErrorOnFocus implements View.OnFocusChangeListener {
    private final TextInputLayout input;
    ClearErrorOnFocus(TextInputLayout input) {
        this.input = input;
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
            input.setError(null);
        }
    }
}
