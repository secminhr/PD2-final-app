package ncku.pd2finalapp.ui.map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import ncku.pd2finalapp.R;
import ncku.pd2finalapp.databinding.ActivityMapBinding;
import ncku.pd2finalapp.ui.map.model.AttackParameter;
import ncku.pd2finalapp.ui.map.model.FortData;
import ncku.pd2finalapp.ui.map.statecontrolling.MapState;
import ncku.pd2finalapp.ui.map.statecontrolling.UserState;
import ncku.pd2finalapp.ui.map.tools.LocationPermissionHelper;
import ncku.pd2finalapp.ui.map.tools.Timer;
import ncku.pd2finalapp.ui.network.Network;
import ncku.pd2finalapp.ui.network.ws.FortBloodUpdateClient;
import ncku.pd2finalapp.ui.network.ws.WSClient;
import ncku.pd2finalapp.ui.selfinfo.selfinformation;

import static ncku.pd2finalapp.ui.map.statecontrolling.UserState.Attacking;
import static ncku.pd2finalapp.ui.map.statecontrolling.UserState.NotRecording;
import static ncku.pd2finalapp.ui.map.statecontrolling.UserState.SendingAttack;
import static ncku.pd2finalapp.ui.map.tools.MapTool.distanceBetween;
import static ncku.pd2finalapp.ui.map.tools.MapTool.getCurrentMarkerBitmap;
import static ncku.pd2finalapp.ui.map.tools.MapTool.getFortBitmap;
import static ncku.pd2finalapp.ui.map.tools.MapTool.getFortBitmapDescriptor;
import static ncku.pd2finalapp.ui.map.tools.MapTool.locationToLatLng;
import static ncku.pd2finalapp.ui.map.tools.MapTool.setupMap;

public class MapActivity extends AppCompatActivity {

    private GoogleMap map;
    private Marker currentMarker;
    private final ArrayList<Marker> fortMarkers = new ArrayList<>();
    private final ArrayList<Circle> fortCircles = new ArrayList<>();

    private ActivityMapBinding binding;

    private final AttackParameter attackParameter = new AttackParameter();
    private BottomSheetBehavior<ConstraintLayout> bottomSheet;

    private boolean isVibrating = false;

    //note: there will be 3
    private WSClient<FortBloodUpdateClient.BloodUpdate> fortBloodChangeClient = null;
    private WSClient<Void> gameEndClient = null;
    private WSClient<Void> readyForRestartClient = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    public void ChangeToInfo(MenuItem item){
        Intent intent = new Intent();
        intent.setClass(MapActivity.this, selfinformation.class);
        startActivity(intent);
    }

    public void moveCameraToCurrentPosition(MenuItem item) {
        if (currentMarker != null) {
            map.animateCamera(CameraUpdateFactory.newLatLng(currentMarker.getPosition()));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupMapFragment();

        bottomSheet = BottomSheetBehavior.from(binding.bottomSheet);
        bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);

        fortBloodChangeClient = Network.bloodUpdateClient().onReceiveMessage(update -> {
            runOnUiThread(() -> {
                for(Marker fortMarker: fortMarkers) {
                    update.update((FortData) fortMarker.getTag());
                }
                markForts(
                        fortMarkers.stream()
                                .map(marker -> (FortData) marker.getTag())
                                .collect(Collectors.toList())
                );
            });
        });

        gameEndClient = Network.endGameClient().onReceiveMessage(Null -> {
            runOnUiThread(() -> binding.gameEndView.setVisibility(View.VISIBLE));
        });

        readyForRestartClient = Network.restartClient().onReceiveMessage(Null -> {
            //delay it so the message of game ending can be seen
            runOnUiThread(() -> {
                new Handler().postDelayed(() -> {
                    this.recreate();
                }, 2000);
            });
        });

//        Vibrator vibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
//        vibrator.vibrate(VibrationEffect.createWaveform(new long[] {
//                10L, 500L
//        }, new int[] {
//                230, 0
//        }, 0));
    }

    private void setupMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(map -> {
            this.map = map;
            setupMap(map, this, (marker) -> {
                if (marker.getTag() != null) {
                    setupBottomSheet((FortData) marker.getTag());
                    bottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                return marker.getTag() != null;
            });
            mapState.setMapReady();
        });
        mapFragment.getView().getViewTreeObserver().addOnGlobalLayoutListener(mapState::setViewRendered);
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

    private final MapState mapState = new MapState().onMapViewReady(() -> {
        moveCameraLocatingNCKU();
        fetchAndMarkForts();
        requireMyLocation();
    });

    private void moveCameraLocatingNCKU() {
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
        clearFortMarkers();
        clearCircles();
        for (FortData fort: forts) {
            Marker marker = createFortMarker(fort);
            fortMarkers.add(marker);
            if (fort.getHp() != 0) {
                Circle c = map.addCircle(new CircleOptions()
                        .center(fort.getFortPosition())
                        .radius(20)
                        .strokeColor(Color.RED)
                        .strokeWidth(3f)
                        .fillColor(Color.argb(80, 255, 0, 0))
                );
                fortCircles.add(c);
            }
        }
    }

    private void clearFortMarkers() {
        for (Marker existedMarker: fortMarkers) {
            existedMarker.remove();
        }
        fortMarkers.clear();
    }


    private void clearCircles() {
        for (Circle circle: fortCircles) {
            circle.remove();
        }
        fortCircles.clear();
    }

    private Marker createFortMarker(FortData fort) {
        Marker marker = map.addMarker(
                new MarkerOptions()
                        .icon(getFortBitmapDescriptor(this, fort))
                        .position(fort.getFortPosition())
                        .anchor(0.5f, 0.5f)
        );
        marker.setTag(fort);
        return marker;
    }

    private final LocationPermissionHelper permissionHelper =
            new LocationPermissionHelper(this).onUserDeny(() -> {
        //when user deny the permission request
        Toast.makeText(this, "Sorry, we can't calculate your position if the permission is not granted.", Toast.LENGTH_SHORT).show();
        finish();
    });


    @SuppressLint("MissingPermission")
    private void requireMyLocation() {
        permissionHelper.executeWithPermission(this, () -> {
            getSupportActionBar().setTitle("Loading position...");
            LocationServices
                    .getFusedLocationProviderClient(this)
                    .getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(this::onFirstTimeLocatedCurrent);
        });
    }

    private void onFirstTimeLocatedCurrent(Location location) {
        if (location == null) { //gps may not be opened
            new AlertDialog.Builder(this)
                    .setTitle("Cannot locate position")
                    .setMessage("We cannot locate your current position.\nPlease turn on your gps and network if you haven't.")
                    .setPositiveButton("Done", (dialogInterface, i) -> {
                        requireMyLocation();
                    }).show();
            return;
        }
        LatLng current = locationToLatLng(location);
        Bitmap markerBitmap = getCurrentMarkerBitmap(this);
        currentMarker = map.addMarker(
                new MarkerOptions()
                        .position(current)
                        .title("Current position")
                        .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
                        .anchor(0.5f, 0.5f)
        );
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 18.3f));
        getSupportActionBar().setTitle(R.string.app_name);
        requestLocationUpdate();
        binding.fab.setVisibility(View.VISIBLE);
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdate() {
        permissionHelper.executeWithPermission(this, () -> {
            LocationRequest request = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(2000);
            LocationServices
                    .getFusedLocationProviderClient(this)
                    .requestLocationUpdates(request, new CurrentLocationCallback(), getMainLooper());
        });
    }

    public void onStartAttackButtonClicked(View v) {
        UserState.setState(Attacking, binding);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentMarker.getPosition(), 18.3f));
        binding.fab.setOnClickListener(view -> cancelAttack());
        bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);

        LatLng current = currentMarker.getPosition();
        PolylineOptions firstPoint = new PolylineOptions()
                .add(current)
                .color(getResources().getColor(R.color.white, null))
                .startCap(new RoundCap())
                .endCap(new RoundCap())
                .jointType(JointType.ROUND)
                .width(25);

        attackParameter.startRecord(map, firstPoint);
    }

    public void cancelAttack() {
        UserState.setState(NotRecording, binding);
        binding.fab.setOnClickListener(this::onStartAttackButtonClicked);
        attackParameter.clear();
    }

    public void attack(FortData fort) {
        UserState.setState(SendingAttack, binding);
        attackParameter.stopRecord();
        Network.sendAttack(attackParameter, fort.getFortPosition())
                .setOnSuccessCallback((result) -> {
                    UserState.setState(NotRecording, binding);
                    binding.fab.setOnClickListener(this::onStartAttackButtonClicked);
                    binding.startAttackButton.setOnClickListener(this::onStartAttackButtonClicked);

                    Timer timer = new Timer(attackParameter.durationInMillis(), 1000, this);
                    fort.addTimer(timer);
                    fort.setList(binding.timerList);
                    attackParameter.clear();
                    new Handler().postDelayed(() -> {
                        bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }, 500);
                })
                .execute();
    }

    private void setupBottomSheet(FortData fort) {
        binding.fortImage.setImageBitmap(getFortBitmap(this, fort));
        binding.hpTextView.setText(fort.getHpRepresentation());
        fort.setList(binding.timerList);

        if (UserState.current == NotRecording) {
            binding.startAttackButton.setOnClickListener(this::onStartAttackButtonClicked);
        } else {
            LatLng lastPosition = attackParameter.lastPosition();
            LatLng fortPosition = fort.getFortPosition();

            Button attackButton = binding.startAttackButton;
            if (fort.getHp() == 0) {
                attackButton.setVisibility(View.GONE);
                binding.closerMessageTextView.setVisibility(View.GONE);
                binding.alreadyTakenDownMessage.setVisibility(View.VISIBLE);
            } else if (distanceBetween(lastPosition, fortPosition) <= 20) {
                attackButton.setText("Attack");
                attackButton.setVisibility(View.VISIBLE);
                attackButton.setOnClickListener((view) -> attack(fort));
                binding.closerMessageTextView.setVisibility(View.GONE);
                binding.alreadyTakenDownMessage.setVisibility(View.GONE);
            } else {
                attackButton.setVisibility(View.GONE);
                binding.alreadyTakenDownMessage.setVisibility(View.GONE);
                binding.closerMessageTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (bottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
            return;
        }
        super.onBackPressed();
    }

    private class CurrentLocationCallback extends LocationCallback {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            LatLng newPoint = locationToLatLng(locationResult.getLastLocation());
            currentMarker.setPosition(newPoint);

            if (UserState.current == Attacking) {
                attackParameter.addPosition(newPoint);
            }

            boolean hasEnterFortRegion = false;
            for(Marker fort: fortMarkers) {
                float distance = distanceBetween(fort.getPosition(), newPoint);
                if (distance <= 20) {
                    hasEnterFortRegion = true;
                    if (!isVibrating && ((FortData) fort.getTag()).getHp() != 0) {
                        isVibrating = true;
                        Vibrator vibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
                        vibrator.vibrate(VibrationEffect.createWaveform(new long[] {
                                10L, 500L
                        }, new int[] {
                                230, 0
                        }, 0));
                        setupBottomSheet((FortData) fort.getTag());
                        bottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                    break;
                }
            }
            if (!hasEnterFortRegion) {
                Vibrator vibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.cancel();
                isVibrating = false;
            }
        }
    }
}