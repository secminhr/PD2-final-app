package ncku.pd2finalapp.ui.network;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ncku.pd2finalapp.ReceiveAndSend.ReceiveInfoFromBack;

public class WSClient {

    private Client client;
    public WSClient(URI uri) {

        client = new Client(uri);
    }

    public boolean connectBlocking() throws InterruptedException {
        return client.connectBlocking();
    }

    public void close() {
        client.close();
    }

    private static class Client extends WebSocketClient {
        private Client(URI uri) {
            super(uri, new HashMap<String, String>() {{
                try {
                    Map<String, List<String>> cookies = new HashMap<>();
                    cookies = CookieHandler.getDefault().get(new URI(ReceiveInfoFromBack.network), cookies);

                    this.put("Cookie", cookies.get("Cookie").stream().collect(Collectors.joining("; ")));
                } catch (IOException|URISyntaxException e) {
                    e.printStackTrace();
                }
            }});
        }

        //all left empty since format is not decided
        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.e("WSClient", "onOpen");
        }

        @Override
        public void onMessage(String message) {
            Log.e("WSClient", "onMessage");
            Log.e("WSClient", message);
            //TODO: deal with message
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.e("WSClient", "onClose");
            Log.e("WSClient", reason);
        }

        @Override
        public void onError(Exception ex) {
            Log.e("WSClient", "onError");
            ex.printStackTrace();
        }
    }
}
