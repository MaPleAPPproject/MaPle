package group3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cp102group3maple.violethsu.maple.R;

import group3.mypage.ExGoogleMap;

public class Login extends AppCompatActivity {
    private Button btfb, btgplus;
    private Button btlogin, btsignup;
    private EditText etaccount;
    private EditText etpassword;

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
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btlogin:
                    String password = etpassword.getText().toString();
                    String account = etaccount.getText().toString();
                    if ((account.equals("abc")) && (password.equals("123"))) {
                        Toast toast = Toast.makeText(Login.this, "帳號及密碼正確", Toast.LENGTH_LONG);
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
            }
        }
    };
}