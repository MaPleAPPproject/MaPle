package group3;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.cp102group3maple.violethsu.maple.R;

public class Signup extends AppCompatActivity {
    private Button btcancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btcancel = (Button) findViewById(R.id.btcancel);

        btcancel.setOnClickListener(listener);
    }

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

}
