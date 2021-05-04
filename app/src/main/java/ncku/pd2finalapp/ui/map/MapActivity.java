package ncku.pd2finalapp.ui.map;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import ncku.pd2finalapp.R;

public class MapActivity extends AppCompatActivity implements OnSuccessListener<Location> {

    private GoogleMap map;
    private Polyline walkedPath;
    private Marker currentMarker;
    private boolean mapReady = false;
    private boolean viewReady = false;
    private boolean currentLocated = false;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(map -> {
            this.map = map;
            map.getUiSettings().setMapToolbarEnabled(false);
            setMapReady(true);
        });
        mapFragment.getView().getViewTreeObserver().addOnGlobalLayoutListener(() -> setViewReady(true));
    }

    private void moveCamera() {
        //move camera to fit the campus on screen
        LatLngBounds bounds = new LatLngBounds(new LatLng(22.993063582069528, 120.21391101450412), new LatLng(23.002434, 120.224757));
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private final LocationPermissionHelper permissionHelper = new LocationPermissionHelper(
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) {
                markCurrent();
            } else {
                Toast.makeText(this, "Sorry, we can't calculate your position if the permission is not granted.", Toast.LENGTH_SHORT).show();
                finish();
            }
        })
    );

    @SuppressLint("MissingPermission")
    private void markCurrent() {
        if (permissionHelper.hasPermission(this)) {
            getSupportActionBar().setTitle("Loading position...");
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
            client.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(this);
        } else {
            permissionHelper.requestPermission();
        }
    }

    //On successfully retrieved user's current location first time
    @Override
    public void onSuccess(Location location) {
        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
        Bitmap markerBitmap = getMarkerBitmap();
        currentMarker = map.addMarker(
                new MarkerOptions()
                        .position(current)
                        .title("Current position")
                        .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
        );
        map.moveCamera(CameraUpdateFactory.newLatLng(current));
        getSupportActionBar().setTitle("PD2FinalApp");
        PolylineOptions points = new PolylineOptions()
                .add(current)
                .color(getResources().getColor(R.color.purple_500, null))
                .width(25);
        walkedPath = map.addPolyline(points);
        currentLocated = true;
        requestLocationUpdate();
    }

    private Bitmap getMarkerBitmap() {
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.map_marker);
        Canvas canvas = new Canvas();
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap markerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(markerBitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return markerBitmap;
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdate() {
        if (permissionHelper.hasPermission(this)) {
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
            LocationRequest request = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000);
            client.requestLocationUpdates(request, new CurrentLocationCallback(), getMainLooper());
        } else {
            permissionHelper.requestPermission();
        }
    }

    class CurrentLocationCallback extends LocationCallback {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (currentLocated) {
                Location last = locationResult.getLastLocation();
                LatLng newPoint = new LatLng(last.getLatitude(), last.getLongitude());
                List<LatLng> points = walkedPath.getPoints();
                points.add(newPoint);
                walkedPath.setPoints(points);
                currentMarker.setPosition(newPoint);
            }
        }
    }
}