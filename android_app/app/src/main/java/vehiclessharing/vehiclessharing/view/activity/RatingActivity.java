package vehiclessharing.vehiclessharing.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.api.UserInfoAPI;
import vehiclessharing.vehiclessharing.api.RatingUserTogetherAPI;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.model.InfomationUser;
import vehiclessharing.vehiclessharing.model.RequestInfo;
import vehiclessharing.vehiclessharing.permission.CheckInternetAndLocation;
import vehiclessharing.vehiclessharing.utils.PlaceHelper;

public class RatingActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener,
        RatingBar.OnRatingBarChangeListener, RatingUserTogetherAPI.RatingCallback, UserInfoAPI.GetInfoUserCallback {
    private TextView txtName, txtSource, txtDes, txtTime, txtComment;
    private ImageView imgAvatar, imgType;

    private RatingBar ratingBar;
    private Button btnSend;
    private int journeyId;
    private String apiToken = "";
    private DatabaseHelper databaseHelper;
    private RequestInfo yourRequestInfo;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferencesScreen;
    private SharedPreferences.Editor editorScreen;
    private int isFavorite = 0;
    private int timeRating = 0;
    private UserInfoAPI userInfoAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        apiToken = MainActivity.sessionId;
        Bundle bundle = getIntent().getExtras();
        journeyId = bundle.getInt("journey_id", 0);
        sharedPreferencesScreen = getSharedPreferences(MainActivity.SCREEN_AFTER_BACK, MODE_PRIVATE);
        if (journeyId == 0) {
            journeyId = sharedPreferencesScreen.getInt(VehicleMoveActivity.JOURNEY_ID, 0);
        }
        int screen=sharedPreferencesScreen.getInt(MainActivity.SCREEN_NAME,MainActivity.MAIN_ACTIVITY);
        if (journeyId == 0 || screen==MainActivity.MAIN_ACTIVITY) {
            goToMainActivity();
        }else {
            editorScreen = sharedPreferencesScreen.edit();
            editorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.RATING);
            editorScreen.putInt(VehicleMoveActivity.JOURNEY_ID, journeyId);
            editorScreen.commit();
        }
        databaseHelper = new DatabaseHelper(this);
        yourRequestInfo = databaseHelper.getRequestInfoNotMe(MainActivity.userId);


        userInfoAPI=new UserInfoAPI(this);
        addControls();
        if (yourRequestInfo.getSourceLocation()!=null&&yourRequestInfo.getDestLocation()!=null){
            loadUI();
        }
        addEvents();
        editorScreen = sharedPreferencesScreen.edit();
        editorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.RATING);
        editorScreen.commit();
    }

    private void addEvents() {
        btnSend.setOnClickListener(this);
        txtComment.setOnFocusChangeListener(this);
        ratingBar.setOnRatingBarChangeListener(this);
    }

    private void addControls() {
        txtName = findViewById(R.id.txtFullName);
        imgAvatar = findViewById(R.id.imgAvatar);
        txtSource = findViewById(R.id.txtSourceLocation);
        txtDes = findViewById(R.id.txtDesLocation);
        txtTime = findViewById(R.id.txtTimeStartEnd);
        imgType = findViewById(R.id.imgVehicleType);
        ratingBar = findViewById(R.id.rbRating);
        txtComment = findViewById(R.id.txtWriteComment);
        btnSend = findViewById(R.id.btnSendRating);
        progressBar = findViewById(R.id.progressBar);

    }

    private void loadUI() {
        userInfoAPI.getUserInfoFromAPI(MainActivity.sessionId, yourRequestInfo.getUserId());
        try {
            txtSource.setText(PlaceHelper.getInstance(this).getAddressByLatLngLocation(yourRequestInfo.getSourceLocation()));
            txtDes.setText(PlaceHelper.getInstance(this).getAddressByLatLngLocation(yourRequestInfo.getDestLocation()));
            txtTime.setText(yourRequestInfo.getTimeStart());
            switch (yourRequestInfo.getVehicleType()) {
                case 0:
                    imgType.setImageResource(R.drawable.ic_directions_run_indigo_700_24dp);
                    break;
                case 1:
                    imgType.setImageResource(R.drawable.ic_motorcycle_indigo_a700_24dp);
                    break;
                case 2:
                    imgType.setImageResource(R.drawable.ic_directions_car_indigo_700_24dp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnSendRating:
                if (CheckInternetAndLocation.isOnline(this)) {

                    if (journeyId != 0) {
                        btnSend.setEnabled(false);
                        ratingBar.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);
                        RatingUserTogetherAPI ratingUserTogetherAPI=new RatingUserTogetherAPI(this);
                       ratingUserTogetherAPI.rating(apiToken, journeyId, ratingBar.getRating(), txtComment.getText().toString());
                    } else {
                        goToMainActivity();
                    }
                }
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        btnSend.setAlpha(1);
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        btnSend.setAlpha(1);
    }

    @Override
    public void ratingSuccess() {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "rating success", Toast.LENGTH_SHORT).show();

        DatabaseHelper mDatabase=new DatabaseHelper(this);
        mDatabase.deleteAllRequest();
        editorScreen = sharedPreferencesScreen.edit();
        editorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.MAIN_ACTIVITY);

        editorScreen.putInt(VehicleMoveActivity.JOURNEY_ID, 0);
        editorScreen.commit();

        if (ratingBar.getRating() < 4 || isFavorite == 1) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, LikeUserActivity.class);
            intent.putExtra(LikeUserActivity.PARTNER_ID, yourRequestInfo.getUserId());
            startActivity(intent);
        }

    }

    private void goToMainActivity() {
        editorScreen = sharedPreferencesScreen.edit();
        editorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.MAIN_ACTIVITY);
        editorScreen.commit();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void ratingFailure(String message) {
        if (timeRating > 2) {
            goToMainActivity();
        } else {
            progressBar.setVisibility(View.GONE);
            btnSend.setEnabled(true);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getInfoUserSuccess(InfomationUser infomationUser) {
        txtName.setText(infomationUser.getUserInfo().getName());
        if (infomationUser.getUserInfo().getAvatarLink() != null && !infomationUser.getUserInfo().getAvatarLink().equals("")) {
            Glide.with(this).load(infomationUser.getUserInfo().getAvatarLink()).placeholder(R.drawable.temp)
                    .error(R.drawable.temp).centerCrop().into(imgAvatar);
        }
        isFavorite = infomationUser.getIsFavorite();
    }

    @Override
    public void getUserInfoFailure(String message) {
        Log.d("getUserInfo", message);
        userInfoAPI.getUserInfoFromAPI(MainActivity.sessionId, yourRequestInfo.getUserId());

    }
}
