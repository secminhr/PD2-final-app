package ncku.pd2finalapp.ui.network;

import com.google.android.gms.maps.model.LatLng;

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
import ncku.pd2finalapp.ui.network.ws.EndGameClient;
import ncku.pd2finalapp.ui.network.ws.FortBloodUpdateClient;
import ncku.pd2finalapp.ui.network.ws.RestartReadyClient;
import ncku.pd2finalapp.ui.network.ws.WSClient;

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

    public static WSClient<FortBloodUpdateClient.BloodUpdate> bloodUpdateClient() {
        return tryConnect(new FortBloodUpdateClient());
    }

    public static WSClient<Void> endGameClient() {
        return tryConnect(new EndGameClient());
    }

    public static WSClient<Void> restartClient() {
        return tryConnect(new RestartReadyClient());
    }

    private static<T> WSClient<T> tryConnect(WSClient<T> client) {
        try {
            client.connectBlocking();
            return client;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
