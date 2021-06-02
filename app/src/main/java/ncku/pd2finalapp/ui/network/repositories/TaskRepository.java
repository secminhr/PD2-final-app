package ncku.pd2finalapp.ui.network.repositories;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

//Provide a abstraction of the repository, can further separate ui and data access.
public interface TaskRepository {
    String infoCheckData();
    String loginCheckData(String username, String password);
    String registerCheckData(String username, String nickname, String password);
    void sendAttackData(List<LatLng> points, long duration, LatLng target);

    TaskRepository current = new RealRepo();


}
