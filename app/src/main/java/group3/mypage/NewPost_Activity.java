package group3.mypage;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.cp102group3maple.violethsu.maple.R;


import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.List;

import group3.Common;
import group3.MainActivity;

import static android.support.constraint.motion.utils.Oscillator.TAG;

public class NewPost_Activity extends Activity implements View.OnClickListener {
    private static final int REQUEST_TAKE_PICTURE = 1;
    private static final int REQUEST_PICK_PICTURE = 2;
    private static final int REQUEST_CROP_PICTURE = 3;

    private ImageButton camera_btn;
    private ImageButton gallery_btn;
    private ImageView ibPhoto;
    private Button send,cancel;

    private File file;
    private Uri contentUri, croppedImageUri;
    private Bitmap picture;
    private Intent intent;



    public static boolean isIntentAvailable(Context context, Intent intent) {

        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpost);
        handleView();




    }


    private void handleView() {

        ibPhoto = findViewById(R.id.ibPhoto);
        send = findViewById(R.id.btSend);
        cancel = findViewById(R.id.btcancel);
        send.setOnClickListener(this);
        cancel.setOnClickListener(this);
        ibPhoto.setImageResource(R.drawable.addimage);

    }




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btSend:
                Intent sendIntent = new Intent(this,Mypage_SinglePost_Activity.class);
                Toast.makeText(this, "Your post successfully created", Toast.LENGTH_SHORT).show();

                break;


            case R.id.btcancel:
                confirmExit();

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

                    Toast.makeText(this, "請同意使用本機相機和讀取權限", Toast.LENGTH_SHORT).show();
                } else {
                 ibPhoto.setEnabled(false);
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


        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case REQUEST_TAKE_PICTURE:
                    crop(contentUri);

                    break;

                case REQUEST_PICK_PICTURE:
                    Uri uri = data.getData();
                    crop(uri);
                    break;
                case REQUEST_CROP_PICTURE:
                    try {
                        picture = BitmapFactory.decodeStream(getContentResolver().openInputStream(croppedImageUri));

                        Bitmap downSizePicture = Common.downSize(picture, 1100);
                        ibPhoto.setImageBitmap(downSizePicture);


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

}

