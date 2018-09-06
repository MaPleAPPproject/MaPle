package group3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.example.violethsu.maple.R;


public class MyPage_SinglePost_Activity extends Activity {

    private int personIcon;
    private String tvName;
    private int ivPhoto;
    private String tvDescription;
    private Button backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.fragment_mypage);
        super.onCreate(savedInstanceState);
        handleView();
    }

    private void handleView() {
        backButton = findViewById(R.id.btBack);


    }


    public void onBackButtonClick(View view) {
        Intent intent = new Intent(MyPage_SinglePost_Activity.this,MainActivity.class);
        intent.putExtra("id",1);

        startActivity(intent);
    }
}
