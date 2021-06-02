package ncku.pd2finalapp.ui.network.repositories;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

//A mock repo that always return success data.
class MockSuccessRepo implements TaskRepository {
    @Override
    public String infoCheckData() {
        return "{\n" +
                "    \"username\": \"sssss\",\n" +
                "    \"status\": {\n" +
                "        \"exp\": 0,\n" +
                "        \"level\": 0,\n" +
                "        \"nickname\": \"s1s\",\n" +
                "        \"faction\": \"A\"\n" +
                "    }\n" +
                "}";
    }

    @Override
    public String loginCheckData(String username, String password) {
        return "{\n" +
                "    \"success\": true,\n" +
                "    \"info\": \"/info\"\n" +
                "}";
    }

    @Override
    public String registerCheckData(String username, String nickname, String password) {
        return "{\n" +
                "  \"success\": true,\n" +
                "  \"auth\": \"/login\"\n" +
                "}";
    }

    @Override
    public void sendAttackData(List<LatLng> points, long duration, LatLng target) {
        return;
    }
}
