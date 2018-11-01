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
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.EventLog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Member;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;
import group3.Common;
import group3.MainActivity;
import group3.friend.Payment;

import static android.support.constraint.motion.utils.Oscillator.TAG;
import static android.view.KeyEvent.KEYCODE_B;


public class Mypage_UserProfile_Activity extends AppCompatActivity implements View.OnClickListener {


    public static final int KEYCODE_BACK = 4;
    private static final int REQUEST_TAKE_PICTURE = 1;
    private static final int REQUEST_PICK_PICTURE = 2;
    private static final int REQUEST_CROP_PICTURE = 3;
    private final static String DEFAULT_NAME = "";
    private final static String DEFAULT_EMAIL = "";
    private final static String DEFAULT_PASSWORD = "";
    private final static String DEFAULT_SELFINTRO = "";
    private static File file;
    private Button save, cancel, premium;
    private ImageButton camera_btn;
    private ImageButton gallery_btn,ibShowPWET,ibEditName,ibCheck,ibCross ,ibPasswordCheck, ibPasswordCross;
    private Intent intent;
    private Uri contentUri, croppedImageUri;
    private Bitmap picture;
    private EditText etSelfIntro, etdisplayname, etNewPassword, etConfirmPassword;
    private TextView tvVipStatus,tvName,tvPassword;
    private byte[] image;
    private CommonTask uploadTask;
    private User_Profile userprofile;
    private int memberId;
    private CommonTask getProfileTask;
    private ImageTask Icontask;
    private String servlet = "/User_profileServlet",userNameChanged;
    private CircleImageView ibPhotoIcon;
    private TextView tvEmail,tvcharNum,tvchar100;
    private CharSequence temp;
    private int selectionStart;
    private int selectionEnd,maxNum = 100;
    private LinearLayout linearLayoutEditMode,linearLayoutUsernameDisplayMode,linearPasswordEditMode;


    public Mypage_UserProfile_Activity () {

    }

    public static boolean isIntentAvailable(Context context, Intent intent) {

        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_2);
        SharedPreferences pref = getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        memberId = Integer.valueOf(pref.getString("MemberId",""));
        handleView();
        loadProfiles(memberId);
        wordCalculator();



    }

    private void handleView() {
        save = findViewById(R.id.btSave);
        cancel = findViewById(R.id.btCancel);
        tvName = findViewById(R.id.tvtopName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPassword = findViewById(R.id.tvPassword);
        etSelfIntro = findViewById(R.id.etSelfIntro);
        premium = findViewById(R.id.btPremium);
        tvVipStatus = findViewById(R.id.tvStatusResult);
        ibPhotoIcon = findViewById(R.id.dePhotoIcon);
        tvcharNum = findViewById(R.id.tvcharNum);
        tvchar100 = findViewById(R.id.tvchar100);
        linearLayoutEditMode = findViewById(R.id.linearLayoutEditMode);
        linearLayoutEditMode.setVisibility(View.GONE);
        linearLayoutUsernameDisplayMode = findViewById(R.id.linearLayoutUsernameDisplayMode);
        linearPasswordEditMode = findViewById(R.id.linearPasswordEditMode);
        linearPasswordEditMode.setVisibility(View.GONE);
        ibCheck = findViewById(R.id.ibCheck);
        ibCross = findViewById(R.id.ibCross);
        etdisplayname = findViewById(R.id.etdisplayname);
        ibShowPWET = findViewById(R.id.ibShowPWET);
        ibShowPWET.setOnClickListener(this);
        ibEditName = findViewById(R.id.ibEditName);
        ibEditName.setOnClickListener(this);
        ibCheck.setOnClickListener(this);
        ibCross.setOnClickListener(this);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ibPasswordCheck = findViewById(R.id.ibPasswordCheck);
        ibPasswordCheck.setOnClickListener(this);
        ibPasswordCross = findViewById(R.id.ibPasswordCross);
        ibPasswordCross.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.ibShowPWET:

                linearPasswordEditMode.setVisibility(View.VISIBLE);
                etdisplayname.setText(tvName.getText().toString().trim());
                break;

            case R.id.ibEditName:
                linearLayoutEditMode.setVisibility(View.VISIBLE);
                linearLayoutUsernameDisplayMode.setVisibility(View.GONE);
                etdisplayname.setText(tvName.getText().toString().trim());
                break;

            case R.id.ibCheck:

                String userName = etdisplayname.getText().toString().trim();
                if((!userName.isEmpty()) && (userName != null)) {
                    if (userName.length() <= 20) {
                        tvName.setText(userName);
                        linearLayoutEditMode.setVisibility(View.GONE);
                        linearLayoutUsernameDisplayMode.setVisibility(View.VISIBLE);
                        hideSoftKeyboard(Mypage_UserProfile_Activity.this,etdisplayname);
                    } else {
                        Toast.makeText(this, "輸入文字必須小於或等於20字元。", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(Mypage_UserProfile_Activity.this, "您尚未輸入任何字元。", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.ibCross:
                linearLayoutEditMode.setVisibility(View.GONE);
                linearLayoutUsernameDisplayMode.setVisibility(View.VISIBLE);
                hideSoftKeyboard(Mypage_UserProfile_Activity.this,etdisplayname);
                break;

            case R.id.ibPasswordCheck:

                String newPassword = etNewPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();
                tvPassword.setText(newPassword);
                if((!newPassword.isEmpty()) && (!confirmPassword.isEmpty()))   {
                    if (newPassword.equals(confirmPassword)) {
                        if(newPassword.length() <= 20) {

                            tvPassword.setText(newPassword);
                            linearPasswordEditMode.setVisibility(View.GONE);
                            hideSoftKeyboard(Mypage_UserProfile_Activity.this,etdisplayname);
                            etNewPassword.setText("");
                            etConfirmPassword.setText("");
                            etConfirmPassword.setShadowLayer(0, 2, 2, Color.YELLOW);

                        } else { // inputed password is more than 20 characters
                            Toast.makeText(this, "密碼長度必須小於或等於20字元。", Toast.LENGTH_SHORT).show();
                        }

                    } else { // newPassword doesn't equal to confirmPassword
                        Toast.makeText(this, "請檢查輸入的密碼是否一致。", Toast.LENGTH_SHORT).show();
                    }

                } else { // ont of editTexts is empty

                    Toast.makeText(this, "請輸入密碼。", Toast.LENGTH_SHORT).show();
                }


                break;


            case R.id.ibPasswordCross:

                linearPasswordEditMode.setVisibility(View.GONE);
                hideSoftKeyboard(Mypage_UserProfile_Activity.this,etdisplayname);
                etNewPassword.setText("");
                etConfirmPassword.setText("");
                break;

            default:

                break;


        }

    }


    public void wordCalculator() {
        etSelfIntro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int remCharNum = maxNum - s.length();
                tvcharNum.setText("剩餘" + remCharNum + "個字元");


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void afterTextChanged(Editable s) {
                int remCharNum = maxNum - s.length();
                tvcharNum.setText("剩餘" + remCharNum + "個字元");
                selectionStart = etSelfIntro.getSelectionStart();
                selectionEnd = etSelfIntro.getSelectionEnd();
                if(temp.length() > maxNum){
                    s.delete(selectionStart -1, selectionEnd);
                    int tempSelection = selectionStart;
                    etSelfIntro.setText(s);
                    //設定游標到最後
                    etSelfIntro.setSelection(tempSelection);
                }
            }
        });
    }



    private void hideSoftKeyboard(Context context, EditText editText) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

    }



    private void loadProfiles(int memberId) {

        if (Common.networkConnected(this)) {
            String url = Common.URL + "/User_profileServlet";
            User_Profile userProfiles = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById");
            jsonObject.addProperty("memberId", memberId);
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
                tvName.setText(userProfiles.getUserName());
                tvPassword.setText(userProfiles.getPassword());
                tvEmail.setText(userProfiles.getEmail());
                etSelfIntro.setText(userProfiles.getSelfIntroduction());
                int vipStatus = userProfiles.getVipStatus();

                switch(vipStatus){
                    case 0:
                        tvVipStatus.setText(R.string.basic);
                        break;
                    case 1:
                        tvVipStatus.setText(R.string.premium);
                        premium.setVisibility(View.GONE);
                        break;

                }
                int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
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


    public void onCancelClick(View view) {
        confirmExit(MainActivity.class);

    }

    public void onSaveClick(View view) {

        SharedPreferences pref = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        memberId = Integer.parseInt(pref.getString("MemberId", ""));
        updateprofile(memberId);
        String name = tvName.getText().toString();
        String email = tvEmail.getText().toString();
        String password = tvPassword.getText().toString();
        String selfIntro = etSelfIntro.getText().toString();
        pref.edit().putString("name",name).apply();
        pref.edit().putString("email",email).apply();
        pref.edit().putString("password",password).apply();
        pref.edit().putString("selfIntro",selfIntro).apply();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            confirmExit(MainActivity.class);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void confirmExit(final Class<?> cls) {

        AlertDialog.Builder ad = new AlertDialog.Builder(Mypage_UserProfile_Activity.this)
                .setTitle("確認視窗")
                .setMessage("確定要離開此頁面嗎？")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Mypage_UserProfile_Activity.this, cls);
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
        camera_btn.setOnClickListener(this);
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

        confirmExit(Payment.class);

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
                    Log.e(TAG, "選好照片了: " + uri.toString());
                    crop(uri);
                    break;

                case REQUEST_CROP_PICTURE:


                    Log.e(TAG, "REQ_CROP_PICTURE: " + croppedImageUri.toString());
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

        String email = tvEmail.getText().toString().trim();
        String password = tvPassword.getText().toString().trim();
        String userName = tvName.getText().toString().trim();
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
            jsonObject.addProperty("memberId", memberId);
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