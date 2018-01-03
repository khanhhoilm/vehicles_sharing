package vehiclessharing.vehiclessharing.controller.activity;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Calendar;

import co.vehiclessharing.R;
import de.hdodenhof.circleimageview.CircleImageView;
import vehiclessharing.vehiclessharing.api.GetUserInfo;
import vehiclessharing.vehiclessharing.model.User;

public class AnotherUserProfileActivity extends AppCompatActivity implements GetUserInfo.GetInfoUserCallback {

    public static final String USER_ID="user_id";
    private android.support.v7.widget.Toolbar toolbar;
    private CircleImageView imgAvatar;
    private ImageView imgGender;
    private TextView txtName, txtAge, txtPhone;
    private RatingBar ratingBar;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_user_profile);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//DO NOT ROTATE the screen even if the user is shaking his phone like mad

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        Bundle bundle=getIntent().getExtras();
        userId= bundle.getInt(USER_ID, 0);

        addControls();
        addEvents();
        loadUI();
    }

    private void loadUI() {
        GetUserInfo.getInstance(this).getUserInfoFromAPI(MainActivity.sessionId,userId);
    }

    private void addEvents() {
        configToolbar();
        imgAvatar = findViewById(R.id.imgAvatar);
        ratingBar = findViewById(R.id.rbRatingValue);
        txtName = findViewById(R.id.txtUserName);
        txtPhone = findViewById(R.id.txtPhone);
        txtAge = findViewById(R.id.txtAge);
        imgGender=findViewById(R.id.gender);

    }

    private void configToolbar() {
        toolbar = findViewById(R.id.toolbar_General);
        toolbar.setTitle(getString(R.string.history));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Back button", "Back button click");
                finish();
                onBackPressed();
            }
        });
    }

    private void addControls() {

    }

    @Override
    public void getInfoUserSuccess(User userInfo) {
        if(userInfo.getAvatarLink()!=null&&!userInfo.getAvatarLink().equals("")){
            Glide.with(this).load(userInfo.getAvatarLink()).placeholder(getResources().getDrawable(R.drawable.temp)).into(imgAvatar);
        }
        txtName.setText(userInfo.getName());
        txtPhone.setText(userInfo.getPhone());

        if(userInfo.getBirthday()!=null&&!userInfo.getBirthday().equals("")){
            txtAge.setText(caculateAge(userInfo.getBirthday()));
        }
        if(userInfo.getGender()==0){
            imgGender.setImageResource(R.drawable.gender_male);
        }else {
            imgGender.setImageResource(R.drawable.gender_female);
        }
    }

    @Override
    public void getUserInfoFailure(String message) {

    }
    private int caculateAge(String birthday){
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String []year=birthday.split("/");
        int age=currentYear-Integer.parseInt(year[2]);
        return age;
    }
}
