package ncku.pd2finalapp.ui.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import androidx.core.content.ContextCompat;
import ncku.pd2finalapp.R;

class MarkerTool {

    static Bitmap getCurrentMarkerBitmap(Context context) {
        return getMarkerBitmap(context, R.drawable.map_marker);
    }

    static Bitmap getFortBitmap(Context context, FortData fort) {
        Bitmap red = getMarkerBitmap(context, R.drawable.castle_red);
        Bitmap white = getMarkerBitmap(context, R.drawable.castle_white);
        mixBitmapByRatio(red, white, 1 - fort.getHpRatio());
        return red;
    }

    static BitmapDescriptor getFortBitmapDescriptor(Context context, FortData fort) {
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
        int mixerWidth = (int) (mixer.getWidth() * ratio);
        int height = source.getHeight();
        int[] pixels = new int[mixerWidth * height];
        mixer.getPixels(pixels, 0, mixerWidth, 0, 0, mixerWidth, height);
        source.setPixels(pixels, 0, mixerWidth, 0, 0, mixerWidth, height);
    }
}
