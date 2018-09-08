package group3.mypage;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
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
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import group3.Common;

import static android.support.constraint.motion.utils.Oscillator.TAG;

public class Mypage_UserProfile_Activity extends Activity {
    private String name;
    private String email;
    private String password;
    private ImageButton ibPhotoIcon;
    private Button save;
    private ImageButton camera_btn;
    private ImageButton gallery_btn;
    private static final int REQUEST_TAKE_PICTURE = 1;
    private static final int REQUEST_PICK_PICTURE = 2;
    private static final int REQUEST_CROP_PICTURE = 3;
    private static File file;
    Intent intent;
    private Uri contentUri, croppedImageUri;
    private Bitmap picture;


    public Mypage_UserProfile_Activity() {

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        handleView();



    }

    private void handleView() {
        save = findViewById(R.id.btSave);
        ibPhotoIcon = findViewById(R.id.ivPhotoIcon);


    }


    public void onSaveClick(View view) {

        Toast.makeText(this, "saved", Toast.LENGTH_SHORT);
    }

    public void onImageClick(View view) {

        final AlertDialog alertDialog = new AlertDialog.Builder(Mypage_UserProfile_Activity.this).create();
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
                if(alertDialog!=null)
                alertDialog.dismiss();
            }
        });


    }


    public static boolean isIntentAvailable(Context context, Intent intent) {

        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
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
                    this.camera_btn.setEnabled(true);
                    this.gallery_btn.setEnabled(true);

                    Toast.makeText(this, "請同意使用本機相機和讀取權限", Toast.LENGTH_SHORT).show();
                } else {
                    this.camera_btn.setEnabled(false);
                    this.gallery_btn.setEnabled(false);
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

//                    Bitmap srcPicture = BitmapFactory.decodeFile(file.getPath());
//                    Bitmap downSizePicture = Common.downSize(srcPicture, newSize);
//                    ibPhotoIcon.setImageBitmap(downSizePicture);
                    crop(contentUri);
                    break;

                case REQUEST_PICK_PICTURE:
                    Uri uri = intent.getData();
                    if (uri != null) {
                        String[] columns = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(uri, columns, null, null, null);

                        if (cursor != null && cursor.moveToFirst()) {
                            String imagePath = cursor.getString(0);
                            cursor.close();
                            Bitmap srcImage = BitmapFactory.decodeFile(imagePath);
                            Bitmap downSizeImage = Common.downSize(srcImage, newSize);
                            ibPhotoIcon.setImageBitmap(downSizeImage);
                            crop(contentUri);
                            break;


                        }
                    }


                case REQUEST_CROP_PICTURE:


                    Log.d(TAG, "REQ_CROP_PICTURE: " + croppedImageUri.toString());
                    try {

                        picture = BitmapFactory.decodeStream(getContentResolver().openInputStream(croppedImageUri));
                        Bitmap downSizePicture = Common.downSize(picture,512);
                        ibPhotoIcon.setImageBitmap(downSizePicture);

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



