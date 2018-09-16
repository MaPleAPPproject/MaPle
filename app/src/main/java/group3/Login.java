package group3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import group3.mypage.ExGoogleMap;

public class Login extends AppCompatActivity {
    private Button btfb, btgplus;
    private Button btlogin, btsignup;
    private EditText etaccount;
    private EditText etpassword;
    //Toby:google
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 10;//設定為讓系統得知是google+登入的
    private static final String TAG ="Login";//define a constant for tag

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btfb = (Button) findViewById(R.id.btfb);
        btgplus = (Button) findViewById(R.id.btgplus);
        btlogin = (Button) findViewById(R.id.btlogin);
        btsignup = (Button) findViewById(R.id.btsignup);
        etaccount = (EditText) findViewById(R.id.etaccount);
        etpassword = (EditText) findViewById(R.id.etpassword);

        btsignup.setOnClickListener(listener);
        btlogin.setOnClickListener(listener);
        btgplus.setOnClickListener(listener);
        btfb.setOnClickListener(listener);

        //Toby:test google+
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,gso);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btlogin:
                    String password = etpassword.getText().toString().trim();
                    String account = etaccount.getText().toString().trim();
                    if ((account.equals("abc")) && (password.equals("123"))) {
                        Toast toast = Toast.makeText(Login.this, "帳號及密碼正確", Toast.LENGTH_SHORT);
                        toast.show();
                        Intent intent = new Intent();
                        intent.setClass(Login.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    } else {
                        Toast toast = Toast.makeText(Login.this, "帳號或密碼錯誤,請重新輸入", Toast.LENGTH_LONG);
                        toast.show();
                        break;
                    }
                case R.id.btsignup: {
                    Intent intent = new Intent();
                    intent.setClass(Login.this, Signup.class);
                    startActivity(intent);
                    break;

                }
                case R.id.btfb: {
                    Intent intent = new Intent();
                    intent.setClass(Login.this, ExGoogleMap.class);
                    startActivity(intent);
                    break;
                }
                case R.id.btgplus:{
                    Intent signInIntent =googleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent,RC_SIGN_IN);

                    break;
                }
            }
        }
    };
    //Toby:test google+
    private void googleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try{
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //Toby:google+ 取得使用者資料
            Log.d(TAG,"handleSignInResult getName:"+account.getDisplayName());
            Log.d(TAG,"handleSignInResult getEmail:"+account.getEmail());
        }catch (ApiException e){
            Log.w(TAG,"signInResult :failed code="+e.getStatusCode());
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Toby:test google+
        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            googleSignInResult(task);

            Toast toast = Toast.makeText(Login.this, "您已從google帳號登入", Toast.LENGTH_LONG);
            toast.show();
        }


    }




}