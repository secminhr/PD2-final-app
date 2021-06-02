package ncku.pd2finalapp.ui.network.tasks;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import ncku.pd2finalapp.ui.network.repositories.TaskRepository;

public class SendAttackTask extends NetworkTask<Void, NoException> {

    private List<LatLng> points;
    private long duration;
    private LatLng target;
    public SendAttackTask(List<LatLng> points, long durationInMinute, LatLng target) {
        this.points = points;
        this.duration = durationInMinute;
        this.target = target;
    }

    @Override
    protected void task() {
        TaskRepository.current.sendAttackData(points, duration, target);
        //This will just success since login is required to reach here
        onSuccess(null);
    }
}
