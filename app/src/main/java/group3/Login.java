package group3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import group3.mypage.ExGoogleMap;

public class Login extends AppCompatActivity {
    private Button btfb, btgplus;
    private Button btlogin, btsignup;
    private EditText etaccount;
    private EditText etpassword;
    private AccountTask userValidTask;
    private final static String TAG="Login";
    private Gson gson;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
//        List<UserAccount> userAccountList=getUserAccount();
//        if (userAccountList == null || userAccountList.isEmpty()) {
//            Toast.makeText(this,"No useraccount Found", Toast.LENGTH_SHORT).show();
//            return;
//        }

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
    }
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        boolean login = preferences.getBoolean("login", false);
        if (login) {
            String account = preferences.getString("EmailId", "");
            String password = preferences.getString("PassWord", "");
            if (isUser(account, password)) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btlogin: {
                    String password = etpassword.getText().toString().trim();
                    String email = etaccount.getText().toString().trim();
                    if(networkConnected()) {
//                        if (account.length() <= 0 || password.length() <= 0) {
//                            Toast toast = Toast.makeText(Login.this, "帳號或密碼錯誤", Toast.LENGTH_SHORT);
//                            toast.show();
//                            break;
//                        }
                        if (isUser(email,password)) {
                            SharedPreferences preferences = getSharedPreferences(
                                    Common.PREF_FILE, MODE_PRIVATE);
                            preferences.edit().putBoolean("login", true)
                                    .putString("Email", email)
                                    .putString("PassWord", password).apply();
                            setResult(RESULT_OK);
                            Intent intent = new Intent();
                            intent.setClass(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        } else {
                            Toast toast = Toast.makeText(Login.this, "帳號或密碼錯誤", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                    else{
                        Toast toast=Toast.makeText(Login.this ,"連線異常請檢查",Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                    }
                }
                case R.id.btsignup: {
                    Intent intent = new Intent();
                    intent.setClass(Login.this, Signup.class);
                    startActivity(intent);
                    break;

                }
//                    case R.id.btfb: {
//                        Intent intent = new Intent();
//                        intent.setClass(Login.this, ExGoogleMap.class);
//                        startActivity(intent);
//                        break;
//                    }
            }
        }
    };
    //        public List<UserAccount> getUserAccount() {
//            List<UserAccount> useraccount = null;
//            if (networkConnected()) {
//                String url = URL + "/UserAccountServlet";
//                String outStr = "";
//                userValidTask = new AccountTask(url, outStr);
//                String inStr = "";
//                try {
//                    inStr = userValidTask.execute().get();
//                    Type listType = new TypeToken<List<UserAccount>>() {
//                    }.getType();
//                    useraccount = gson.fromJson(inStr, listType);
//                } catch (Exception e) {
//                //    Log.e(TAG, e.toString());
//                }
//            }
//            return useraccount;
//        }
    private boolean networkConnected() {
        ConnectivityManager conManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager != null ? conManager.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isConnected();
    }
    private boolean isUser(final String Email, final String PassWord) {
        boolean isUser = false;
        if (Common.networkConnected(Login.this)) {
            String url = Common.URL + "/UserAccountServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "userValid");
            jsonObject.addProperty("Email", Email);
            jsonObject.addProperty("PassWord", PassWord);
            String jsonOut = jsonObject.toString();
            userValidTask= new AccountTask(url, jsonOut);
            try {
                String result = userValidTask.execute().get();
                isUser = Boolean.valueOf(result);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                isUser = false;
            }
        } else {
            Toast.makeText(this,R.string.msg_NoNetwork,Toast.LENGTH_SHORT);
        }
        return isUser;
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (userValidTask != null) {
            userValidTask.cancel(true);
            userValidTask = null;
        }
    }

}