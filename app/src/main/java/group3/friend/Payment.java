package group3.friend;


import android.app.Activity;
import android.content.Context;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.android.billingclient.api.PurchaseHistoryResponseListener;

import java.util.ArrayList;
import java.util.List;

import group3.Common;
import group3.friend.Billing.BillingManager;
import group3.friend.Billing.BillingUpdatesListener;
import group3.friend.Billing.OrderListDataType;
import group3.friend.Billing.ServerConnect;
import group3.mypage.CommonTask;
import group3.mypage.User_Profile;

public class Payment implements PurchaseHistoryResponseListener {
    public int vipStatus;
    private BillingManager mBillingManager;
    private static final String TAG = "Payment";
    private ServerConnect serverConnect;
    private int memberId;
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private String urlToOrderList = ServerConnect.URL + "/ReceiptServlet";
    private String userUrl = Common.URL + "/User_profileServlet";
    private Activity mActivity;
    private String userName;
    private String email;
    private String password;
    private String selfIntroduction;
    private byte[] image;


    private Context context;


    public Payment(Context context, Activity activity, int memberId) {
        this.mActivity = activity;
        this.context = context;
        this.memberId = memberId;

        mBillingManager = new BillingManager(activity, new MyBillingUpdateListener());

        defaultVipStatus();
        if (vipStatus == 0) {
            OrderListDataType data = null;
            data = findByID(memberId, urlToOrderList);
            if (data != null) {
                Log.d(TAG, "Data:" + data);
            } else {
                Log.d(TAG, "Data = null");
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

    }

    private OrderListDataType findByID(int id, String Url) {
        if (ServerConnect.networkConnected(mActivity)) {
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
                vipStatusUpdate();

            }
        }


    }
    public boolean vipStatusUpdate() {
        boolean isUpdated = false;
        if (ServerConnect.networkConnected(mActivity)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "vipStatusUpdate");
            jsonObject.addProperty("memberId", memberId);
            String jsonOut = jsonObject.toString();

            serverConnect = new ServerConnect(userUrl, jsonOut);
            try {
                String jsonIn = serverConnect.execute().get();
                Log.d(TAG, "result:" + jsonIn);
                if (jsonIn.equals(1)) {
                    isUpdated = true;
                    Fragment currentFragment = mActivity.getFragmentManager().findFragmentByTag("FriendFragment");
                    FragmentTransaction fragmentTransaction = mActivity.getFragmentManager().beginTransaction();
                    fragmentTransaction.detach(currentFragment);
                    fragmentTransaction.attach(currentFragment);
                    fragmentTransaction.commit();
                }

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            ServerConnect.showToast(context, R.string.msg_Nonetwork);
        }
        return isUpdated;
    }
}
