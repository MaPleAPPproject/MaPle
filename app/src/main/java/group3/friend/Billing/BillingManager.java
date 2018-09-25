package group3.friend.Billing;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;


import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.Purchase.PurchasesResult;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;


import java.io.IOException;
import java.util.List;
import java.util.Set;


public class BillingManager implements PurchasesUpdatedListener {

    private Activity mActivity;
    private BillingClient mBillingClient;
    private String product = "vip_status";
    private static final String TAG = "BillingManager";
    private boolean isServiceConnected = false;
    private int mBillingClientResponseCode;
    private BillingUpdatesListener mBillingUpdatesListener;
    private Set<String> mTokensToBeConsumed;
    private static final String BASE_64_ENCODED_PUBLIC_KEY = "IIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAleLT8EKOJxM2M0R2XC5JuksRWAtr/VwVNnyZKdetZVLSgvY4xYW2LlP2b0nJctuQT3IJmtumpeaMug5iCeiCDXfIicddBQEOy4lUmFeCiD6CuB5+qi/hQDDpl/ZZtnuPd3ihMecpOUgHAHHk1hX9IvmI4PISohzspJhjEcgCdcQLDdqtJnMhgcbnc8O86Yn6ZqpmR2+lyNo0i10uDpOENX66d/nB8t6YembAX/urBQWo+bJiCFeZQXMBMwyR1J9LRYx0tJqRccUKrOIi4ybQJJDwzQN3yRaTxf8rMKo78l6/fT2EB9LjtFadw/GrMQWDR6708xhv4aR2tDLpw/3MxQIDAQAB";



    public String getProduct() {
        return product;
    }


    public BillingManager(Activity activity, final BillingUpdatesListener billingUpdatesListener) {

        this.mActivity = activity;
        this.mBillingUpdatesListener = billingUpdatesListener;

        mBillingClient = BillingClient.newBuilder(mActivity).setListener(this).build();
        startServiceConnection(new Runnable() {
            @Override
            public void run() {
                mBillingUpdatesListener.onBillingClientSetupFinished();

            }
        });


    }

    private void startServiceConnection(final Runnable runnable) {

        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(int responseCode) {

                if (responseCode == BillingClient.BillingResponse.OK) {
                    Log.d(TAG, "BillingResponse: " + responseCode);
                    isServiceConnected = true;
                    if (runnable != null) {
                        runnable.run();
                    }
                }
                Log.w(TAG, "BillingResponse: " + responseCode);
                mBillingClientResponseCode = responseCode;

            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.w(TAG, "BillingClient Service 連線終止");
                isServiceConnected = false;
            }
        });

    }


    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {

        if (responseCode == BillingClient.BillingResponse.OK) {

            mBillingUpdatesListener.onPurchasesUpdated(purchases);
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            Log.i(TAG, "onPurchasesUpdated() – user cancelled the purchase flow – skipping");
        } else {
            Log.w(TAG, "onPurchasesUpdated() got unknown resultCode: " + responseCode);
        }

    }

    private void executeServiceRequest(Runnable runnable) {
        if (isServiceConnected) {
            runnable.run();
        } else {
            // If the billing service disconnects, try to reconnect once.
            startServiceConnection(runnable);
        }
    }

    public void queryPurchases() {
        Runnable queryToExecute = new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
                if (areSubscriptionsSupported()) {
                    Purchase.PurchasesResult subscriptionResult
                            = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
                    if (subscriptionResult.getResponseCode() == BillingClient.BillingResponse.OK) {
                        purchasesResult.getPurchasesList().addAll(subscriptionResult.getPurchasesList());
                    } else {
                        Log.w(TAG, "queryPurchases : error");
                        // Handle any error response codes.
                    }
                } else if (purchasesResult.getResponseCode() == BillingClient.BillingResponse.OK) {
                    // Skip subscription purchases query as they are not supported.
                } else {
                    // Handle any other error response codes.
                }
                onQueryPurchasesFinished(purchasesResult);
            }
        };
        executeServiceRequest(queryToExecute);
    }

    private void onQueryPurchasesFinished(Purchase.PurchasesResult result) {
        // Have we been disposed of in the meantime? If so, or bad result code, then quit
        if (mBillingClient == null || result.getResponseCode() != BillingClient.BillingResponse.OK) {
            Log.w(TAG, "Billing client was null or result code (" + result.getResponseCode()
                    + ") was bad – quitting");
            return;
        }

        Log.d(TAG, "Query inventory was successful.");

        // Update the UI and purchases inventory with new list of purchases
        // mPurchases.clear();
        onPurchasesUpdated(BillingClient.BillingResponse.OK, result.getPurchasesList());
    }

    public boolean areSubscriptionsSupported() {
        int responseCode = mBillingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS);
        if (responseCode != BillingClient.BillingResponse.OK) {
            Log.w(TAG, "areSubscriptionsSupported() got an error response: " + responseCode);
        }
        return responseCode == BillingClient.BillingResponse.OK;
    }

    public void initiatePurchaseFlow(final String skuId,
                                     final @BillingClient.SkuType String billingType) {
        Runnable purchaseFlowRequest = new Runnable() {
            @Override
            public void run() {
                BillingFlowParams mParams = BillingFlowParams.newBuilder().
                        setSku(product).setType(skuId).setType(billingType).build();
                mBillingClient.launchBillingFlow(mActivity, mParams);
            }
        };
        executeServiceRequest(purchaseFlowRequest);

    }

    public void consumeAsync(final String purchaseToken) {
        // If we’ve already scheduled to consume this token – no action is needed (this could happen
        // if you received the token when querying purchases inside onReceive() and later from
        // onActivityResult()
        // Generating Consume Response listener
        final ConsumeResponseListener onConsumeListener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@BillingClient.BillingResponse int responseCode, String purchaseToken) {
                // If billing service was disconnected, we try to reconnect 1 time
                // (feel free to introduce your retry policy here).
                mBillingUpdatesListener.onConsumeFinished(purchaseToken, responseCode);
            }
        };

        // Creating a runnable from the request to use it inside our connection retry policy below
        Runnable consumeRequest = new Runnable() {
            @Override
            public void run() {
                // Consume the purchase async
                mBillingClient.consumeAsync(purchaseToken, onConsumeListener);
            }
        };

        executeServiceRequest(consumeRequest);
    }

    public void querySkuDetailsAsync(final List<String> skuList) {
        // Create a runnable from the request to use inside the connection retry policy.

        Runnable queryRequest = new Runnable() {
            @Override
            public void run() {
                // Create the SkuDetailParams object
                SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                // Run the query asynchronously.
                mBillingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                        final List<SkuDetails> skus = skuDetailsList;
                        if (responseCode == BillingClient.BillingResponse.OK && skuDetailsList != null) {
                            for (SkuDetails skuDetails : skuDetailsList) {
                                String sku = skuDetails.getSku();
                                String price = skuDetails.getPrice();
                                String text = "Member ID : " + sku + "\nOrder ID : " + price;

                                Log.d(TAG, text);
                                Log.d(TAG, "onSkuDetailResponse : " + responseCode);
                            }
                        } else {
                            Log.w(TAG, "onSkuDetailResponse Null : " + responseCode);
                        }
                    }
                });
            }
        };

        executeServiceRequest(queryRequest);

    }
    private boolean verifyValidSignature(String signedData, String signature) {
        // Some sanity checks to see if the developer (that's you!) really followed the
        // instructions to run this sample (don't put these checks on your app!)
        if (BASE_64_ENCODED_PUBLIC_KEY.contains("CONSTRUCT_YOUR")) {
            throw new RuntimeException("Please update your app's public key at: "
                    + "BASE_64_ENCODED_PUBLIC_KEY");
        }

        try {
            return Security.verifyPurchase(BASE_64_ENCODED_PUBLIC_KEY, signedData, signature);
        } catch (IOException e) {
            Log.e(TAG, "Got an exception trying to validate a purchase: " + e);
            return false;
        }
    }

}

