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
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;


import group3.Common;
import group3.MainActivity;


import static android.support.constraint.motion.utils.Oscillator.TAG;

public class NewPost_Activity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_TAKE_PICTURE = 1;
    private static final int REQUEST_PICK_PICTURE = 2;
    private static final int REQUEST_CROP_PICTURE = 3;
    private static final int REQUEST_GET_LOCATION = 4;


    private static final int RESULT_OK = 0;


    private ImageView ibPhoto,ivMarker;
    private Button btsend, btcancel, btcurrent, btmap;
    private EditText etComment;
    private File file;
    private Uri contentUri, croppedImageUri;
    private Bitmap picture;
    private Intent intent;
    private byte[] image;

    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private GoogleMap map;
    private TextView tvLocation;
    private double latitude;
    private double longitude;
    private String address;
    private String adminArea;
    private String countryCode;
    private String countryName;
    private String district;
    private CommonTask insertTask;
    private HashMap<String,Integer> resultMap = new HashMap<>();
    private int postId;
    private int memberId;



    public static boolean isIntentAvailable(Context context, Intent intent) {

        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpost);
        SharedPreferences pref = getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        memberId = Integer.valueOf(pref.getString("MemberId",""));
        handleView();



    }


    private void handleView() {

        ibPhoto = findViewById(R.id.ibPhoto);
        btsend = findViewById(R.id.btSend);
        btcancel = findViewById(R.id.btcancel);
        ibPhoto.setImageResource(R.drawable.addimage);

        etComment = findViewById(R.id.etComment);
        tvLocation = findViewById(R.id.tvLocation);
        ivMarker = findViewById(R.id.ivMarker);

        btsend.setOnClickListener(this);
        btcancel.setOnClickListener(this);
        ivMarker.setOnClickListener(this);




    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btSend:

                insert(memberId);
                Bundle bundle = new Bundle();
                bundle.putInt("postId", postId);
                bundle.putString("comment", etComment.getText().toString().trim());
                bundle.putString("location",tvLocation.getText().toString().trim());
                Intent sendIntent = new Intent(this, Mypage_SinglePost_Activity.class);
                sendIntent.putExtras(bundle);
                Toast.makeText(this, "您的貼文已新增成功", Toast.LENGTH_SHORT).show();
                startActivity(sendIntent);
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

    protected void onStart() {
        super.onStart();
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Common.askPermissions(this, permissions, Common.REQ_EXTERNAL_STORAGE);
    }

    public void onPhotoClick(View view) {

        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(NewPost_Activity.this).create();
        alertDialog.show();
        Window win = alertDialog.getWindow();


        win.setContentView(R.layout.alertdialog_layout);

        ImageButton camera_btn = (ImageButton) win.findViewById(R.id.camera);
        camera_btn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {


        switch (requestCode) {
            case Common.REQ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ibPhoto.setEnabled(true);


                } else {
                    ibPhoto.setEnabled(false);
                    Toast.makeText(this, "請同意使用本機相機和讀取權限", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
        }
    }

    public void confirmExit() {


        android.app.AlertDialog.Builder ad = new android.app.AlertDialog.Builder(NewPost_Activity.this)
                .setTitle("確認視窗")
                .setMessage("確定要離開此頁面嗎？")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(NewPost_Activity.this, MainActivity.class);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                    picture = BitmapFactory.decodeStream(getContentResolver().openInputStream(croppedImageUri));
                    Bitmap downSizePicture = Common.downSize(picture, 900);
                    ibPhoto.setImageBitmap(downSizePicture);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    downSizePicture.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    image = out.toByteArray();

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
                        Toast.makeText(this, "請選擇您的位置", Toast.LENGTH_SHORT);
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
            cropIntent.putExtra("outputX", 1000);
            cropIntent.putExtra("outputY", 1000);
            cropIntent.putExtra("scale", true);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, croppedImageUri);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, REQUEST_CROP_PICTURE);
        } catch (ActivityNotFoundException anfe) {
            Toast.makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
        }
    }


    private List<Address> getAddress(Location location) {
        List<Address> result = null;
        try {
            if (location != null) {
                Geocoder gc = new Geocoder(this, Locale.getDefault());
                result = gc.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }




    public void insert(int memberId) {
        if (image == null) {
            Toast.makeText(this, R.string.no_Image, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "請確認欄位是否有空白", Toast.LENGTH_SHORT).show();
            return;
        } else if (Common.networkConnected(this)) {
            String url = Common.URL + "/CpostServlet";
            String comment = etComment.getText().toString().trim();
            NewPost locationTable = new NewPost(countryCode, address, district, longitude, latitude);
            String imageBase64 = Base64.encodeToString(image, Base64.DEFAULT);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "insert");
            jsonObject.addProperty("memberId", memberId);
            jsonObject.addProperty("locationTable",new Gson().toJson(locationTable));
            jsonObject.addProperty("comment", comment);
            jsonObject.addProperty("imageBase64", imageBase64);
            String jsonOut = jsonObject.toString();
            insertTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = insertTask.execute().get();
                Type setType = new TypeToken<HashMap<String,Integer>>(){}.getType();
                resultMap = new Gson().fromJson(jsonIn,setType);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (resultMap==null|| resultMap.isEmpty()) {
                Toast.makeText(this, "insert failed", Toast.LENGTH_SHORT).show();
            } else {
                for(String result : resultMap.keySet())

                    switch (result){
                        case "postId":
                            postId =resultMap.get(result);
                            Log.e("resultMap.postId = ", "resultMap.postId = " + postId);

                            break;
                        case "pictureCount":
                            int pictureCount =resultMap.get(result);
                            if (pictureCount==0){
                                Toast.makeText(this, "insert failed", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case "locationCount":
                            int locationCount =resultMap.get(result);
                            if (locationCount==0){
                                Toast.makeText(this, "insert failed", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        default:
                            return;
                    }
                Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, Mypage_SinglePost_Activity.class);
                startActivity(intent);

            }
        } else

        {
            Toast.makeText(this, getString(R.string.msg_Nonetwork), Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    protected void onStop() {
        super.onStop();
        if (insertTask != null) {
            insertTask.cancel(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


}