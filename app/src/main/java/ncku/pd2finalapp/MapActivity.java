package ncku.pd2finalapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MapActivity extends AppCompatActivity {

    private GoogleMap map;
    private boolean mapReady = false;
    private boolean viewReady = false;
    private void setMapReady(boolean value) {
        if (value == mapReady) {
            return;
        }
        mapReady = value;
        if (mapReady && viewReady) {
            moveCamera();
            markCurrent();
        }
    }
    private void setViewReady(boolean value) {
        if (value == viewReady) {
            return;
        }
        viewReady = value;
        if (mapReady && viewReady) {
            moveCamera();
            markCurrent();
        }
    }

    private final ActivityResultLauncher<String> permissionRequester =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
        if (granted) {
            markCurrent();
        } else {
            Toast.makeText(this, "Sorry, we can't calculate your position if the permission is not granted.", Toast.LENGTH_SHORT).show();
            finish();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(map -> {
            this.map = map;
            setMapReady(true);
        });
        mapFragment.getView().getViewTreeObserver().addOnGlobalLayoutListener(() -> setViewReady(true));
    }

    private void moveCamera() {
        LatLngBounds bounds = new LatLngBounds(new LatLng(22.993063582069528, 120.21391101450412), new LatLng(23.002434, 120.224757));
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private void markCurrent() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getSupportActionBar().setTitle("Loading position...");
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
            client.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(location -> {
                LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                map.addMarker(new MarkerOptions().position(current).title("Current position"));
                map.moveCamera(CameraUpdateFactory.newLatLng(current));
                getSupportActionBar().setTitle("PD2FinalApp");
            });
        } else {
            permissionRequester.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }
}