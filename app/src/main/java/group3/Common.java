package group3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.HashSet;
import java.util.Set;

import group3.mypage.Mypage_UserProfile_Activity;

public class Common {

    public String URL = "http://10.0.2.2:8080/MaPle_Web";
    public final static String PREF_FILE = "preference";
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


    public static final int REQ_EXTERNAL_STORAGE = 0;

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
//    public static boolean networkConnected(Activity activity){
//        ConnctivityManager connctivityManager =
//                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connctivityManager != null? connctivityManager.getActiveNetworkInfo() : null;
//        return networkInfo != null && networkInfo.isConnected();
//    }


//    public void confirmExit(){
//
//
//        AlertDialog.Builder  ad= new AlertDialog.Builder(Common,MainActivity.class)
//                .setTitle("確認視窗")
//                .setMessage("確定要離開此頁面嗎？")
//                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//
//
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//
//                    }
//                });
//        ad.show();
//
//
//    }





}
