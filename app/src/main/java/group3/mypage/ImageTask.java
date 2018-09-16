package group3.mypage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class ImageTask extends AsyncTask<Object,Integer,Bitmap> {

    private final static String TAG = "ImageTask";
    private  String url;
    private int memberId,imageSize;
    private WeakReference<ImageView> imageViewWeakReference;

    public ImageTask(String url, int memberId, int imageSize) {
        this.url = url;
        this.memberId = memberId;
        this.imageSize = imageSize;
    }


    public ImageTask(String url, int memberId, int imageSize, ImageView imageView) {
        this.url = url;
        this.memberId = memberId;
        this.imageSize = imageSize;
        this.imageViewWeakReference = new WeakReference<>(imageView);
    }


    @Override
    protected Bitmap doInBackground(Object... params) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getImage");
        jsonObject.addProperty("memberId",memberId);
        jsonObject.addProperty("imageSize",imageSize);
        return getRemoteImage(url, jsonObject.toString());
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        ImageView imageView = imageViewWeakReference.get();
        if(isCancelled() || imageView == null){
            return;
        }
        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        }else{
            imageView.setImageResource(R.drawable.icon_facev);

        }

    }

    private Bitmap getRemoteImage(String url, String jsonOut){
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try{
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bw.write(jsonOut);
            Log.d(TAG,"output:"+jsonOut);
            bw.close();

            int requestCode = connection.getResponseCode();
            if(requestCode==200){

                bitmap = BitmapFactory.decodeStream(new BufferedInputStream(connection.getInputStream()));
                Log.d(TAG,"input:"+bitmap);
            }else{
                Log.d(TAG,"requestCode:"+requestCode);
            }
        }catch (IOException e){
            Log.e(TAG,e.toString());
        }finally {
            if(connection!=null)
                connection.disconnect();
        }
        return bitmap;
    }
}
