package group3.friend;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.billingclient.api.BillingClient;
import com.example.violethsu.maple.R;

public class Payment extends AppCompatActivity {
    private ImageButton itPaymentConfirm;
    private RadioGroup rgPaymentType;
    private RadioButton rbGooglePay, rbCreditCard;
    private String path = "";
    private FragmentManager fragmentManager;
    //    private AllInOne aio = new AllInOne(path);
    private boolean vipStatus = false;
    private BillingClient billingClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (vipStatus != false) {
            setContentView(R.layout.receipt);
            //           receiptFragment();
        } else {
            setContentView(R.layout.activity_payment);
            handleView();
            clickEven();
            pay();

        }
    }

//    private void receiptFragment() {
//        Fragment fragment = fragmentManager.findFragmentById(R.id.fmlReceipt);
//        if (fragment == null) {
//            BlankFragment receiptFragment = new BlankFragment();
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//
//            transaction.replace(R.id.fmlReceipt, receiptFragment, "fragment");
//
//            transaction.commit();
//        }
//    }

    private void pay() {
//        billingClient = new BillingClient.newBuilder()
//                .setListenter()
//                .build();
    }

    private void clickEven() {
        rgPaymentType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (findViewById(checkedId) == rbCreditCard) {

                } else {

                }
            }
        });
        itPaymentConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void handleView() {
        itPaymentConfirm = findViewById(R.id.btPayment);
        rgPaymentType = findViewById(R.id.rgPaymentType);
        rbCreditCard = findViewById(R.id.rbCreditCard);
        rbGooglePay = findViewById(R.id.rbGooglePay);
    }
}
