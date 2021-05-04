package ncku.pd2finalapp.ui.network;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WSClient {

    private Client client;
    private String username;
    public WSClient(URI uri, String username) {
        client = new Client(uri);
        this.username = username;
    }

    public void send(LatLng latLng) {
        if (client.isOpen()) {
            client.send(toWSFormat(latLng));
        }
    }

    private String toWSFormat(LatLng latlng) {
        return "{\"id\":" + username + ",\"longitude\":" + latlng.longitude + ",\"latitude\":" + latlng.latitude + "}";
    }

    public boolean connectBlocking() throws InterruptedException {
        return client.connectBlocking();
    }

    public void close() {
        client.close();
    }

    private static class Client extends WebSocketClient {
        private Client(URI uri) {
            super(uri);
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
