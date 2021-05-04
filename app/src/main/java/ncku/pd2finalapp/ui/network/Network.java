package ncku.pd2finalapp.ui.network;

import java.net.URI;
import java.net.URISyntaxException;

//Provide an interface to interact with networking part in a non-blocking way
public class Network {
    public static NetworkTask<LoginTask.LoginFailedException> login(String username, String password) {
        return new LoginTask(username, password);
    }

    public static NetworkTask<RegisterTask.UsernameExistsException> register(String username, String nickname, String password, String faction) {
        return new RegisterTask(username, nickname, password, faction);
    }

    public static WSClient createWebSocketConnection() {
        String uri = "";
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
