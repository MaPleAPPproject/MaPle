package group3.mypage;


import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import group3.Common;
import group3.MainActivity;

import static android.support.constraint.motion.utils.Oscillator.TAG;

public class Mypage_PostUpdate_Activity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_TAKE_PICTURE = 1;
    private static final int REQUEST_PICK_PICTURE = 2;
    private static final int REQUEST_CROP_PICTURE = 3;
    private static final int REQUEST_GET_LOCATION = 4;
    private int postId;
    private CommonTask getPostTask, updateTask;
    private TextView tvLocation;
    private ImageView imageView,ivMarker;
    private Button btCurrent, btmap, btCancel, btSend;
    private EditText etComment;
    private byte[] photo;
    private File file;
    private Uri contentUri, croppedImageUri;
    private double latitude, longitude;
    private String address, adminArea, countryCode, countryName, district;
    private Bitmap picture;
    private byte[] image;
    private  int memberId;
    private User_Profile userProfiles;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatepost);
        SharedPreferences pref = getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        memberId = Integer.valueOf(pref.getString("MemberId",""));
        handleViews();
        Intent intent = getIntent();
        postId = intent.getIntExtra("postId", 0);
        loadPost(postId);


    }

    private void handleViews() {
        tvLocation = findViewById(R.id.tvLocation);
        tvLocation.setVisibility(View.GONE);
        imageView = findViewById(R.id.ibPhoto);
        ivMarker = findViewById(R.id.ivMarker);
        btCancel = findViewById(R.id.btcancel);
        btSend = findViewById(R.id.btSend);
        etComment = findViewById(R.id.etComment);
        btSend.setOnClickListener(this);
        btCancel.setOnClickListener(this);
//        btCurrent.setOnClickListener(this);
        ivMarker.setOnClickListener(this);
//        imageView.setOnClickListener(this);



    }

    public static boolean isIntentAvailable(Context context, Intent intent) {

        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btSend:
                if ((image == null) && (photo == null )) {

                    Toast.makeText(this, "請確認欄位是否有空白", Toast.LENGTH_SHORT).show();
                    return;
                }else if(image == null){

                }
                updatePost(postId);
//                Bundle bundle = new Bundle();
//                bundle.putInt("postId", postId);
//                Intent sendIntent = new Intent(this, Mypage_SinglePost_Activity.class);
//                sendIntent.putExtras(bundle);
//                Toast.makeText(this, "Your post successfully updated", Toast.LENGTH_SHORT).show();
//                startActivity(sendIntent);
                break;


            case R.id.btcancel:
                confirmExit();

                break;


            case R.id.ivMarker:
                Intent mapIntent = new Intent(this, MapLocation.class);
                startActivityForResult(mapIntent, REQUEST_GET_LOCATION);


                break;




        }

    }




    public void confirmExit() {


        android.app.AlertDialog.Builder ad = new android.app.AlertDialog.Builder(Mypage_PostUpdate_Activity.this)
                .setTitle("確認視窗")
                .setMessage("確定要離開此頁面嗎？")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Mypage_PostUpdate_Activity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        ad.show();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {
            case REQUEST_TAKE_PICTURE:
                Log.e("take picture", "contenturi = " + contentUri);
                try {
                    crop(contentUri);
                }catch (NullPointerException npe){
                    npe.toString();
                }finally {

                }

                break;
            case REQUEST_PICK_PICTURE:
                try{
                    Uri uri = data.getData();
                    Log.e("pick picture", "uri = " + uri);
                    crop(uri);
                }catch (NullPointerException npe){
                    npe.toString();
                }finally {

                }
                break;

            case REQUEST_CROP_PICTURE:
                Log.e(TAG, "REQ_CROP_PICTURE: " + croppedImageUri.toString());
                try {
                    if(croppedImageUri != null) {
                        picture = BitmapFactory.decodeStream(getContentResolver().openInputStream(croppedImageUri));
                        Bitmap downSizePicture = Common.downSize(picture, 512);
                        imageView.setImageBitmap(downSizePicture);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        downSizePicture.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        image = out.toByteArray();
                    }else{
                        return;
                    }

                } catch (FileNotFoundException e) {
                    Log.e(TAG, e.toString());
                }
                break;

            case REQUEST_GET_LOCATION:


                try{
                    Bundle bundle = data.getExtras();
                    latitude = bundle.getDouble("latitude");
                    longitude = bundle.getDouble("longitude");
                    address = bundle.getString("address");
                    adminArea = bundle.getString("adminArea");
                    countryCode = bundle.getString("countryCode");
                    countryName = bundle.getString("countryName");
//                    district = bundle.getString("district");
                    if (adminArea == null) {
                        district = address;
                    } else {
                        district = countryName + "," + adminArea;
                    }
                    tvLocation.setText(district);
                    tvLocation.setVisibility(View.VISIBLE);
                }catch(Exception e){
                    e.toString();
                }finally {

                    if (tvLocation == null) {
                        Toast.makeText(this, "別忘了選擇地點喔！", Toast.LENGTH_SHORT);
                    }
                }
                break;


            default:
        }
    }

    public void crop(Uri srcImageUri) {
        File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        croppedImageUri = Uri.fromFile(file);
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cropIntent.setDataAndType(srcImageUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 180);
            cropIntent.putExtra("outputY", 180);
            cropIntent.putExtra("scale", true);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, croppedImageUri);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, REQUEST_CROP_PICTURE);
        } catch (ActivityNotFoundException anfe) {
            Toast.makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Common.askPermissions(this, permissions, Common.REQ_EXTERNAL_STORAGE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {


        switch (requestCode) {
            case Common.REQ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageView.setEnabled(true);


                } else {
                    imageView.setEnabled(false);
                    Toast.makeText(this, "請同意使用本機相機和讀取權限", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
        }
    }

    public void loadPost(int postId) {

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
//
                Log.e("PostUpdate_Activity", "Fail to load post from server.");

            } else {

                etComment.setText(post.getComment());
                tvLocation.setText(post.getDistrict());
                tvLocation.setVisibility(View.VISIBLE);
                //getPostImage
                int imageSize = getResources().getDisplayMetrics().widthPixels / 3 * 2;
//                int imageSize = 900;
                Bitmap bitmap = null;
                try {
                    bitmap = new PostImageTask(url, postId, imageSize, imageView).execute().get();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    photo = out.toByteArray();

                } else {
//                    Toast.makeText(this, "no_image", Toast.LENGTH_SHORT).show();
                    Log.e("PostUpdate_Activity", "Fail to load post photo from server.");

                }

            }

        } else {
//           Toast.makeText(this, getString(R.string.msg_Nonetwork), Toast.LENGTH_SHORT).show();
            Log.e("PostUpdate_Activity", "Fail to connect to server");
        }
    }


    public void updatePost(int postId) {


        if (Common.networkConnected(this)) {
            String url = Common.URL + "/CpostServlet";
            String comment = etComment.getText().toString().trim();
            NewPost updatePost = new NewPost(comment, countryCode, address, district, longitude, latitude);
            JsonObject jsonObject = new JsonObject();
            if(image != null){
                String imageBase64 = Base64.encodeToString(image, Base64.DEFAULT);
                jsonObject.addProperty("action", "update");
                jsonObject.addProperty("postId", postId);
                jsonObject.addProperty("updatePost", new Gson().toJson(updatePost));
                jsonObject.addProperty("imageBase64", imageBase64);
            }else{
                String imageBase64 = Base64.encodeToString(photo, Base64.DEFAULT);
                jsonObject.addProperty("action", "update");
                jsonObject.addProperty("postId", postId);
                jsonObject.addProperty("updatePost", new Gson().toJson(updatePost));
                jsonObject.addProperty("imageBase64", imageBase64);
            }

            String jsonOut = jsonObject.toString();
            updateTask = new CommonTask(url, jsonOut);
            int count = 0;
            try {
                String jsonIn = updateTask.execute().get();
                count = Integer.valueOf(jsonIn);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (count == 0) {
                Toast.makeText(this, "您的貼文更新失敗，請再試一次.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "您的貼文已成功更改", Toast.LENGTH_SHORT).show();
            }

            Intent intent = new Intent(this, Mypage_SinglePost_Activity.class);
            intent.putExtra("postId", postId);
            startActivity(intent);
            finish();


        } else

        {
            Toast.makeText(this, getString(R.string.msg_Nonetwork), Toast.LENGTH_SHORT).show();
        }


    }


    public void onPhotoClick(View view) {


        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(Mypage_PostUpdate_Activity.this).create();
        alertDialog.show();
        Window win = alertDialog.getWindow();


        win.setContentView(R.layout.alertdialog_layout);

        ImageButton camera_btn = (ImageButton) win.findViewById(R.id.camera);
        camera_btn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                file = new File(file, "picture.jpg");
                contentUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                if (isIntentAvailable(getApplicationContext(), intent)) {
                    startActivityForResult(intent, REQUEST_TAKE_PICTURE);
                    alertDialog.dismiss();


                } else {

                    Toast.makeText(getBaseContext(), R.string.msg_NoCameraAppsFound, Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton gallery_btn = (ImageButton) win.findViewById(R.id.gallery);
        gallery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent1, REQUEST_PICK_PICTURE);
                if (alertDialog != null)
                    alertDialog.dismiss();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (updateTask != null) {
            updateTask.cancel(true);
        }

        if (getPostTask != null) {
            getPostTask.cancel(true);
        }
    }


}