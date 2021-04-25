package ncku.pd2finalapp.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;
import ncku.pd2finalapp.MapActivity;
import ncku.pd2finalapp.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout usernameInput, passwordInput;
    private final LoginRepo repo = LoginRepo.instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);

        EditText passwordEditText = findViewById(R.id.passwordEditText);
        EditText usernameEditText = findViewById(R.id.usernameEditText);
        usernameEditText.addTextChangedListener(new ClearErrorOnTextChange(usernameInput));
        passwordEditText.addTextChangedListener(new ClearErrorOnTextChange(passwordInput));
        passwordEditText.setOnEditorActionListener(this::OnPasswordDoneClicked);
    }

    private boolean OnPasswordDoneClicked(TextView text, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            login();
            return false;
        }
        return false;
    }

    public void onLoginClicked(View clickedButton) {
        login();
    }
    public void onRegisterClicked(View registerButton) {
        RegisterDialog registerDialog = new RegisterDialog(this);
       new AlertDialog.Builder(this)
                .setTitle("Register")
                .setView(registerDialog)
                .setCancelable(false)
                .setPositiveButton("Register", (dialogInterface, i) -> register(registerDialog))
                .setNegativeButton("Cancel", (dialogInterface, i) -> {})
                .show();
    }

    private void login() {
        setLoginState(LoginState.LOGGING_IN);
        if (isEmpty(usernameInput, "username must not be empty") |
            isEmpty(passwordInput, "password must not be empty")) {
            setLoginState(LoginState.IDLE);
            return;
        }
        repo.setOnFailureCallback((exception) -> {
            usernameInput.setError(exception.getMessage());
            passwordInput.setError(exception.getMessage());
            setLoginState(LoginState.IDLE);
        });
        repo.setOnSuccessCallback(this::loginSuccess);

        repo.login(usernameInput.getEditText().getText().toString(),
                   passwordInput.getEditText().getText().toString());
    }

    private boolean isEmpty(TextInputLayout input, String messageWhenEmpty) {
        String content = input.getEditText().getText().toString();
        if (content.isEmpty()) {
            input.setError(messageWhenEmpty);
            return true;
        }
        return false;
    }

    private void loginSuccess() {
        setLoginState(LoginState.IDLE);
        Intent openMapActivity = new Intent(this, MapActivity.class);
        startActivity(openMapActivity);
        finish();
    }

    private void register(RegisterDialog dialog) {

    }

    private void setLoginState(LoginState state) {
        state.updateViewWhenSet(findViewById(R.id.loginButton), findViewById(R.id.loginProgressBar));
    }

    private enum LoginState {
        LOGGING_IN {
            @Override
            void updateViewWhenSet(Button loginButton, ProgressBar progressBar) {
                loginButton.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
        },
        IDLE {
            @Override
            void updateViewWhenSet(Button loginButton, ProgressBar progressBar) {
                progressBar.setVisibility(View.INVISIBLE);
                loginButton.setVisibility(View.VISIBLE);
            }
        };

        abstract void updateViewWhenSet(Button loginButton, ProgressBar progressBar);
    }

}