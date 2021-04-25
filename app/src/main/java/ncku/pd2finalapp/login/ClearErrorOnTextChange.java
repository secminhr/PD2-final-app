package ncku.pd2finalapp.login;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

/**
 * A {@link TextWatcher} for {@link TextInputLayout} that will clear the error message when text is editing.
 *
 * @author secminhr
 */
class ClearErrorOnTextChange implements TextWatcher {
    private final TextInputLayout input;
    public ClearErrorOnTextChange(TextInputLayout input) {
        this.input = input;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //no action
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        input.setError(null);
    }

    @Override
    public void afterTextChanged(Editable editable) {
        //no action
    }
}
