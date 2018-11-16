package group3;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import group3.mypage.Mypage_UserProfile_Activity;


public class Login extends AppCompatActivity {
    private final static String URL = "http://10.0.2.2:8080/MaPle";
//    private final static String URL = "http://192.168.50.90:8080/MaPle";
    private final static String TAG = "Login";
    String memberId;
    //    private Button btfb, btgplus;
    private Button btlogin, btsignup;
    private EditText etloemail;
    private EditText etpassword;
    private AccountTask userValidTask;
    private Gson gson;
    private long time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

        btlogin = (Button) findViewById(R.id.btlogin);
        btsignup = (Button) findViewById(R.id.btsignup);
        etloemail = (EditText) findViewById(R.id.etloemail);
        etpassword = (EditText) findViewById(R.id.etpassword);

        btsignup.setOnClickListener(listener);
        btlogin.setOnClickListener(listener);

    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btlogin: {
                    hideSoftKeyboard(Login.this,etpassword );
                    String password = etpassword.getText().toString().trim();
                    String email = etloemail.getText().toString().trim();
                    if (email.length() <= 0 || password.length() <= 0) {
                        Toast toast = Toast.makeText(Login.this, "請輸入帳號或密碼", Toast.LENGTH_LONG);
                        toast.show();
                    } else if (isLogin(email, password)) {
                            SharedPreferences preferences = getSharedPreferences(
                                    Common.PREF_FILE, MODE_PRIVATE);
                            preferences.edit()
                                    .putBoolean("login", true)
                                    .putString("MemberId", memberId)
                                    .apply();
                            setResult(RESULT_OK);

                            Intent intent = new Intent();
                            intent.setClass(Login.this, MainActivity.class);
                            startActivity(intent);
//                            Toast.makeText(Login.this, "歡迎使用MaPle", Toast.LENGTH_LONG);
                            finish();
                            break;
                    }
                    break;
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
    protected void onStart() {
        super.onStart();
        String password = etpassword.getText().toString().trim();
        String email = etloemail.getText().toString().trim();
        if (email.length() > 0 || password.length() > 0){
            if (isLogin(email, password)) {
                setResult(RESULT_OK);
                SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE,
                        MODE_PRIVATE);
                boolean login = preferences.getBoolean("login", false);
                if (login) {
                    memberId = preferences.getString("MemberId", "");
                }
            }
        }
    }

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
                    Toast toast = Toast.makeText(Login.this, "查無此帳號", Toast.LENGTH_LONG);
                    toast.show();
                }else if (result.equals("")) {
                    Toast toast = Toast.makeText(Login.this, "伺服器異常", Toast.LENGTH_LONG);
                    toast.show();
                }else{
                    islogin = true;
                    memberId = result;
                }
            } catch (Exception e) {
                connectionError();
            }
        } else {
            connectionError();
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

    private void connectionError() {
        Toast toast = Toast.makeText(Login.this, "連線異常請檢查", Toast.LENGTH_LONG);
        toast.show();
    }

    public void hideSoftKeyboard(Context context, EditText editText) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

    }

}
