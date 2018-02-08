package vehiclessharing.vehiclessharing.view.activity;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
/*
import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;*/

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.constant.Utils;
import vehiclessharing.vehiclessharing.view.fragment.SigninFragment;


import static vehiclessharing.vehiclessharing.constant.Utils.Login_Fragment;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener{
    public static ProgressDialog mProgress;

    private static FragmentManager fragmentManager;
    private ImageView imgClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        setContentView(R.layout.activity_signin);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.frameContainer, new SigninFragment(),
                            Login_Fragment).commit();
        }

        addControls();
        addEvents();
    }

    private void addEvents() {
        imgClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_activity:
                this.finishAffinity();
                break;
        }
    }

    private void addControls() {
        mProgress =new ProgressDialog(this);
        mProgress.setTitle(Utils.SignIn);
        mProgress.setMessage(Utils.PleaseWait);
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
        //[End] Setup for progress

        imgClose = (ImageView) findViewById(R.id.close_activity);
        //set animation Close
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pendulum);
        imgClose.startAnimation(animation);
    }

    public void replaceLoginFragment() {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                .replace(R.id.frameContainer, new SigninFragment()).commit();
    }

    @Override
    public void onBackPressed() {

        Fragment signUpFragment = fragmentManager
                .findFragmentByTag(Utils.SignUp_Fragment);
        Fragment forgetPasswordFragment = fragmentManager
                .findFragmentByTag(Utils.ForgotPassword_Fragment);

        finishAffinity();
        if (signUpFragment != null)
            replaceLoginFragment();
        else if (forgetPasswordFragment != null)
            replaceLoginFragment();
        else
            super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
     }

    @Override
    public void onStop() {
        super.onStop();
    }
}
