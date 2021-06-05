package ncku.pd2finalapp.ui.network.ws;

import java.net.URI;

public class RestartReadyClient extends WSClient<Void> {
    public RestartReadyClient() {
       super(URI.create(root + "/websocket/renew"));
//        super(URI.create("wss://echo.websocket.org"));
    }

    @Override
    protected Void onReceiveString(String content) {
        return null;
    }
}
