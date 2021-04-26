package ncku.pd2finalapp.ui.login;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

//A state for LoginActivity representing whether it's logging in or not
enum LoginState {
    LOGGING_IN {
        @Override
        void updateViewWhenSet(Button loginButton, ProgressBar progressBar) {
            loginButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    },
    IDLE {
        @Override
        void updateViewWhenSet(Button loginButton, ProgressBar progressBar) {
            progressBar.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        }
    };

    abstract void updateViewWhenSet(Button loginButton, ProgressBar progressBar);
}