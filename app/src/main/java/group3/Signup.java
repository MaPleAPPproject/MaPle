package group3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;;

public class Signup extends AppCompatActivity {
    private static final String TAG = "Signup";
    private AccountTask userExistTask, userRegisterTask;
    private Button btcancel;
    private EditText etsignpw;
    private EditText etemail;
    private boolean userExist = false;
    private Button.OnClickListener listener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btcancel:
                    Intent intent = new Intent();
                    intent.setClass(Signup.this, Login.class);
                    startActivity(intent);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etsignpw = (EditText) findViewById(R.id.etsignpw);
        etemail = (EditText) findViewById(R.id.etemail);
        btcancel = (Button) findViewById(R.id.btcancel);

        etsignpw.setOnClickListener(listener);
        etemail.setOnClickListener(listener);
        btcancel.setOnClickListener(listener);

        etemail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Common.networkConnected(Signup.this)) {
                        String url = Common.URL + "/UserServlet";
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("action", "userExist");
                        jsonObject.addProperty("Email", etemail.getText().toString());
                        String jsonOut = jsonObject.toString();
                        userExistTask = new AccountTask(url, jsonOut);
                        try {
                            String result = userExistTask.execute().get();
                            userExist = Boolean.valueOf(result);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                        // show an error message if the id exists;
                        // otherwise, the error message should be clear
                        if (userExist) {
                            Toast toast = Toast.makeText(Signup.this, "Email已註冊過", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
            }
        });
    }

    public void onsubmitclick(View view) {
        String Email = etemail.getText().toString().trim();
        String PassWord = etsignpw.getText().toString().trim();
        StringBuilder text = new StringBuilder();

        boolean isInputValid = true;
        if (userExist) {
            Toast toast = Toast.makeText(Signup.this, "Email已註冊過", Toast.LENGTH_SHORT);
            toast.show();
            isInputValid = false;
            text.append("Email已註冊過").append("\n");

        }

        if (Email.isEmpty()) {
            Toast toast = Toast.makeText(Signup.this, "Email不可空白", Toast.LENGTH_SHORT);
            toast.show();
            isInputValid = false;


        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etsignpw.getText()).matches()) {
            Toast toasterro = Toast.makeText(Signup.this, "請填寫完整email", Toast.LENGTH_SHORT);
            toasterro.show();
            isInputValid = false;

        }
        if (PassWord.isEmpty()) {
            Toast toast = Toast.makeText(Signup.this, "密碼不可空白", Toast.LENGTH_SHORT);
            toast.show();
            isInputValid = false;

        }

        UserAccount useraccount=new UserAccount(Email,PassWord);
        if (isInputValid) {
            if (Common.networkConnected(Signup.this)) {
                String url = Common.URL + "/UserAccountServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "insert");
                jsonObject.addProperty("useraccount", new Gson().toJson(useraccount));
                String jsonOut = jsonObject.toString();
                userRegisterTask = new AccountTask(url, jsonOut);
                int count = 0;
                try {
                    String result = userRegisterTask.execute().get();
                    count = Integer.valueOf(result);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (count == 0) {
                    Toast toast = Toast.makeText(Signup.this, "註冊失敗,請重新註冊", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    // user ID and password will be saved in the preferences file
                    // and starts UserActivity
                    // while the user account is created successfully
                    SharedPreferences preferences = getSharedPreferences(
                            Common.PREF_FILE, MODE_PRIVATE);
                    preferences.edit().putBoolean("login", true)
                            .putString("Email", Email)
                            .putString("PassWord", PassWord).apply();
                    Toast toast = Toast.makeText(Signup.this, "註冊成功", Toast.LENGTH_LONG);
                    toast.show();
                    Intent intent = new Intent(this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (userExistTask != null) {
            userExistTask.cancel(true);
            userExistTask = null;
        }
        if (userRegisterTask != null) {
            userRegisterTask.cancel(true);
            userExistTask = null;
        }
    }
}
