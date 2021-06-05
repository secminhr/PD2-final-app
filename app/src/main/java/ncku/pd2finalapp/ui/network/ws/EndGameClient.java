package ncku.pd2finalapp.ui.network.ws;

import java.net.URI;

public class EndGameClient extends WSClient<Void> {

    public EndGameClient() {
        super(URI.create(root + "/websocket/checkgame"));
//        super(URI.create("wss://echo.websocket.org"));
    }

    @Override
    protected Void onReceiveString(String content) {
        return null;
    }
}
