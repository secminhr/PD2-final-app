package ncku.pd2finalapp.ui.network.ws;

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
import java.util.function.Consumer;

import ncku.pd2finalapp.ReceiveAndSend.ReceiveInfoFromBack;

public abstract class WSClient<Message> {
    private final URI uri;
    private Client client;
    private Consumer<Message> onMessageListener = (message) -> {};
    protected static String root = "ws://cryptic-island-19755.herokuapp.com/websocket";

    protected WSClient(String endpoint) {
        uri = URI.create(root + endpoint);
        client = new Client(uri);
    }

    private void onWebSocketClose(boolean remote) {
        if (remote) { //connection somehow closed by remote, reconnect
            reconnect(uri);
        }
    }

    private void reconnect(URI uri) {
        client = new Client(uri);
        client.connect();
    }

    public boolean connectBlocking() throws InterruptedException {
        return client.connectBlocking();
    }

    public void close() {
        client.close();
    }

    protected abstract Message onReceiveString(String content);
    public WSClient<Message> setOnReceiveMessageListener(Consumer<Message> listener) {
        onMessageListener = listener;
        return this;
    }

    protected class Client extends WebSocketClient {

        private Client(URI uri) {
            super(uri, new HashMap<String, String>() {{
                try {
                    Map<String, List<String>> cookies = new HashMap<>();
                    cookies = CookieHandler.getDefault().get(new URI(ReceiveInfoFromBack.network), cookies);

                    this.put("Cookie", String.join("; ", cookies.get("Cookie")));
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
            Message m = onReceiveString(message);
            onMessageListener.accept(m);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.e("WSClient", "onClose");
            Log.e("WSClient", reason);
            onWebSocketClose(remote);
        }

        @Override
        public void onError(Exception ex) {
            Log.e("WSClient", "onError");
            ex.printStackTrace();
        }
    }
}
