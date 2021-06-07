package ncku.pd2finalapp.ui.map.statecontrolling;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import androidx.core.content.ContextCompat;
import ncku.pd2finalapp.R;
import ncku.pd2finalapp.databinding.ActivityMapBinding;

public enum UserState {
    NotRecording {
        @Override
        void updateView(UserState previous, ActivityMapBinding binding) {
            if (previous == Attacking || previous == SendingAttack) {
                ExtendedFloatingActionButton fab = binding.fab;
                fab.setIconTint(ColorStateList.valueOf(Color.WHITE));
                fab.setBackgroundColor(Color.BLACK);
                fab.setIcon(ContextCompat.getDrawable(binding.fab.getContext(), R.drawable.sword));
                fab.setTextColor(Color.WHITE);
                fab.extend();

                Button attackButton = binding.startAttackButton;
                attackButton.setEnabled(true);
                attackButton.setText("Start Attack");
                attackButton.setVisibility(View.VISIBLE);

                binding.closerMessageTextView.setVisibility(View.GONE);
                binding.alreadyTakenDownMessage.setVisibility(View.GONE);
                binding.attackProgressBar.setVisibility(View.GONE);
                binding.attackProgressBar.setVisibility(View.GONE);
            }
        }
    },
    Attacking {
        @Override
        void updateView(UserState previous, ActivityMapBinding binding) {
            ExtendedFloatingActionButton fab = binding.fab;
            fab.setIcon(ContextCompat.getDrawable(binding.fab.getContext(), R.drawable.ic_baseline_cancel_24));
            fab.setTextColor(Color.BLACK);
            fab.setIconTint(ColorStateList.valueOf(Color.BLACK));
            fab.setBackgroundColor(Color.WHITE);
            fab.shrink();
        }
    },
    SendingAttack {
        @Override
        void updateView(UserState previous, ActivityMapBinding binding) {
            binding.startAttackButton.setVisibility(View.GONE);
            binding.attackProgressBar.setVisibility(View.VISIBLE);
        }
    };

    abstract void updateView(UserState previous, ActivityMapBinding binding);

    public static UserState current = NotRecording;
    public static void setState(UserState state, ActivityMapBinding binding) {
        state.updateView(current, binding);
        current = state;
    }
}
