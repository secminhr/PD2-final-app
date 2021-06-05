package ncku.pd2finalapp.ui.login;

import android.content.Context;
import android.widget.FrameLayout;

import com.google.android.material.textfield.TextInputLayout;

import ncku.pd2finalapp.R;

class RegisterDialog extends FrameLayout {

    private final TextInputLayout[] inputs;

    RegisterDialog(Context context) {
        super(context);
        inflate(getContext(), R.layout.dialog_register, this);

        inputs = new TextInputLayout[] {
                findViewById(R.id.usernameInput),
                findViewById(R.id.nicknameInput),
                findViewById(R.id.passwordInput)
        };

        for (TextInputLayout input: inputs) {
            input.addOnEditTextAttachedListener(inputLayout ->
                input.getEditText().setOnFocusChangeListener(new ClearErrorOnFocus(input))
            );
        }
    }

    boolean hasNoEmptyField() {
        boolean hasEmpty = false;
        for (TextInputLayout input: inputs) {
            if (input.getEditText().getText().toString().isEmpty()) {
                hasEmpty = true;
                input.setError("Cannot be empty");
            }
        }
        return !hasEmpty;
    }

    void showErrorOnUsername(Exception e) {
        inputs[0].setError(e.getMessage());
    }

    String getUsername() {
        return inputs[0].getEditText().getText().toString();
    }
    String getNickname() {
        return inputs[1].getEditText().getText().toString();
    }
    String getPassword() {
        return inputs[2].getEditText().getText().toString();
    }

}