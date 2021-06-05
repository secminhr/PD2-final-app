package ncku.pd2finalapp.ui.network.ws;

import java.net.URI;

import ncku.pd2finalapp.ui.map.FortData;

public class FortBloodUpdateClient extends WSClient<FortBloodUpdateClient.BloodUpdate> {
    public FortBloodUpdateClient() {
        super(URI.create(root + "/websocket/updateBlood"));
//        super(URI.create("wss://echo.websocket.org"));
    }

    @Override
    protected BloodUpdate onReceiveString(String content) {
        return BloodUpdate.parse(content);
    }


    public static class BloodUpdate {
        static BloodUpdate parse(String string) {
            return new BloodUpdate();
        }

        public void update(FortData fort) {

        }
    }
}
