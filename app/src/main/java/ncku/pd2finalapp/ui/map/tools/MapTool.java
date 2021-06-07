package ncku.pd2finalapp.ui.map.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import androidx.core.content.ContextCompat;
import ncku.pd2finalapp.R;
import ncku.pd2finalapp.ui.map.model.FortData;

public class MapTool {

    public static void setupMap(GoogleMap map,
                                Context context,
                                GoogleMap.OnMarkerClickListener listener) {
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style));
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setOnMarkerClickListener(listener);
    }

    public static Bitmap getCurrentMarkerBitmap(Context context) {
        return getMarkerBitmap(context, R.drawable.map_marker);
    }

    public static Bitmap getFortBitmap(Context context, FortData fort) {
        Bitmap red = getMarkerBitmap(context, R.drawable.castle_red);
        Bitmap white = getMarkerBitmap(context, R.drawable.castle_white);
        mixBitmapByRatio(red, white, 1 - fort.getHpRatio());
        return red;
    }

    public static BitmapDescriptor getFortBitmapDescriptor(Context context, FortData fort) {
        return BitmapDescriptorFactory.fromBitmap(getFortBitmap(context, fort));
    }

    private static Bitmap getMarkerBitmap(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Canvas canvas = new Canvas();
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap markerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(markerBitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return markerBitmap;
    }

    private static void mixBitmapByRatio(Bitmap source, Bitmap mixer, double ratio) {
        int mixerHeight = (int) (mixer.getHeight() * ratio);
        int width = source.getWidth();
        int[] pixels = new int[width * mixerHeight];
        mixer.getPixels(pixels, 0, width, 0, 0, width, mixerHeight);
        source.setPixels(pixels, 0, width, 0, 0, width, mixerHeight);
    }

    public static float distanceBetween(LatLng a, LatLng b) {
        Location aLocation = latLngToLocation(a);
        Location bLocation = latLngToLocation(b);
        return aLocation.distanceTo(bLocation);
    }

    private static Location latLngToLocation(LatLng latLng) {
        Location location = new Location("");
        location.setLongitude(latLng.longitude);
        location.setLatitude(latLng.latitude);
        return location;
    }

    public static LatLng locationToLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }
}
