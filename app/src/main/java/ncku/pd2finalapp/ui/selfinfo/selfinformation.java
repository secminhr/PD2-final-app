package ncku.pd2finalapp.ui.selfinfo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import ncku.pd2finalapp.R;
import ncku.pd2finalapp.ui.map.MapActivity;
import ncku.pd2finalapp.ui.network.Network;
import ncku.pd2finalapp.ui.network.tasks.NetworkTask;

public class selfinformation extends AppCompatActivity {
    int levelfirst = 1;


    String name = "temp";
    String level = "temp";
    String faction = "temp";
    String nickname = "temp";
    String exp = "temp";
    String status = "none";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_upgrade);
        Network.getUserInfo()//這不是有執行到嘛 我可能平常試點這裏 所以他直接跳 因為他不是順序的 他是過一小段時間，東西抓到了裡面才會執行了解
                .setOnSuccessCallback(info -> {
                    name = info.getUsername();
                    level = Integer.toString(info.getLevel());
                    faction = info.getFaction();
                    nickname = info.getNickname();
                    exp = Integer.toString(info.getExp());
                    showall();


                    //do whatever you need, like showing the info, etc

                })
                .setOnFailureCallback(exception -> {
                    Log.e("name","erro");
                    // this should never happen because it's NoException
                    //but in case you want to know how to handle error
                })
                .execute();








    }
    public void upgrade(View v){
        int i = upupgrade.canupgrade();
        if(i==1){
            levelfirst++;
            showupgradefirst();
        }
        else {
           shownomoney();
        }
    }
    public void showname(){
        TextView text1 = findViewById(R.id.namestore);
        text1.setText(name);
    }
    public void shownickname(){
        TextView text1 = findViewById(R.id.nicknamestore);
        text1.setText(nickname);
    }
    public void showexp(){
        TextView text1 = findViewById(R.id.expstore);
        text1.setText(exp);
    }

    public void showstatus(){
        TextView text1 = findViewById(R.id.statusstore);
        text1.setText(status);
    }

    public void showfaction(){
        TextView text1 = findViewById(R.id.factionsotre);
        text1.setText(faction);
    }

    public void showlevel(){
        TextView text2 = findViewById(R.id.levelstore);
        text2.setText(level);

       // text2.setText((CharSequence) Network.getUserInfo(), TextView.BufferType.EDITABLE);
    }
    public void showupgradefirst(){
        TextView text3 = findViewById(R.id.valueoffirst);
        text3.setText(Integer.toString(levelfirst));
    }
    public void ChangeToMap(View v){

        Intent intent = new Intent();
        intent.setClass(selfinformation.this  , MapActivity.class);
        startActivity(intent);
    }
    private void shownomoney(){
        AlertDialog.Builder MyAlertDialog = new AlertDialog.Builder(this);
        MyAlertDialog.setTitle("No $");
        MyAlertDialog.setMessage("You don't have enough $");
        MyAlertDialog.show();

    }
    public void showall(){
        showname();
        showlevel();
        showupgradefirst();
        shownickname();
        showexp();
        showfaction();
        showstatus();

    }


}
