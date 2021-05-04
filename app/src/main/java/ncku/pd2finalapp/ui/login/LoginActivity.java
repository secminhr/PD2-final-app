package ncku.pd2finalapp.ui.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;
import ncku.pd2finalapp.ui.map.MapActivity;
import ncku.pd2finalapp.R;
import ncku.pd2finalapp.ui.network.Network;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout usernameInput, passwordInput;
    public static final String USERNAME_EXTRA = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);

        EditText passwordEditText = findViewById(R.id.passwordEditText);
        EditText usernameEditText = findViewById(R.id.usernameEditText);
        usernameEditText.setOnFocusChangeListener(new ClearErrorOnFocus(usernameInput));
        passwordEditText.setOnFocusChangeListener(new ClearErrorOnFocus(passwordInput));
        passwordEditText.setOnEditorActionListener(this::OnPasswordDoneClicked);
    }

    public void onRegisterClicked(View registerButton) {
        RegisterDialog registerDialog = new RegisterDialog(this);
        AlertDialog alert = new AlertDialog.Builder(this)
                .setTitle("Register")
                .setView(registerDialog)
                .setPositiveButton("Register", (dialogInterface, i) -> {}) //empty for later override
                .setNegativeButton("Cancel", (dialogInterface, i) -> {})
                .show();

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(button -> {
            //we decide to override here to avoid immediate closing of alert after button clicked
            register(registerDialog, alert); //control of dismissing alert is hand out of register
        });
    }

    private void register(RegisterDialog dialog, AlertDialog alert) {
        if (dialog.hasNoEmptyField()) {
            Network.register(dialog.getUsername(), dialog.getNickname(), dialog.getPassword(), dialog.getFaction())
                    .setOnSuccessCallback(() -> onRegisterSuccess(dialog, alert))
                    .setOnFailureCallback(dialog::showErrorOnUsername)
                    .execute();
        }
    }

    private void onRegisterSuccess(RegisterDialog dialog, AlertDialog alert) {
        alert.dismiss();
        usernameInput.setError(null);
        passwordInput.setError(null);
        usernameInput.getEditText().setText(dialog.getUsername());
        passwordInput.getEditText().setText(dialog.getPassword());
        login();
    }

    public void onLoginClicked(View clickedButton) {
        login();
    }

    private boolean OnPasswordDoneClicked(TextView text, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            passwordInput.clearFocus();
            login();
        }
        return false;
    }

    private void login() {
        setLoginState(LoginState.LOGGING_IN);
        if (isEmpty(usernameInput, "username must not be empty") |
            isEmpty(passwordInput, "password must not be empty")) {
            setLoginState(LoginState.IDLE);
            return;
        }
        Network.login(getStringFromInput(usernameInput), getStringFromInput(passwordInput))
                .setOnSuccessCallback(this::onLoginSuccess)
                .setOnFailureCallback(this::onLoginFailed)
                .execute();
    }

    private void onLoginSuccess() {
        setLoginState(LoginState.IDLE);
        Intent openMapActivity = new Intent(this, MapActivity.class);
        openMapActivity.putExtra(USERNAME_EXTRA, getStringFromInput(usernameInput));
        startActivity(openMapActivity);
        finish();
    }

    private void onLoginFailed(Exception exception) {
        usernameInput.setError(exception.getMessage());
        passwordInput.setError(exception.getMessage());
        setLoginState(LoginState.IDLE);
    }

    private boolean isEmpty(TextInputLayout input, String messageWhenEmpty) {
        if (getStringFromInput(input).isEmpty()) {
            input.setError(messageWhenEmpty);
            return true;
        }
        return false;
    }

    private String getStringFromInput(TextInputLayout input) {
        return input.getEditText().getText().toString();
    }

    private void setLoginState(LoginState state) {
        state.updateViewWhenSet(findViewById(R.id.loginButton), findViewById(R.id.loginProgressBar));
    }
}