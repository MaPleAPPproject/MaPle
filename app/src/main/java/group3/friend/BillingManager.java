package group3.friend;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;


import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import java.util.List;

import static android.content.ContentValues.TAG;

public class BillingManager implements PurchasesUpdatedListener {
    Activity mActivity;
    BillingClient mbillingClient;

    public BillingManager(Activity mActivity) {
        this.mActivity = mActivity;
        mbillingClient = BillingClient.newBuilder(mActivity).setListener(this).build();
        mbillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(int responseCode) {
                if (responseCode == BillingClient.BillingResponse.OK) {
                    Log.i(TAG, "onBillingSetupFinished() reponse : " + responseCode);
                } else {
                    Log.w(TAG, "onBillingSetupFinished() errorcode : " + responseCode);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.w(TAG, "onBillingServiceDisconnected()");
            }
        });
    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        Log.d(TAG, "onPurchasesUpdated() responseCode : "+ responseCode);

    }
}
