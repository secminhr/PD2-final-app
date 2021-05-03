package ncku.pd2finalapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
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

import java.util.List;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MapActivity extends AppCompatActivity {

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
            map.getUiSettings().setMapToolbarEnabled(false);
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

                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.map_marker);
                Canvas canvas = new Canvas();
                Bitmap markerBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                canvas.setBitmap(markerBitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                drawable.draw(canvas);

                currentMarker = map.addMarker(new MarkerOptions().position(current).title("Current position").icon(BitmapDescriptorFactory.fromBitmap(markerBitmap)));
                map.moveCamera(CameraUpdateFactory.newLatLng(current));
                getSupportActionBar().setTitle("PD2FinalApp");
                PolylineOptions points = new PolylineOptions()
                                                .add(current)
                                                .color(getResources().getColor(R.color.purple_500, null))
                                                .width(25);
                walkedPath = map.addPolyline(points);
                currentLocated = true;
            });

            LocationRequest request = LocationRequest.create()
                                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                    .setInterval(5000);

            client.requestLocationUpdates(request, new LocationCallback() {
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

                @Override
                public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                    super.onLocationAvailability(locationAvailability);
                    //do nothing
                }
            }, getMainLooper());
        } else {
            permissionRequester.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }
}