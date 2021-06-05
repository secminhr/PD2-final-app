package ncku.pd2finalapp.ui.login;

import android.view.View;

import ncku.pd2finalapp.databinding.ActivityLoginBinding;

//A state for LoginActivity representing whether it's logging in or not
enum LoginState {
    LOGGING_IN {
        @Override
        void updateView(ActivityLoginBinding binding) {
            binding.loginButton.setVisibility(View.GONE);
            binding.loginProgressBar.setVisibility(View.VISIBLE);
        }
    },
    IDLE {
        @Override
        void updateView(ActivityLoginBinding binding) {
            binding.loginProgressBar.setVisibility(View.GONE);
            binding.loginButton.setVisibility(View.VISIBLE);
        }
    };

    abstract void updateView(ActivityLoginBinding binding);
}