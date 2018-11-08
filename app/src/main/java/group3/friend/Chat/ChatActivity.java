package group3.friend.Chat;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import group3.Common;
import group3.friend.Friend;
import group3.friend.FriendsListFragment;
import group3.mypage.MypageFragment;
import group3.mypage.Mypage_UserProfile_Activity;
import group3.mypage.User_Profile;

import static group3.friend.Chat.SocketCommon.chatWebSocketClient;


public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ChatActivity";
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_IMAGE = 1;
    private static final int REQ_CROP_PICTURE = 2;
    private static final int REQ_PERMISSIONS_STORAGE = 101;
    private LocalBroadcastManager broadcastManager;
    private EditText etMessage;
    private ScrollView scrollView;
    private LinearLayout layout;
    private ImageView ivCamera, ivPicture, ivSend;
    private String friend;
    private Uri contentUri, croppedImageUri;
    private String memberid;
    private String userName;
    private String friendName;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getUserName();
        setTitle(R.string.textTitle_Chat);
        handleviews();
        broadcastManager = LocalBroadcastManager.getInstance(this);
        registerChatReceiver();
        SharedPreferences pref = this.getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        memberid = pref.getString("MemberId", "");
        activity = this;


        // 取得前頁傳來的聊天對象
        if (getIntent().getStringExtra("friendName") != null) {
            friendName = getIntent().getStringExtra("friendName"); //取得聊天的朋友
        } else {
            friendName = getIntent().getStringExtra("userName");
        }

        if (getIntent().getStringExtra("friend") != null) {
            friend = getIntent().getStringExtra("friend"); //取得聊天的朋友
        } else {

        }
        setTitle("friend: " + friendName);


        // 設定目前聊天對象
        // message不為null代表這頁是由notification開啟，而非由FriendsFragment轉來
        String messageType = getIntent().getStringExtra("messageType");//messageType分辨是文字訊息？圖片訊息？
        if (messageType != null) { //不是空值代表對方先傳訊息，透過通知才點入此聊天室
            String messageContent = getIntent().getStringExtra("messageContent");


            Bundle bundle = getIntent().getExtras();
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.d(TAG, String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
            switch (messageType) {
                case "text":
                    showMessage(friendName, messageContent, true);
                    break;
                case "image":
                    byte[] image = Base64.decode(messageContent, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    showImage(friendName, bitmap, true);
                    break;
                default:
                    break;
            }
        }
    }

    private void getUserName() {
        SharedPreferences preferences = getSharedPreferences(
                "userAccountDetail", MODE_PRIVATE);
        userName = preferences.getString("userName", "");

    }


    private void handleviews() {
        etMessage = findViewById(R.id.etMessage);
        scrollView = findViewById(R.id.scrollView);
        layout = findViewById(R.id.layout);

        ivCamera = findViewById(R.id.ivCamera);
        ivPicture = findViewById(R.id.ivPhoto);
        ivSend = findViewById(R.id.ivSend);
        ivCamera.setOnClickListener(this);
        ivPicture.setOnClickListener(this);
        ivSend.setOnClickListener(this);
    }

    private void registerChatReceiver() {

        IntentFilter closeFilter = new IntentFilter("close");
        FriendStateReceiver friendStateReceiver = new FriendStateReceiver();
        broadcastManager.registerReceiver(friendStateReceiver, closeFilter);

        IntentFilter chatFilter = new IntentFilter("chat");
        ChatReceiver chatReceiver = new ChatReceiver();
        broadcastManager.registerReceiver(chatReceiver, chatFilter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        // 設定目前聊天對象
        ChatWebSocketClient.friendInChat = friend;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {

        }
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            //點擊照相機
            case R.id.ivCamera:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                file = new File(file, "picture.jpg");
                // targeting Android 7.0 (API level 24) and higher,
                // storing images using a FileProvider.
                // passing a file:// URI across a package boundary causes a FileUriExposedException.
                contentUri = FileProvider.getUriForFile(
                        this, getPackageName() + ".provider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

                if (isIntentAvailable(this, intent)) {
                    startActivityForResult(intent, REQ_TAKE_PICTURE);
                } else {
                    showToast(R.string.text_NoCameraApp);
                }
                break;
            //點擊挑照片
            case R.id.ivPhoto:
                intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_PICK_IMAGE);
                break;
            //點擊傳送按鈕
            case R.id.ivSend:
                String message = etMessage.getText().toString();
                if (message.trim().isEmpty()) {
                    showToast(R.string.text_MessageEmpty);
                    return;
                }

                showMessage(userName, message, false); //showMessage是判斷讓訊息左或右，左邊是自己的訊息，右邊是對方的訊息
                // 將輸入的訊息清空
                etMessage.setText(null);
                // 將欲傳送的對話訊息轉成JSON後送出

                ChatMessage chatMessage = new ChatMessage("chat", memberid, userName, friend, message, "text"); //message type可用數字代替更佳
                String chatMessageJson = new Gson().toJson(chatMessage);
                chatWebSocketClient.send(chatMessageJson);
                Log.d(TAG, "output: " + chatMessageJson);
                break;
            default:
                break;
        }
    }


    public boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            int newSize = 400;
            switch (requestCode) {
                // 手機拍照App拍照完成後可以取得照片圖檔
                case REQ_TAKE_PICTURE:
                    Log.d(TAG, "REQ_TAKE_PICTURE: " + contentUri.toString());
                    crop(contentUri);
                    break;
                case REQ_PICK_IMAGE:
                    Uri uri = data.getData();
                    crop(uri);
                    break;
                case REQ_CROP_PICTURE:
                    Log.d(TAG, "REQ_CROP_PICTURE: " + croppedImageUri.toString());
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(croppedImageUri));
                        Bitmap downsizedImage = Common.downSize(bitmap, newSize);

                        showImage(userName, downsizedImage, false);
                        // 將欲傳送的對話訊息轉成JSON後送出
                        String message = Base64.encodeToString(bitmapToPNG(downsizedImage), Base64.DEFAULT);
                        ChatMessage chatMessage = new ChatMessage("chat", memberid, userName, friend, message, "image");
                        String chatMessageJson = new Gson().toJson(chatMessage);
                        chatWebSocketClient.send(chatMessageJson);
                        Log.d(TAG, "output: " + chatMessageJson);
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, e.toString());
                    }
                    break;
            }
        }
    }


    private void crop(Uri sourceImageUri) {
        File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        croppedImageUri = Uri.fromFile(file);
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // the recipient of this Intent can read soruceImageUri's data
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // set image source Uri and type
            cropIntent.setDataAndType(sourceImageUri, "image/*");
            // send crop message
            cropIntent.putExtra("crop", "true");
            // aspect ratio of the cropped area, 0 means user define
            cropIntent.putExtra("aspectX", 0); // this sets the max width
            cropIntent.putExtra("aspectY", 0); // this sets the max height
            // output with and height, 0 keeps original size
            cropIntent.putExtra("outputX", 0);
            cropIntent.putExtra("outputY", 0);
            // whether keep original aspect ratio
            cropIntent.putExtra("scale", true);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, croppedImageUri);
            // whether return data by the intent
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, REQ_CROP_PICTURE);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast.makeText(this, "您的裝置不支援剪裁動作", Toast.LENGTH_SHORT).show();
        }
    }


    private byte[] bitmapToPNG(Bitmap srcBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 轉成PNG不會失真，所以quality參數值會被忽略
        srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    // 接收到聊天訊息會在TextView呈現
    private class ChatReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            ChatMessage chatMessage = new Gson().fromJson(message, ChatMessage.class);
            String sender = chatMessage.getSender();
            String messageType = chatMessage.getMessageType();

            friendName = chatMessage.getSenderName();

            // 接收到聊天訊息，若發送者與目前聊天對象相同，就顯示訊息
//            if(friend == null){
//                friend = sender;
//            }
            if (sender.equals(friend)) {
                switch (messageType) {
                    case "text":
                        showMessage(friendName, chatMessage.getContent(), true);
                        break;
                    case "image":
                        byte[] image = Base64.decode(chatMessage.getContent(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                        showImage(friendName, bitmap, true);
                        break;
                    default:
                        break;
                }

            }
            Log.d(TAG, "received message: " + message);
        }
    }

    private class FriendStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            StateMessage stateMessage = new Gson().fromJson(message, StateMessage.class);
            String type = stateMessage.getType();
            String friended = stateMessage.getUser();
            switch (type) {
                case "close":
                    if (friended.equals(friend)) {
                        activity.finish();
                    }
            }
        }
    }

    /**
     * 將文字訊息呈現在畫面上
     *
     * @param sender  發訊者
     * @param message 訊息內容
     * @param left    true代表訊息要貼在左邊(發訊者為他人)，false代表右邊(發訊者為自己)
     */
    private void showMessage(String sender, String message, boolean left) {
        String text = sender + ": " + message;
        View view;
        // 準備左右2種layout給不同種類發訊者(他人/自己)使用
        if (left) {
            view = View.inflate(this, R.layout.chat_message_left, null);

        } else {
            view = View.inflate(this, R.layout.chat_message_right, null);
        }
        TextView textView = view.findViewById(R.id.textView);
        textView.setText(text);
        layout.addView(view);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }


    private void showImage(String sender, Bitmap bitmap, boolean left) {
        String text = sender + ": ";
        View view;
        if (left) {
            view = View.inflate(this, R.layout.chat_image_left, null);
        } else {
            view = View.inflate(this, R.layout.chat_image_right, null);
        }
        TextView textView = view.findViewById(R.id.textView);
        textView.setText(text);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
        layout.addView(view);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }


    //離開這個頁面
    @Override
    protected void onStop() {
        super.onStop();
        ChatWebSocketClient.friendInChat = null; //friendInChat設為空值
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int stringId) {
        Toast.makeText(this, stringId, Toast.LENGTH_SHORT).show();
    }


}
