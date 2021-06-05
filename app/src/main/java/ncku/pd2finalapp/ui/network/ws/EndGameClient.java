package ncku.pd2finalapp.ui.network.ws;

public class EndGameClient extends WSClient<Void> {

    public EndGameClient() {
        super("/checkgame");
    }

    @Override
    protected Void onReceiveString(String content) {
        return null;
    }
}
