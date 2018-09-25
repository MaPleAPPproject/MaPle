package group3.friend;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import group3.friend.Billing.BillingManager;
import group3.friend.Billing.BillingUpdatesListener;
import group3.friend.Billing.OrderListDataType;
import group3.friend.Billing.ServerConnect;
import group3.mypage.CommonTask;
import group3.mypage.ImageTask;
import group3.mypage.User_Profile;

import static android.support.constraint.motion.utils.Oscillator.TAG;

public class Payment extends AppCompatActivity implements PurchaseHistoryResponseListener {
    private ImageButton itPaymentConfirm;
    private RadioGroup rgPaymentType;
    private RadioButton rbGooglePay, rbCreditCard;
    private FragmentManager fragmentManager;
    private TextView tvReceipt;

    private int vipStatus = 0;

    private BillingManager mBillingManager;
    private BillingClient mBillingClient;
    private static final String TAG = "Payment";
    private ServerConnect serverConnect;
    private int memberId = 2;
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private String urlToOrderList = ServerConnect.URL + "/ReceiptServlet";
    private String urlToUserAccount = ServerConnect.URL + "/User_profileServlet";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (false) {
            setContentView(R.layout.receipt);
            //           receiptFragment();
        } else {
            setContentView(R.layout.activity_payment);
            handleView();
            defaultVipStatus();
            clickEven();
            initProgress();
        }
    }

    private void defaultVipStatus() {

        memberId = Integer.parseInt(getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE)
                .getString("MemberId", ""));

        if (Common.networkConnected(this)) {
            String userUrl = Common.URL + "/User_profileServlet";
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
                userProfiles = new Gson().fromJson(jsonIn, User_Profile.class);
                } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (userProfiles == null) {
                Toast.makeText(this, "no_profile", Toast.LENGTH_SHORT).show();
            } else {
                this.vipStatus = userProfiles.getVipStatus();
                switch (vipStatus) {
                    case 0:
                        break;
                    case 1:
                        itPaymentConfirm.setVisibility(View.GONE);
                        break;
                }
            }
        }
    }

    private void handleView() {
        itPaymentConfirm = findViewById(R.id.btPayment);
        rgPaymentType = findViewById(R.id.rgPaymentType);
        rbCreditCard = findViewById(R.id.rbCreditCard);
        rbGooglePay = findViewById(R.id.rbGooglePay);
        rgPaymentType.setVisibility(View.INVISIBLE);

    }

    private void initProgress() {
        List<String> skuList = new ArrayList<>();
        skuList.add(mBillingManager.getProduct());

        mBillingManager.querySkuDetailsAsync(skuList);

    }

    private void clickEven() {
        mBillingManager = new BillingManager(this, new MyBillingUpdateListener());
        itPaymentConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vipStatus == 1) {

                    OrderListDataType data = null;
                    data = findByID(memberId, urlToOrderList);
                    if (data != null) {
                        String text = "Member ID : " + data.getMemberid() + "\nOrder ID : " + data.getOrderid() + "\nOrder Data : " + data.getOrderdate();
                        Log.d(TAG, text);
                        tvReceipt.setText(text);
                    } else {
                        ServerConnect.showToast(Payment.this, R.string.no_data_from_db);
                    }
                } else {
                    List<String> skuList = new ArrayList<>();
                    skuList.add(mBillingManager.getProduct());
                    mBillingManager.querySkuDetailsAsync(skuList);
                    pay();

                }
            }
        });
    }

    private void pay() {
        mBillingManager.initiatePurchaseFlow(mBillingManager.getProduct(), BillingClient.SkuType.INAPP);
        //       mBillingManager.consumeAsync(“token”); // comsume item methon
    }

    private OrderListDataType findByID(int id, String Url) {
        if (ServerConnect.networkConnected(Payment.this)) {
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
            ServerConnect.showToast(Payment.this, R.string.msg_Nonetwork);
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

                if (ServerConnect.networkConnected(Payment.this)) {
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
                        ServerConnect.showToast(Payment.this, R.string.msg_insertFail);
                    } else {
                        ServerConnect.showToast(Payment.this, R.string.msg_InsertSuccess);
                    }
                } else {
                    ServerConnect.showToast(Payment.this, R.string.msg_Nonetwork);
                }

            }
        }
    }

    @Override
    protected void onDestroy() {
        mBillingClient.endConnection();
        super.onDestroy();
    }
}
