package ncku.pd2finalapp.ui.login;

import android.content.Context;
import android.widget.FrameLayout;

import com.google.android.material.textfield.TextInputLayout;

import ncku.pd2finalapp.R;

class RegisterDialog extends FrameLayout {

    private final TextInputLayout username;
    private final TextInputLayout nickname;
    private final TextInputLayout password;
    private final TextInputLayout faction;
    private final TextInputLayout[] inputs;

    RegisterDialog(Context context) {
        super(context);
        inflate(getContext(), R.layout.dialog_register, this);

        username = findViewById(R.id.usernameInput);
        nickname = findViewById(R.id.nicknameInput);
        password = findViewById(R.id.passwordInput);
        faction = findViewById(R.id.factionInput);
        inputs = new TextInputLayout[] { username, nickname, password, faction };

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
        TextInputLayout usernameInput = findViewById(R.id.usernameInput);
        usernameInput.setError(e.getMessage());
    }

    String getUsername() {
        return username.getEditText().getText().toString();
    }
    String getNickname() {
        return nickname.getEditText().getText().toString();
    }
    String getPassword() {
        return password.getEditText().getText().toString();
    }
    String getFaction() {
        return faction.getEditText().getText().toString();
    }

}