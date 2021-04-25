package ncku.pd2finalapp.login;

import android.content.Context;
import android.widget.FrameLayout;

import ncku.pd2finalapp.R;

public class RegisterDialog extends FrameLayout {
    public RegisterDialog(Context context) {
        super(context);
        inflate(getContext(), R.layout.dialog_register, this);
    }

    boolean hasNoEmptyField() {
        return false;
    }

}