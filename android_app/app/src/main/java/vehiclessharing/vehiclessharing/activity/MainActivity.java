package vehiclessharing.vehiclessharing.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//import com.google.firebase.auth.FirebaseAuth;

import vehiclessharing.vehiclessharing.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, SigninActivity.class));
            finish();
       //     return;
        //}
        setContentView(R.layout.activity_main);
    }
}
