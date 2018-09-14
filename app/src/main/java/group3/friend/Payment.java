package group3.friend;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.cp102group3maple.violethsu.maple.R;

import java.util.ArrayList;
import java.util.List;

import group3.MainActivity;
import group3.friend.Billing.BillingManager;
import group3.friend.Billing.BillingUpdatesListener;

public class Payment extends AppCompatActivity {
    private ImageButton itPaymentConfirm;
    private RadioGroup rgPaymentType;
    private RadioButton rbGooglePay, rbCreditCard;
    private String path = "";
    private FragmentManager fragmentManager;

    private boolean vipStatus = false;

    private BillingManager mBillingManager;
    private BillingClient mBillingClient;
    private static final String TAG = "Payment";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (vipStatus) {
            setContentView(R.layout.receipt);
            //           receiptFragment();
        } else {
            setContentView(R.layout.activity_payment);
            handleView();
            clickEven();
            pay();

        }
    }

    private void clickEven() {
    }

    private void handleView() {
        itPaymentConfirm = findViewById(R.id.btPayment);
        rgPaymentType = findViewById(R.id.rgPaymentType);
        rbCreditCard = findViewById(R.id.rbCreditCard);
        rbGooglePay = findViewById(R.id.rbGooglePay);

    }

    private void pay() {

        mBillingManager = new BillingManager(this, new MyBillingUpdateListener());
        mBillingManager.initiatePurchaseFlow(mBillingManager.getProduct(), null, BillingClient.SkuType.INAPP);
        //       mBillingManager.consumeAsync(“token”); // comsume item methon
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

                //update ui

            }
        }
    }

}
