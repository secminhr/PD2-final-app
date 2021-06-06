package ncku.pd2finalapp.ui.map.model;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

public class AttackParameter {
    private Polyline walkedPath = null;
    private LocalTime startRecordingTime = null;
    private Duration duration = null;

    public void startRecord(GoogleMap map, PolylineOptions first) {
        walkedPath = map.addPolyline(first);
        startRecordingTime = LocalTime.now();
    }

    public List<LatLng> getPath() {
        return walkedPath.getPoints();
    }

    public void stopRecord() {
        duration = Duration.between(startRecordingTime, LocalTime.now());
    }

    public long durationInMinutes() {
        long millis = duration.toMillis();
        return millis / 1000 / 60;
    }

    public long durationInMillis() {
        return duration.toMillis();
    }

    public void clear() {
        walkedPath.remove();
        startRecordingTime = null;
        duration = null;
    }

    public void addPosition(LatLng position) {
        List<LatLng> points = walkedPath.getPoints();
        points.add(position);
        walkedPath.setPoints(points);
    }

    public LatLng lastPosition() {
        return walkedPath.getPoints().get(walkedPath.getPoints().size()-1);
    }
}

