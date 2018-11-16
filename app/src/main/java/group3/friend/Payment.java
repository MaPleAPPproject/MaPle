package group3.friend;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.cp102group3maple.violethsu.maple.R;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.android.billingclient.api.PurchaseHistoryResponseListener;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import group3.Common;
import group3.Login;
import group3.friend.Billing.BillingManager;
import group3.friend.Billing.BillingUpdatesListener;
import group3.friend.Billing.OrderListDataType;
import group3.friend.Billing.ServerConnect;
import group3.mypage.CommonTask;
import group3.mypage.ImageTask;
import group3.mypage.User_Profile;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.motion.utils.Oscillator.TAG;

public class Payment implements PurchaseHistoryResponseListener {
    public int vipStatus;
    private BillingManager mBillingManager;
    private static final String TAG = "Payment";
    private ServerConnect serverConnect;
    private int memberId;
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private String urlToOrderList = ServerConnect.URL + "/ReceiptServlet";
    private String userUrl = Common.URL + "/User_profileServlet";
    private Activity activity;
    private String userName;
    private String email;
    private String password;
    private String selfIntroduction;
    private byte[] image;


    private Context context;


    public Payment(Context context, Activity activity) {
        this.activity = activity;
        this.context = context;

        mBillingManager = new BillingManager(activity, new MyBillingUpdateListener());

        SharedPreferences preferences = activity.getSharedPreferences(
                "userAccountDetail", MODE_PRIVATE);
        if (preferences.getString("userMemberId", "")!= null) {
            memberId = Integer.parseInt(preferences.getString("userMemberId", ""));
        }
        defaultVipStatus();
        if (vipStatus == 0) {
            OrderListDataType data = null;
            data = findByID(memberId, urlToOrderList);
            if (data != null) {
                Log.d(TAG, "data = null.");
            } else {
                Log.d(TAG, "Data:" + data);
            }
        }
    }


    private void defaultVipStatus() {

        if (Common.networkConnected(context)) {

            User_Profile userProfiles = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById");
            jsonObject.addProperty("memberId", this.memberId);
            String jsonOut = jsonObject.toString();
            CommonTask getProfileTask = new CommonTask(userUrl, jsonOut);
            Log.d(TAG, jsonOut);
            try {
                String jsonIn = getProfileTask.execute().get();
                Log.d(TAG, jsonIn);
                userProfiles = gson.fromJson(jsonIn, User_Profile.class);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (userProfiles == null) {
                Toast.makeText(context, "no_profile", Toast.LENGTH_SHORT).show();
            } else {
                this.vipStatus = userProfiles.getVipStatus();
                this.userName = userProfiles.getUserName();
                this.selfIntroduction = userProfiles.getSelfIntroduction();
                this.email = userProfiles.getEmail();
                this.password = userProfiles.getPassword();
            }
        }
    }


    public void pay() {
        List<String> skuList = new ArrayList<>();
        if (mBillingManager.getProduct() != null) {

            skuList.add(mBillingManager.getProduct());
            mBillingManager.querySkuDetailsAsync(skuList);
            mBillingManager.initiatePurchaseFlow(mBillingManager.getProduct(), BillingClient.SkuType.INAPP);

        } else {
            Log.w(TAG, "getProduct is null.");
        }
        //       mBillingManager.consumeAsync(“token”); // comsume item methon
    }

    private OrderListDataType findByID(int id, String Url) {
        if (ServerConnect.networkConnected(activity)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById");
            jsonObject.addProperty("id", id);
            String jsonOut = jsonObject.toString();
            serverConnect = new ServerConnect(Url, jsonOut);
            try {

                String jsonIn = serverConnect.execute().get();
                Log.d(TAG, "result:" + jsonIn);

                OrderListDataType result = gson.fromJson(jsonIn, OrderListDataType.class);

                return result;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

        } else {
            ServerConnect.showToast(context, R.string.msg_Nonetwork);
        }
        return null;
    }

    @Override
    public void onPurchaseHistoryResponse(int responseCode, List<Purchase> purchasesList) {

    }


    class MyBillingUpdateListener implements BillingUpdatesListener {
        @Override
        public void onBillingClientSetupFinished() {
            mBillingManager.queryPurchases();


        }

        @Override
        public void onConsumeFinished(String token, int result) {

            if (result == BillingClient.BillingResponse.OK) {
            }
        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchases) {

            for (Purchase p : purchases) {

                if (ServerConnect.networkConnected(activity)) {
                    OrderListDataType data = null;
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "infoInsert");
                    jsonObject.addProperty("id", memberId);
                    data = new OrderListDataType(memberId);
                    jsonObject.addProperty("data", gson.toJson(data));
                    String jsonOut = jsonObject.toString();
                    int count = 0;
                    serverConnect = new ServerConnect(urlToOrderList, jsonOut);
                    try {
                        String result = new ServerConnect(urlToOrderList, jsonObject.toString()).execute().get();
                        count = Integer.valueOf(result);

                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        ServerConnect.showToast(context, R.string.msg_insertFail);
                    } else {
                        ServerConnect.showToast(context, R.string.msg_InsertSuccess);
                    }


                    int imageSize = activity.getResources().getDisplayMetrics().widthPixels / 4;
                    Bitmap bitmap = null;

                    try {
                        bitmap = new ImageTask(userUrl, memberId, imageSize).execute().get();

                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (bitmap != null) {

                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        image = out.toByteArray();

                    } else {
                        Toast.makeText(context, "no_image", Toast.LENGTH_SHORT).show();
//                    ibPhotoIcon.setImageResource(R.drawable.icon_facev);
                    }
                    User_Profile vipStatusUpdate = new User_Profile(memberId, email, password, userName, selfIntroduction, vipStatus);
                    String imageBase64 = Base64.encodeToString(image, Base64.DEFAULT);
                    jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "update");
                    jsonObject.addProperty("memberId", memberId);
                    jsonObject.addProperty("userprofile", new Gson().toJson(vipStatusUpdate));
                    jsonObject.addProperty("imageBase64", imageBase64);
                    int updatecount = 0;
                    try {
                        String result = new CommonTask(userUrl, jsonObject.toString()).execute().get();
                        updatecount = Integer.valueOf(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (updatecount == 0) {
                        Toast.makeText(context, "update failed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "update successful", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    ServerConnect.showToast(context, R.string.msg_Nonetwork);
                }

            }
        }
    }
}
