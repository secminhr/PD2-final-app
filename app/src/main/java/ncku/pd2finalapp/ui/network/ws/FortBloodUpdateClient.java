package ncku.pd2finalapp.ui.network.ws;

import ncku.pd2finalapp.ui.map.model.FortData;

public class FortBloodUpdateClient extends WSClient<FortBloodUpdateClient.BloodUpdate> {
    public FortBloodUpdateClient() {
        super("/updateBlood");
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
