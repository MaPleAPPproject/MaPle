package group3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.violethsu.maple.R;

public class PostActivity extends AppCompatActivity {
    private Button btBack;
    private ImageView imageView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        imageView=findViewById(R.id.ivPhoto);
        btBack=(Button) findViewById(R.id.btBack);
        btBack.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                PostActivity.this.finish();
            }
});

    }
}