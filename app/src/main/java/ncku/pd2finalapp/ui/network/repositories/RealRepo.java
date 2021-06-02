package ncku.pd2finalapp.ui.network.repositories;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import ncku.pd2finalapp.ReceiveAndSend.AttackCheck;
import ncku.pd2finalapp.ReceiveAndSend.InfoCheck;
import ncku.pd2finalapp.ReceiveAndSend.LoginCheck;
import ncku.pd2finalapp.ReceiveAndSend.RegisterCheck;

//Repo that connect to a server.
class RealRepo implements TaskRepository {
    @Override
    public String infoCheckData() {
        return new InfoCheck().InfoCheckData();
    }

    @Override
    public String loginCheckData(String username, String password) {
        return new LoginCheck().LoginCheckData(username, password);
    }

    @Override
    public String registerCheckData(String username, String nickname, String password) {
        return new RegisterCheck().RegisterCheckData(username, nickname, password);
    }

    @Override
    public void sendAttackData(List<LatLng> points, long duration, LatLng target) {
        new AttackCheck().sendAttackData(points, duration, target);
    }
}
