package ncku.pd2finalapp.ui.selfinfo;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import ncku.pd2finalapp.R;
import ncku.pd2finalapp.ui.network.Network;

public class selfinformation extends AppCompatActivity {
    String name = "temp";
    String nickname = "temp";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_upgrade);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Network.getUserInfo()
                .setOnSuccessCallback(info -> {
                    name = info.getUsername();
                    nickname = info.getNickname();
                    showall();
                })
                .execute();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) { //back button
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showname(){
        TextView text1 = findViewById(R.id.namestore);
        text1.setText(name);
    }
    public void shownickname(){
        TextView text1 = findViewById(R.id.nicknamestore);
        text1.setText(nickname);
    }

    public void showall(){
        showname();
        shownickname();
    }
}
