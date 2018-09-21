package group3.mypage;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.EventLog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import group3.Common;
import group3.MainActivity;
import group3.friend.Payment;

import static android.support.constraint.motion.utils.Oscillator.TAG;
import static android.view.KeyEvent.KEYCODE_B;


public class Mypage_UserProfile_Activity extends AppCompatActivity {


    public static final int KEYCODE_BACK = 4;
    private static final int REQUEST_TAKE_PICTURE = 1;
    private static final int REQUEST_PICK_PICTURE = 2;
    private static final int REQUEST_CROP_PICTURE = 3;
    private final static String DEFAULT_NAME = "";
    private final static String DEFAULT_EMAIL = "";
    private final static String DEFAULT_PASSWORD = "";
    private final static String DEFAULT_SELFINTRO = "";
    private static File file;
    private ImageButton ibPhotoIcon;
    private Button save, cancel, premium;
    private ImageButton camera_btn;
    private ImageButton gallery_btn;
    private Intent intent;
    private Uri contentUri, croppedImageUri;
    private Bitmap picture;
    private EditText etName, etEmail, etPassword, etSelfIntro;
    private TextView tvVipStatus;
    private byte[] image;
    private CommonTask uploadTask;
    private User_Profile userprofile;
    private int memberId;
    private CommonTask getProfileTask;
    private ImageTask Icontask;
    private String servlet = "/User_profileServlet";


    public Mypage_UserProfile_Activity() {

    }

    public static boolean isIntentAvailable(Context context, Intent intent) {

        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        handleView();
        loadProfiles();


    }

    private void handleView() {
        save = findViewById(R.id.btSave);
        cancel = findViewById(R.id.btCancel);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etSelfIntro = findViewById(R.id.etSelfIntro);
        premium = findViewById(R.id.btPremium);
        ibPhotoIcon = findViewById(R.id.ibPhotoIcon);
        tvVipStatus = findViewById(R.id.tvStatusResult);
    }


    private void loadProfiles() {
        SharedPreferences pf = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        pf.edit()
                .putInt("memberId", memberId).apply();

        memberId = pf.getInt("memberId", 1);
        if (Common.networkConnected(this)) {
            String url = Common.URL + "/User_profileServlet";
            User_Profile userProfiles = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById");
            jsonObject.addProperty("memberId", "2");
            String jsonOut = jsonObject.toString();
            getProfileTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = getProfileTask.execute().get();
                userProfiles = new Gson().fromJson(jsonIn, User_Profile.class);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (userProfiles == null) {
                Toast.makeText(this, "no_profile", Toast.LENGTH_SHORT).show();
            } else {
                etName.setText(userProfiles.getUserName());
                etPassword.setText(userProfiles.getPassword());
                etEmail.setText(userProfiles.getEmail());
                etSelfIntro.setText(userProfiles.getSelfIntroduction());
                int vipStatus = userProfiles.getVipStatus();
                        switch(vipStatus){
                            case 0:
                                tvVipStatus.setText(R.string.basic);
                            case 1:
                                tvVipStatus.setText(R.string.premium);
                                premium.setVisibility(View.GONE);

                        }


//                int memberId  = userProfiles.getMemberID();
                int memberId = 2;
                int imageSize = getResources().getDisplayMetrics().widthPixels / 4;
                Bitmap bitmap = null;

                try {
                    bitmap = new ImageTask(url, memberId, imageSize, ibPhotoIcon).execute().get();

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (bitmap != null) {
                    ibPhotoIcon.setImageBitmap(bitmap);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    image = out.toByteArray();

                } else {
                    Toast.makeText(this, "no_image", Toast.LENGTH_SHORT).show();
//                    ibPhotoIcon.setImageResource(R.drawable.icon_facev);
                }

            }

        } else {
            Toast.makeText(this, "no_profile", Toast.LENGTH_SHORT).show();
        }

    }

    //呼叫會在畫面上顯示各個偏好設定的預設值
    private void restoreDefaults() {
        etName.setText(DEFAULT_NAME);
        etEmail.setText(DEFAULT_EMAIL);
        etPassword.setText(DEFAULT_PASSWORD);
        etSelfIntro.setText(DEFAULT_SELFINTRO);
    }

    public void onCancelClick(View view) {
        confirmExit();

    }

    public void onSaveClick(View view) {

//        SharedPreferences pf = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
//        String name = etName.getText().toString();
//        String email = etEmail.getText().toString();
//        String password = etPassword.getText().toString();
//        String selfIntro = etSelfIntro.getText().toString();
//        pf.edit().putString("name",name).apply();
//        pf.edit().putString("email",email).apply();
//        pf.edit().putString("password",password).apply();
//        pf.edit().putString("selfIntro",selfIntro).apply();
        updateprofile(memberId);


}


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            confirmExit();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    public void confirmExit() {


        AlertDialog.Builder ad = new AlertDialog.Builder(Mypage_UserProfile_Activity.this)
                .setTitle("確認視窗")
                .setMessage("確定要離開此頁面嗎？")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Mypage_UserProfile_Activity.this, MainActivity.class);
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

    public void onImageClick(View view) {

        final AlertDialog alertDialog = new AlertDialog.Builder(Mypage_UserProfile_Activity.this).create();
        alertDialog.show();
        Window win = alertDialog.getWindow();
        win.setContentView(R.layout.alertdialog_layout);

        camera_btn =  win.findViewById(R.id.camera);
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

        gallery_btn = win.findViewById(R.id.gallery);
        gallery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_PICK_PICTURE);
                alertDialog.dismiss();
            }
        });


    }

    public void onPremiumClick(View view) {

        AlertDialog.Builder ad = new AlertDialog.Builder(Mypage_UserProfile_Activity.this)
                .setTitle("確認視窗")
                .setMessage("確定要離開此頁面嗎？")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent premiumIntent = new Intent(Mypage_UserProfile_Activity.this, Payment.class);
                        startActivity(premiumIntent);



                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
        ad.show();
    }

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
                    ibPhotoIcon.setEnabled(true);

                } else {
                    ibPhotoIcon.setEnabled(false);
                    Toast.makeText(this, "請同意使用本機相機和讀取權限", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            int newSize = 512;
            switch (requestCode) {
                case REQUEST_TAKE_PICTURE:
                    crop(contentUri);
                    break;

                case REQUEST_PICK_PICTURE:
                    Uri uri = data.getData();
                    crop(uri);
                    break;

                case REQUEST_CROP_PICTURE:


                    Log.d(TAG, "REQ_CROP_PICTURE: " + croppedImageUri.toString());
                    try {

                        picture = BitmapFactory.decodeStream(getContentResolver().openInputStream(croppedImageUri));
                        Bitmap downSizePicture = Common.downSize(picture, 512);
                        ibPhotoIcon.setImageBitmap(downSizePicture);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        downSizePicture.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        image = out.toByteArray();

                    } catch (FileNotFoundException e) {
                        Log.e(TAG, e.toString());
                    }
                    break;

                default:
            }
        }
    }

    private void crop(Uri srcImageUri) {
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


    public void updateprofile(int memberId){
        if (image == null) {
            Toast.makeText(this, R.string.no_Image, Toast.LENGTH_SHORT).show();
            return;
        }
        memberId = 2;
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String userName = etName.getText().toString().trim();
        int vipStatus;
        if(tvVipStatus.getText().toString().trim().equals("Premium"))
            vipStatus = 1;
        else
            vipStatus = 0;
        String selfIntroduction = etSelfIntro.getText().toString().trim();
        if((email.length()<=0)||(password.length()<=0)||(userName.length()<=0)){

            Toast.makeText(this, "請確認欄位是否有填寫", Toast.LENGTH_SHORT).show();
        }else if (Common.networkConnected(this)) {
            String url = Common.URL + servlet;
            User_Profile newuserprofile = new User_Profile( memberId,email, password, userName,selfIntroduction, vipStatus);
            String imageBase64 = Base64.encodeToString(image, Base64.DEFAULT);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "update");
            jsonObject.addProperty("userprofile", new Gson().toJson(newuserprofile));
            jsonObject.addProperty("imageBase64", imageBase64);
            int count = 0;
            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                count = Integer.valueOf(result);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (count == 0) {
                Toast.makeText(this, "update failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Mypage_UserProfile_Activity.this, MainActivity.class);
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
        if (getProfileTask != null) {
            getProfileTask.cancel(true);
        }

        if (Icontask != null) {
            Icontask.cancel(true);
        }

        if (uploadTask != null) {
            uploadTask.cancel(true);
        }
    }
}




