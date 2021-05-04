package ncku.pd2finalapp.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

class LocationPermissionHelper {

    private final ActivityResultLauncher<String> permissionLauncher;
    LocationPermissionHelper(ActivityResultLauncher<String> permissionLauncher) {
        this.permissionLauncher = permissionLauncher;
    }

    boolean hasPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    void requestPermission() {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

}
