package ncku.pd2finalapp.ui.network;

import java.net.URI;
import java.net.URISyntaxException;

import ncku.pd2finalapp.ui.info.UserInfo;

//Provide an interface to interact with networking part in a non-blocking way
public class Network {
    public static NetworkTask<Void, LoginTask.LoginFailedException> login(String username, String password) {
        return new LoginTask(username, password);
    }

    public static NetworkTask<Void, RegisterTask.UsernameExistsException> register(String username, String nickname, String password, String faction) {
        return new RegisterTask(username, nickname, password, faction);
    }

    public static NetworkTask<UserInfo, NoException> getUserInfo() {
        return new InfoTask();
    }

    public static WSClient createWebSocketConnection(String username) {
        String uri = "";
        try {
            URI server = new URI(uri);
            WSClient client = new WSClient(server, username);
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
