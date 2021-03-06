package ncku.pd2finalapp.ui.map.tools;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import ncku.pd2finalapp.ui.map.Block;

public class LocationPermissionHelper {

    private final ActivityResultLauncher<String> permissionLauncher;
    private Block suspendedBlock = () -> {};
    private Block onUserDenyBlock = () -> {};

    public LocationPermissionHelper(ComponentActivity activity) {
        this.permissionLauncher = activity.registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) {
                suspendedBlock.execute();
            } else {
                onUserDenyBlock.execute();
            }
        });
    }

    public LocationPermissionHelper onUserDeny(Block onUserDeny) {
        onUserDenyBlock = onUserDeny;
        return this;
    }

    public void executeWithPermission(Context context, Block block) {
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
