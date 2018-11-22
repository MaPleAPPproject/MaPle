package group3.friend.Chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import static group3.Common.IP;


public class SocketCommon {
    private static Map<String, String> friendInChat ;

    private static final String TAG = "SocketCommon";
    public final static String SERVER_URI =
            "ws://"+IP+":8080/MaPle/ChatServer/";
    public static ChatWebSocketClient chatWebSocketClient;
    private static List<String> onlinefriendList = new ArrayList<>();




    // 建立WebSocket連線
    public static void connectServer(String userName, Context context) {
        if (chatWebSocketClient == null) {
            URI uri = null;
            try {
                uri = new URI(SERVER_URI + userName);
            } catch (URISyntaxException e) {
                Log.e(TAG, e.toString());
            }
            chatWebSocketClient = new ChatWebSocketClient(uri, context);
            chatWebSocketClient.connect();
        }
    }

    // 中斷WebSocket連線
    public static void disconnectServer() {
        if (chatWebSocketClient != null) {
            chatWebSocketClient.close();
            chatWebSocketClient = null;
        }
        onlinefriendList.clear();
    }

    public static List<String> getonlineFriendList() {
        return onlinefriendList;
    }

    public static void setonlineFriendList(List<String> onlinefriendList) {
        SocketCommon.onlinefriendList = onlinefriendList;
    }

    public static Bitmap downSize(Bitmap srcBitmap, int newSize) {
        if (newSize <= 50) {
            // 如果欲縮小的尺寸過小，就直接定為128
            newSize = 128;
        }
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();
        String text = "source image size = " + srcWidth + "x" + srcHeight;
        Log.d(TAG, text);
        int longer = Math.max(srcWidth, srcHeight);

        if (longer > newSize) {
            double scale = longer / (double) newSize;
            int dstWidth = (int) (srcWidth / scale);
            int dstHeight = (int) (srcHeight / scale);
            srcBitmap = Bitmap.createScaledBitmap(srcBitmap, dstWidth, dstHeight, false);
            System.gc();
            text = "\nscale = " + scale + "\nscaled image size = " +
                    srcBitmap.getWidth() + "x" + srcBitmap.getHeight();
            Log.d(TAG, text);
        }
        return srcBitmap;
    }
    public static void reconnect(){
        if (chatWebSocketClient != null) {

        }
    }

}
