package ncku.pd2finalapp.ui.selfinfo;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import ncku.pd2finalapp.R;
import ncku.pd2finalapp.ui.network.Network;

public class selfinformation extends AppCompatActivity {
    String name = "temp";
    String level = "temp";
    String nickname = "temp";
    String exp = "temp";
    String status = "none";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_upgrade);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Network.getUserInfo()
                .setOnSuccessCallback(info -> {
                    name = info.getUsername();
                    level = Integer.toString(info.getLevel());
                    nickname = info.getNickname();
                    exp = Integer.toString(info.getExp());
                    showall();

                })
                .setOnFailureCallback(exception -> {
                    Log.e("name","erro");
                    // this should never happen because it's NoException
                    //but in case you want to know how to handle error
                })
                .execute();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
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

    public void showstatus(){
        TextView text1 = findViewById(R.id.statusstore);
        text1.setText(status);
    }

    public void ChangeToMap(View v){
        finish();
    }

    public void showall(){
        showname();
        shownickname();
        showstatus();
    }
}
