package group3.mypage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;

import group3.Common;
import group3.MainActivity;

import static android.support.constraint.motion.utils.Oscillator.TAG;

public class Mypage_SinglePost_Activity extends AppCompatActivity {
    private ImageView personIcon, ivPhoto;
    private TextView tvName, tvLocation, tvDescription, tvshowTimeStamp;
    private String comment, location;
    private Button backButton;
    private int postId;
    private byte[] photo;
    private byte[] profileIcon;
    private CommonTask getPostTask;
    private PostImageTask getPostImageTask;
    private int memberId = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.fragment_mypage);
        super.onCreate(savedInstanceState);
//        SharedPreferences pref = getSharedPreferences(Common.PREF_FILE,
//                MODE_PRIVATE);
//        memberId = Integer.valueOf(pref.getString("MemberId",""));
        handleView();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            postId = bundle.getInt("postId");
            comment = bundle.getString("comment");
            tvDescription.setText(comment);
            location = bundle.getString("location");
            tvLocation.setText(location);
        }
        loadData(postId);
        loadProfileIcon(memberId);


    }

    private void handleView() {
        personIcon = findViewById(R.id.personicon);
//        personIcon.setImageResource(R.drawable.icon_facev);
        tvName = findViewById(R.id.tvName);
        ivPhoto = findViewById(R.id.ivPhoto);
        tvLocation = findViewById(R.id.tvLocation);
        tvDescription = findViewById(R.id.tvDescription);
        tvshowTimeStamp = findViewById(R.id.tvShowTimeStamp);


    }


    public void onBackButtonClick(View view) {
        Intent intent = new Intent(Mypage_SinglePost_Activity.this, MainActivity.class);
        intent.putExtra("id", 1);

        startActivity(intent);
    }


    public void loadData(int postId) {

        if (Common.networkConnected(this)) {
            String url = Common.URL + "/CpostServlet";
            User_Profile userProfiles = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getPost");
            jsonObject.addProperty("postId", postId);
            String jsonOut = jsonObject.toString();
            getPostTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = getPostTask.execute().get();
                userProfiles = new Gson().fromJson(jsonIn, User_Profile.class);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (userProfiles == null) {
                Toast.makeText(this, "no_profile", Toast.LENGTH_SHORT).show();
            } else {
                tvName.setText(userProfiles.getUserName());
                tvshowTimeStamp.setText(userProfiles.getPassword());
                //getPostImage
                int imageSize = getResources().getDisplayMetrics().widthPixels / 3 * 2;
                Bitmap bitmap = null;
                try {
                    bitmap = new PostImageTask(url, postId, imageSize, personIcon).execute().get();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (bitmap != null) {
                    ivPhoto.setImageBitmap(bitmap);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    photo = out.toByteArray();

                } else {
                    Toast.makeText(this, "no_image", Toast.LENGTH_SHORT).show();

                }
                //getPhotoIcon
                int memberId = 2;
                int imageIconSize = getResources().getDisplayMetrics().widthPixels / 4;
                Bitmap iconBitmap = null;
                try {
                    iconBitmap = new ImageTask(url, memberId, imageIconSize, personIcon).execute().get();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (iconBitmap != null) {
                    ivPhoto.setImageBitmap(iconBitmap);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    iconBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    photo = out.toByteArray();

                } else {
                    Toast.makeText(this, "no_image", Toast.LENGTH_SHORT).show();

                }
            }

        } else {
            Toast.makeText(this, "no_profile", Toast.LENGTH_SHORT).show();
        }


    }

    public void loadProfileIcon(int memberId) {


        if (Common.networkConnected(this)) {
            String url = Common.URL + "/User_profileServlet";
            int imageSize = getResources().getDisplayMetrics().widthPixels / 4;
            Bitmap bitmap = null;

            try {
                bitmap = new ImageTask(url, memberId, imageSize, personIcon).execute().get();

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (bitmap != null) {
                personIcon.setImageBitmap(bitmap);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                profileIcon = out.toByteArray();

            } else {
                Toast.makeText(this, "no_image", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
