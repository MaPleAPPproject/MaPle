package group3;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import static android.content.Context.*;

public class Common {


    private static final String TAG = "Common";
    public static String IP = "10.0.2.2";
//    public static String IP = "192.168.196.124";
    public static String URL = "http://"+IP+":8080/MaPle";
    public final static String PREF_FILE = "preference";
    private final static String DEFAULT_NAME = "";
    private final static String DEFAULT_EMAIL = "";
    private final static String DEFAULT_PASSWORD = "";
    private final static String DEFAULT_SELFINTRO = "";


    public static Bitmap downSize(Bitmap srcPicture, int newSize) {

        if (newSize < 20) {
            newSize = 20;
        }
        int srcWidth = srcPicture.getWidth();
        int srcHeight = srcPicture.getHeight();
        int longer = Math.max(srcWidth, srcHeight);
        if (longer > newSize) {
            double scale = longer / (double) newSize;
            int dstWidth = (int) (srcWidth / scale);
            int dstHeight = (int) (srcHeight / scale);

            srcPicture = Bitmap.createScaledBitmap(srcPicture, dstWidth, dstHeight, false);
            System.gc();
        }
        return srcPicture;
    }

    public static Bitmap upSize(Bitmap srcPicture, int newSize) {

        if (newSize < 20) {
            newSize = 20;
        }
        int srcWidth = srcPicture.getWidth();
        int srcHeight = srcPicture.getHeight();
        int longer = Math.max(srcWidth, srcHeight);
        if (longer > newSize) {
            double scale = longer / (double) newSize;
            int dstWidth = (int) (srcWidth / scale);
            int dstHeight = (int) (srcHeight / scale);

            srcPicture = Bitmap.createScaledBitmap(srcPicture, dstWidth, dstHeight, false);
            System.gc();
        }
        return srcPicture;
    }


    public static final int REQ_EXTERNAL_STORAGE = 0;
    public static final int REQ_ACCESS_FINE_LOCATION = 1;


    public static void askPermissions(Activity activity, String[] permissions, int requestCode) {
        Set<String> permissionsRequest = new HashSet<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(activity, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(permission);
            }
        }

        if (!permissionsRequest.isEmpty()) {
            ActivityCompat.requestPermissions(activity,
                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
                    requestCode);
        }
    }


    public static boolean networkConnected(Context context) {
        ConnectivityManager conManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager != null ? conManager.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isConnected();
    }


    public static byte[] bitmapToPNG(Bitmap srcPicture) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        srcPicture.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();


    }

    public SharedPreferences getSharedPreferences(String name, int mode) {
        return getSharedPreferences(PREF_FILE, MODE_PRIVATE);
    }
    public static void showToast(Context context, int messageResId) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
    }



}
