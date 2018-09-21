package group3.mypage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.JsonObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.cp102group3maple.violethsu.maple.R.drawable.addimage;

public class PostImageTask extends AsyncTask<Object,Integer,Bitmap>{

    private final static String TAG = "PostImageTask";
    private  String url;
    private int postId,imageSize;
    private WeakReference<ImageView> imageViewWeakReference;

    public PostImageTask(String url, int postId, int imageSize) {
        this.url = url;
        this.postId = postId;
        this.imageSize = imageSize;
    }




    public PostImageTask(String url, int postId, int imageSize, ImageView imageView) {
        this.url = url;
        this.postId = postId;
        this.imageSize = imageSize;
        this.imageViewWeakReference = new WeakReference<>(imageView);
    }




    @Override
    protected Bitmap doInBackground(Object... params) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getPostImage");
        jsonObject.addProperty("postId",postId);
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
            imageView.setImageResource(addimage);

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
