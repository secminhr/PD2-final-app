package ncku.pd2finalapp.ui.map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.CircleOptions;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import ncku.pd2finalapp.R;
import ncku.pd2finalapp.ui.network.Network;
import ncku.pd2finalapp.ui.network.ws.FortBloodUpdateClient;
import ncku.pd2finalapp.ui.network.ws.WSClient;
import ncku.pd2finalapp.ui.selfinfo.selfinformation;

import static ncku.pd2finalapp.ui.map.MarkerTool.getFortBitmap;
import static ncku.pd2finalapp.ui.map.MarkerTool.getFortBitmapDescriptor;
import static ncku.pd2finalapp.ui.map.MarkerTool.getMarkerBitmap;

public class MapActivity extends AppCompatActivity implements OnSuccessListener<Location> {

    private GoogleMap map;
    private Polyline walkedPath;
    private Marker currentMarker;
    private ArrayList<Marker> fortMarkers = new ArrayList<>();

    private BottomSheetBehavior<ConstraintLayout> bottomSheet;

    //note: there will be 3
    private WSClient<FortBloodUpdateClient.BloodUpdate> fortBloodChangeClient = null;
    private WSClient<Void> gameEndClient = null;
    private WSClient<Void> readyForRestartClient = null;

    private boolean isRecording = false;
    private LocalTime startRecordingTime = null;

    private final MapState mapState = new MapState().onMapViewReady(() -> {
        moveCamera();
        fetchAndMarkForts();
        markCurrent();
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);

        mapFragment.getMapAsync(map -> {
            this.map = map;
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
            map.getUiSettings().setMapToolbarEnabled(false);
            map.setOnMarkerClickListener(this::onMarkerClick);
            mapState.setMapReady();
        });
        mapFragment.getView().getViewTreeObserver().addOnGlobalLayoutListener(mapState::setViewRendered);

        ConstraintLayout sheet = findViewById(R.id.bottomSheet);
        bottomSheet = BottomSheetBehavior.from(sheet);
        bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);

        fortBloodChangeClient = Network.bloodUpdateClient().setOnReceiveMessageListener((update) -> {
            for(Marker fortMarker: fortMarkers) {
                update.update((FortData) fortMarker.getTag());
            }
            markForts(
                fortMarkers.stream()
                        .map((marker) -> (FortData) marker.getTag())
                        .collect(Collectors.toList())
            );

        });
        gameEndClient = Network.endGameClient().setOnReceiveMessageListener((Null) -> {
            runOnUiThread(() -> {
                ConstraintLayout gameEndLayout = findViewById(R.id.gameEndView);
                gameEndLayout.setVisibility(View.VISIBLE);
            });
        });

        readyForRestartClient = Network.restartClient().setOnReceiveMessageListener((Null) -> {
            runOnUiThread(this::recreate);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        menu.getItem(0).setOnMenuItemClickListener(menuItem -> {
            ChangeToInfo();
            return true;
        });

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fortBloodChangeClient != null) {
            fortBloodChangeClient.close();
        }
        if (gameEndClient != null) {
            gameEndClient.close();
        }
        if (readyForRestartClient != null) {
            readyForRestartClient.close();
        }
    }

    private void moveCamera() {
        //move camera to fit the campus on screen
        LatLngBounds bounds = new LatLngBounds(new LatLng(22.993063582069528, 120.21391101450412), new LatLng(23.002434, 120.224757));
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
    }

    private void fetchAndMarkForts() {
        Network.getFortsData()
                .setOnSuccessCallback(this::markForts)
                .execute();
    }

    private void markForts(List<FortData> forts) {
        for (Marker existedMarker: fortMarkers) {
            existedMarker.remove();
        }
        fortMarkers.clear();
        for (FortData fort: forts) {
            Marker marker = map.addMarker(
                new MarkerOptions()
                    .icon(getFortBitmapDescriptor(this, fort))
                    .position(fort.getFortPosition())
                    .anchor(0.5f, 0.5f)
            );
            marker.setTag(fort);
            fortMarkers.add(marker);
            map.addCircle(new CircleOptions()
                    .center(fort.getFortPosition())
                    .radius(20)
                    .strokeColor(Color.RED)
                    .fillColor(Color.argb(80, 255, 0, 0))
            );
        }
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
        Bitmap markerBitmap = getMarkerBitmap(this, R.drawable.map_marker);
        currentMarker = map.addMarker(
                new MarkerOptions()
                        .position(current)
                        .title("Current position")
                        .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
        );
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 19));
        getSupportActionBar().setTitle(R.string.app_name);
        requestLocationUpdate();
        ExtendedFloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
    }

    public void onStartAttackButtonClicked(View v) {
        ExtendedFloatingActionButton floatingButton = findViewById(R.id.fab);
        floatingButton.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_cancel_24));
        floatingButton.setTextColor(Color.BLACK);
        floatingButton.setIconTint(ColorStateList.valueOf(Color.BLACK));
        floatingButton.setBackgroundColor(Color.WHITE);
        floatingButton.shrink();
        floatingButton.setOnClickListener((view) -> cancelAttack());

        bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        startRecording();
    }

    private void startRecording() {
        LatLng current = currentMarker.getPosition();
        PolylineOptions firstPoint = new PolylineOptions()
                .add(current)
                .color(getResources().getColor(R.color.white, null))
                .startCap(new RoundCap())
                .endCap(new RoundCap())
                .jointType(JointType.ROUND)
                .width(25);

        walkedPath = map.addPolyline(firstPoint);
        isRecording = true;
        startRecordingTime = LocalTime.now();
    }

    private void cancelAttack() {
        ExtendedFloatingActionButton floatingButton = findViewById(R.id.fab);
        floatingButton.setIconTint(ColorStateList.valueOf(Color.WHITE));
        floatingButton.setBackgroundColor(Color.BLACK);
        floatingButton.setIcon(ContextCompat.getDrawable(this, R.drawable.sword));
        floatingButton.setTextColor(Color.WHITE);
        floatingButton.extend();
        floatingButton.setOnClickListener(this::onStartAttackButtonClicked);

        Button attackButton = findViewById(R.id.startAttackButton);
        attackButton.setEnabled(true);
        attackButton.setText("Start Attack");
        attackButton.setOnClickListener(this::onStartAttackButtonClicked);
        TextView closerMessageTextView = findViewById(R.id.closerMessageTextView);
        closerMessageTextView.setVisibility(View.GONE);

        walkedPath.remove();
        isRecording = false;
        startRecordingTime = null;
    }

    private void attack(FortData fort) {
        Button attackButton = findViewById(R.id.startAttackButton);
        attackButton.setVisibility(View.GONE);

        long millis = Duration.between(startRecordingTime, LocalTime.now()).toMillis();
        ProgressBar attackProgressBar = findViewById(R.id.attackProgressBar);
        attackProgressBar.setVisibility(View.VISIBLE);
        Network.sendAttack(walkedPath.getPoints(), millis / 1000 / 60, fort.getFortPosition())
                .setOnSuccessCallback((result) -> {
                    ExtendedFloatingActionButton floatingButton = findViewById(R.id.fab);
                    floatingButton.setIconTint(ColorStateList.valueOf(Color.WHITE));
                    floatingButton.setBackgroundColor(Color.BLACK);
                    floatingButton.setIcon(ContextCompat.getDrawable(this, R.drawable.sword));
                    floatingButton.setTextColor(Color.WHITE);
                    floatingButton.extend();
                    floatingButton.setOnClickListener(this::onStartAttackButtonClicked);

                    attackButton.setEnabled(true);
                    attackButton.setText("Start Attack");
                    attackButton.setOnClickListener(this::onStartAttackButtonClicked);
                    attackButton.setVisibility(View.VISIBLE);
                    TextView closerMessageTextView = findViewById(R.id.closerMessageTextView);
                    closerMessageTextView.setVisibility(View.GONE);
                    attackProgressBar.setVisibility(View.GONE);

                    Timer timer = new Timer(millis, 1000, this);
                    fort.addTimer(timer);
                    LinearLayout layout = findViewById(R.id.timerList);
                    fort.setList(layout);

                    new Handler().postDelayed(() -> {
                        bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }, 500);
                })
                .execute();
        walkedPath.remove();
        isRecording = false;
        startRecordingTime = null;
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdate() {
        permissionHelper.executeWithPermission(this, () -> {
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
            LocationRequest request = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(2000);
            client.requestLocationUpdates(request, new CurrentLocationCallback(), getMainLooper());
        });
    }

    public boolean onMarkerClick(Marker marker) {
        if (marker.getTag() != null) {
            refreshBottomSheet((FortData) marker.getTag());
            bottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
            return true;
        }
        return false;
    }

    private void refreshBottomSheet(FortData fort) {
        ImageView fortImageView = findViewById(R.id.fortImage);
        fortImageView.setImageBitmap(getFortBitmap(this, fort));
        TextView hpTextView = findViewById(R.id.hpTextView);
        hpTextView.setText(fort.getHpRepresentation());
        LinearLayout layout = findViewById(R.id.timerList);
        fort.setList(layout);

        if (isRecording) {
            LatLng lastPosition = walkedPath.getPoints().get(walkedPath.getPoints().size()-1);
            Location lastLocation = new Location("");
            lastLocation.setLongitude(lastPosition.longitude);
            lastLocation.setLatitude(lastPosition.latitude);

            LatLng fortPosition = fort.getFortPosition();
            Location fortLocation = new Location("");
            fortLocation.setLongitude(fortPosition.longitude);
            fortLocation.setLatitude(fortPosition.latitude);

            float distance = lastLocation.distanceTo(fortLocation);
            if (distance <= 20) {
                Button attackButton = findViewById(R.id.startAttackButton);
                attackButton.setVisibility(View.VISIBLE);
                attackButton.setText("Attack");
                attackButton.setOnClickListener((view) -> attack(fort));
                TextView closerMessageTextView = findViewById(R.id.closerMessageTextView);
                closerMessageTextView.setVisibility(View.GONE);
            } else {
                Button attackButton = findViewById(R.id.startAttackButton);
                attackButton.setVisibility(View.GONE);
                attackButton.setText("Attack");
                attackButton.setOnClickListener(null);
                TextView closerMessageTextView = findViewById(R.id.closerMessageTextView);
                closerMessageTextView.setVisibility(View.VISIBLE);
            }
        } else {
            Button attackButton = findViewById(R.id.startAttackButton);
            attackButton.setEnabled(true);
            attackButton.setText("Start Attack");
            attackButton.setOnClickListener(this::onStartAttackButtonClicked);
            TextView closerMessageTextView = findViewById(R.id.closerMessageTextView);
            closerMessageTextView.setVisibility(View.GONE);
        }
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
                walkedPath.setPoints(points);
            }
        }
    }
    public void ChangeToInfo(){
        Intent intent = new Intent();
        intent.setClass(MapActivity.this, selfinformation.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (bottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
            return;
        }
        super.onBackPressed();
    }
}