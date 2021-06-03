package ncku.pd2finalapp.ui.network;

import com.google.android.gms.maps.model.LatLng;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import ncku.pd2finalapp.ui.info.UserInfo;
import ncku.pd2finalapp.ui.map.FortData;
import ncku.pd2finalapp.ui.network.tasks.GetFortDataTask;
import ncku.pd2finalapp.ui.network.tasks.InfoTask;
import ncku.pd2finalapp.ui.network.tasks.LoginTask;
import ncku.pd2finalapp.ui.network.tasks.NetworkTask;
import ncku.pd2finalapp.ui.network.tasks.NoException;
import ncku.pd2finalapp.ui.network.tasks.RegisterTask;
import ncku.pd2finalapp.ui.network.tasks.SendAttackTask;

//Provide an interface to interact with networking part in a non-blocking way
public class Network {
    public static NetworkTask<Void, LoginTask.LoginFailedException> login(String username, String password) {
        return new LoginTask(username, password);
    }
    public static NetworkTask<Void, RegisterTask.UsernameExistsException> register(String username, String nickname, String password) {
        return new RegisterTask(username, nickname, password);
    }
    public static NetworkTask<UserInfo, NoException> getUserInfo() {
        return new InfoTask();
    }
    public static NetworkTask<Void, NoException> sendAttack(List<LatLng> points,long durationInMinute, LatLng target) {
        return new SendAttackTask(points, durationInMinute, target);
    }
    public static NetworkTask<List<FortData>, NoException> getFortsData() {
        return new GetFortDataTask();
    }

    public static WSClient createWebSocketConnection() {
        String uri = "ws://popular-sloth-16.loca.lt/websocket";
        try {
            URI server = new URI(uri);
            WSClient client = new WSClient(server);
            client.connectBlocking();
            return client;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
