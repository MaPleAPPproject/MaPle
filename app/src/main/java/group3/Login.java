package group3;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.List;

import group3.mypage.ExGoogleMap;

public class Login extends AppCompatActivity {
    private final static String URL = "http://10.0.2.2:8080/MaPle";
    private final static String TAG = "Login";
    private Button btfb, btgplus;
    private Button btlogin, btsignup;
    private EditText etloemail;
    private EditText etpassword;
    private AccountTask userValidTask;
    private Gson gson;
    private long time = 0;
    String memberid=null;

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btlogin: {
                    String password = etpassword.getText().toString().trim();
                    String email = etloemail.getText().toString().trim();

                        if (email.length() <= 0 || password.length() <= 0) {
                            Toast toast = Toast.makeText(Login.this, "請輸入帳號及密碼", Toast.LENGTH_SHORT);
                            toast.show();
                            break;
                        }

                        if (isLogin(email,password)) {
                            SharedPreferences preferences = getSharedPreferences(
                                    Common.PREF_FILE, MODE_PRIVATE);
                            preferences.edit().putBoolean("login", true)
                                    .putString("MemberId",memberid)
                                    .putString("Email", email)
                                    .putString("PassWord", password)
                                    .apply();
                            setResult(RESULT_OK);

                            Intent intent = new Intent();
                            intent.setClass(Login.this, MainActivity.class);
                            startActivity(intent);
                            Toast toast = Toast.makeText(Login.this, "歡迎使用MaPle", Toast.LENGTH_SHORT);
                            toast.show();
                            finish();
                            break;
                        } else {
                            Toast toast = Toast.makeText(Login.this, "帳號或密碼錯誤", Toast.LENGTH_SHORT);
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

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();



        btfb = (Button) findViewById(R.id.btfb);
        btgplus = (Button) findViewById(R.id.btgplus);
        btlogin = (Button) findViewById(R.id.btlogin);
        btsignup = (Button) findViewById(R.id.btsignup);
        etloemail = (EditText) findViewById(R.id.etloemail);
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
        String Email = preferences.getString("Email", "");
        String PassWord = preferences.getString("PassWord", "");
            boolean login = preferences.getBoolean("login", false);
            if (login) {
                memberid = preferences.getString("MemberId", "");
                if (isLogin(Email, PassWord)) {
                    setResult(RESULT_OK);
                }
            }
    }



//    private boolean networkConnected() {
//        ConnectivityManager conManager =
//                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = conManager != null ? conManager.getActiveNetworkInfo() : null;
//        return networkInfo != null && networkInfo.isConnected();
//    }



    private boolean isLogin(final String Email, final String PassWord) {
        boolean islogin = false;
        if (Common.networkConnected(Login.this)) {
            String url = Common.URL + "/UserAccountServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findByEP");
            jsonObject.addProperty("Email", Email);
            jsonObject.addProperty("PassWord", PassWord);
            String jsonOut = jsonObject.toString();
            userValidTask = new AccountTask(url, jsonOut);
            try {
                String result = userValidTask.execute().get();
                if (result.equals("0")) {
                    Toast toast = Toast.makeText(Login.this, "查無此帳號", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    islogin = true;
                    memberid = result;
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Toast toast = Toast.makeText(Login.this, "連線異常請檢查", Toast.LENGTH_SHORT);
            toast.show();
        }
        return islogin;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        PackageManager pm = getPackageManager();
        ResolveInfo homeInfo = pm.resolveActivity(
                new Intent(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_HOME), 0);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActivityInfo ai = homeInfo.activityInfo;
            Intent startIntent = new Intent(Intent.ACTION_MAIN);
            if ((System.currentTimeMillis() - time > 2000)) {
                Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
                time = System.currentTimeMillis();
                return true;
            } else {
                startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                startIntent
                        .setComponent(new ComponentName(ai.packageName, ai.name));
                startActivitySafely(startIntent);
                return false;
            }
        } else
            return super.onKeyDown(keyCode, event);

    }
    private void startActivitySafely(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "ActivityNotFoundExceptionnull",
                    Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(this, "SecurityExceptionnull", Toast.LENGTH_SHORT)
                    .show();
        }
    }
    @Override
    protected void onStop () {
        super.onStop();
        if (userValidTask != null) {
            userValidTask.cancel(true);
            userValidTask = null;
        }
    }

}