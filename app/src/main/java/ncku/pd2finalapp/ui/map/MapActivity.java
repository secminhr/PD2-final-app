package ncku.pd2finalapp.ui.map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
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
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import ncku.pd2finalapp.R;
import ncku.pd2finalapp.ui.login.LoginActivity;
import ncku.pd2finalapp.ui.network.Network;
import ncku.pd2finalapp.ui.network.WSClient;
import ncku.pd2finalapp.ui.selfinfo.selfinformation;

public class MapActivity extends AppCompatActivity implements OnSuccessListener<Location> {

    private GoogleMap map;
    private Polyline walkedPath;
    private Marker currentMarker;
    private WSClient locationAsyncClient;
    private String username;
    private boolean isRecording = false;
    private void setRecording(boolean value) {
        isRecording = value;
        ExtendedFloatingActionButton button = findViewById(R.id.fab);
        if (isRecording) {
            button.setOnClickListener(this::onStopRecordingClicked);
        } else {
            button.setOnClickListener(this::onStartRecordingClicked);
        }
    }

    private LocalTime startRecordingTime = null;

    private final MapState mapState = new MapState().onMapViewReady(() -> {
        moveCamera();
        markCurrent();
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        username = getIntent().getStringExtra(LoginActivity.USERNAME_EXTRA);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(map -> {
            this.map = map;
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
            map.getUiSettings().setMapToolbarEnabled(false);
            mapState.setMapReady();
        });
        mapFragment.getView().getViewTreeObserver().addOnGlobalLayoutListener(mapState::setViewRendered);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        menu.getItem(0)
                .setOnMenuItemClickListener(menuItem -> {
                    ChangeToInfo();
                    return true;
                });
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationAsyncClient != null) {
            locationAsyncClient.close();
        }
    }

    private void moveCamera() {
        //move camera to fit the campus on screen
        LatLngBounds bounds = new LatLngBounds(new LatLng(22.993063582069528, 120.21391101450412), new LatLng(23.002434, 120.224757));
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private final LocationPermissionHelper permissionHelper = new LocationPermissionHelper(this).onUserDeny(() -> {
        //when user deny the permission request
        Toast.makeText(this, "Sorry, we can't calculate your position if the permission is not granted.", Toast.LENGTH_SHORT).show();
        finish();
    });

    @SuppressLint("MissingPermission")
    private void markCurrent() {
        permissionHelper.executeWithPermission(this, () -> {
            getSupportActionBar().setTitle("Loading position...");
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
            client.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(this);
        });
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
        map.animateCamera(CameraUpdateFactory.newLatLng(current));
        getSupportActionBar().setTitle("PD2FinalApp");
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

    public void onStartRecordingClicked(View v) {
        ExtendedFloatingActionButton button = (ExtendedFloatingActionButton) v;
        button.setText("Connecting...");
        button.setIconResource(R.drawable.ic_baseline_connecting_24);
        LatLng current = currentMarker.getPosition();
        locationAsyncClient = Network.createWebSocketConnection(username);

        button.setIconResource(R.drawable.ic_baseline_stop_24);
        button.shrink();
        if (locationAsyncClient != null) {
            locationAsyncClient.send(current);
        }

        PolylineOptions firstPoint = new PolylineOptions()
                .add(current)
                .color(getResources().getColor(R.color.white, null))
                .startCap(new RoundCap())
                .endCap(new RoundCap())
                .jointType(JointType.ROUND)
                .width(25);

        walkedPath = map.addPolyline(firstPoint);
        setRecording(true);
        startRecordingTime = LocalTime.now();
    }

    public void onStopRecordingClicked(View v) {
        ExtendedFloatingActionButton button = (ExtendedFloatingActionButton) v;
        button.setText("Start Recording");
        button.setIconResource(R.drawable.ic_baseline_record_24);
        button.extend();
        if (locationAsyncClient != null) {
            locationAsyncClient.close();
            locationAsyncClient = null;
        }
        setRecording(false);
        long minutes = Duration.between(startRecordingTime, LocalTime.now()).toMinutes();
        startRecordingTime = null;
        //TODO: fix the target
        Network.sendAttack(walkedPath.getPoints(), minutes, walkedPath.getPoints().get(0)).execute();
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdate() {
        permissionHelper.executeWithPermission(this, () -> {
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
            LocationRequest request = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000);
            client.requestLocationUpdates(request, new CurrentLocationCallback(), getMainLooper());
        });
    }

    private class CurrentLocationCallback extends LocationCallback {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            Location last = locationResult.getLastLocation();
            LatLng newPoint = new LatLng(last.getLatitude(), last.getLongitude());
            currentMarker.setPosition(newPoint);

            if (isRecording) {
                List<LatLng> points = walkedPath.getPoints();
                points.add(newPoint);
                if (locationAsyncClient != null) {
                    locationAsyncClient.send(newPoint);
                }
                walkedPath.setPoints(points);
            }
        }
    }
    public void ChangeToInfo(){
        Intent intent = new Intent();
        intent.setClass(MapActivity.this, selfinformation.class);
        startActivity(intent);
    }
}