package ncku.pd2finalapp.ui.login;

import com.google.android.material.textfield.TextInputLayout;

class TextFieldTool {
    static boolean isEmpty(TextInputLayout input, String messageWhenEmpty) {
        if (getStringFromInput(input).isEmpty()) {
            input.setError(messageWhenEmpty);
            return true;
        }
        return false;
    }

    static String getStringFromInput(TextInputLayout input) {
        return input.getEditText().getText().toString();
    }
}
