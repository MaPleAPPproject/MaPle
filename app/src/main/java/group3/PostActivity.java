package group3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.violethsu.maple.R;

public class PostActivity extends AppCompatActivity {
    private Button btBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_post);
        btBack=(Button) findViewById(R.id.btBack);
        btBack.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PostActivity.this, ExploreActivity.class);
                startActivity(intent);
                PostActivity.this.finish();
            }
});
    }
}