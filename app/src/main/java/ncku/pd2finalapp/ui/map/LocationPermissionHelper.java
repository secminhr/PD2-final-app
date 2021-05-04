package ncku.pd2finalapp.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

class LocationPermissionHelper {

    private final ActivityResultLauncher<String> permissionLauncher;
    private Block suspendedBlock = null;
    private Block onUserDenyBlock = null;

    LocationPermissionHelper(ComponentActivity activity) {
        this.permissionLauncher = activity.registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) {
                if (suspendedBlock != null) {
                    suspendedBlock.execute();
                }
            } else {
                if (onUserDenyBlock != null) {
                    onUserDenyBlock.execute();
                }
            }
        });
    }

    LocationPermissionHelper onUserDeny(Block onUserDeny) {
        onUserDenyBlock = onUserDeny;
        return this;
    }

    void executeWithPermission(Context context, Block block) {
        if (hasPermission(context)) {
            block.execute();
        } else {
            suspendedBlock = block;
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private boolean hasPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

}
