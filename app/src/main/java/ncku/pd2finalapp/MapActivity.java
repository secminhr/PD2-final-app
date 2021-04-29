package ncku.pd2finalapp;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.appcompat.app.AppCompatActivity;

public class MapActivity extends AppCompatActivity {

    private GoogleMap map;
    private boolean mapReady = false;
    private boolean viewReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(map -> {
            this.map = map;
            LatLng ncku = new LatLng(22.99915621444473, 120.21689980736697);
            map.addMarker(new MarkerOptions().position(ncku).title("NCKU"));
            mapReady = true;
            tryMoveCamera();
        });

        mapFragment.getView().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            viewReady = true;
            tryMoveCamera();
        });
    }

    private void tryMoveCamera() {
        if (viewReady && mapReady) {
            LatLngBounds bounds = new LatLngBounds(new LatLng(22.993063582069528, 120.21391101450412), new LatLng(23.002434, 120.224757));
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        }
    }
}