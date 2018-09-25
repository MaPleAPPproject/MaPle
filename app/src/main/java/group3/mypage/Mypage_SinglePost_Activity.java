package group3.mypage;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private TextView tvName, tvLocation, tvDescription,tvshowTimeStamp;
    private String comment, location;
    private Button btReturn;
    private int postId;
    private byte[] photo;
    private byte[] profileIcon;
    private CommonTask getPostTask;
    private PostImageTask getPostImageTask;
    private int memberId = 2;
    private CommonTask deletePostTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.my_page_post);
        super.onCreate(savedInstanceState);
//        SharedPreferences pref = getSharedPreferences(Common.PREF_FILE,
//                MODE_PRIVATE);
//        memberId = Integer.valueOf(pref.getString("MemberId",""));
//        Log.i("singlePost sharepref", pref.getString("MemberId",""));
        handleView();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            postId = bundle.getInt("postId");
            loadData(postId);
            loadProfileIcon(2);
        }


    }

    public void handleView() {
        personIcon = findViewById(R.id.personicon);
        personIcon.setImageResource(R.drawable.icon_facev);
        tvName = findViewById(R.id.tvName);
        ivPhoto = findViewById(R.id.ivPhoto);
        tvLocation = findViewById(R.id.tvlocation);
        tvDescription = findViewById(R.id.tvDescription);
        tvshowTimeStamp = findViewById(R.id.tvShowTimeStamp);
        btReturn = findViewById(R.id.btReturn);

    }


    public void onReturnClick(View view) {
        int memberId = 2;
        Intent intent = new Intent(Mypage_SinglePost_Activity.this, MainActivity.class);
        intent.putExtra("memberId", memberId);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.single_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.newPost_modify:
                Intent modifyIntent = new Intent(this,Mypage_PostUpdate_Activity.class);
                modifyIntent.putExtra("postId", postId);
                startActivity(modifyIntent);


                return true;
            case R.id.newPost_delete:

                AlertDialog.Builder builder = new AlertDialog.Builder(Mypage_SinglePost_Activity.this);
                builder.setMessage("Are you sure that you want to delete this post ?")
                        .setTitle("Delete");
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deletePost(postId);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public void loadData(int postId) {

        if (Common.networkConnected(this)) {
            String url = Common.URL + "/CpostServlet";
            NewPost post = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getPost");
            jsonObject.addProperty("postId", postId);
            String jsonOut = jsonObject.toString();
            getPostTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = getPostTask.execute().get();
                post = new Gson().fromJson(jsonIn, NewPost.class);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (post == null) {
                Toast.makeText(this, "no_profile", Toast.LENGTH_SHORT).show();
            } else {
                tvName.setText(post.getUserName());

                tvshowTimeStamp.setText(post.getPostedDate().substring(0, 16));
                tvDescription.setText(post.getComment());
                tvLocation.setText(post.getDistrict());
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


    public void deletePost(int postId){


        if (Common.networkConnected(this)) {
            String url = Common.URL + "/CpostServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "deletePost");
            jsonObject.addProperty("postId", postId);
            String jsonOut = jsonObject.toString();
            deletePostTask = new CommonTask(url, jsonOut);
            int count = 0;
            try {

                String jsonIn = deletePostTask.execute().get();
                count = Integer.valueOf(jsonIn);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (count != 0 ) {
                Toast.makeText(this, "Your post has deleted", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, "Fail to delete the post", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "no_profile", Toast.LENGTH_SHORT).show();
        }



    }
}



