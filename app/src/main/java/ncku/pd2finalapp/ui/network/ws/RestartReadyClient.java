package ncku.pd2finalapp.ui.network.ws;

public class RestartReadyClient extends WSClient<Void> {
    public RestartReadyClient() {
       super("/renew");
    }

    @Override
    protected Void onReceiveString(String content) {
        return null;
    }
}
