package vehiclessharing.vehiclessharing.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import co.vehiclessharing.R;
import de.hdodenhof.circleimageview.CircleImageView;
import vehiclessharing.vehiclessharing.api.ConfirmRequestAPI;
import vehiclessharing.vehiclessharing.authentication.SessionManager;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.model.ReceiveRequest;
import vehiclessharing.vehiclessharing.model.RequestInfo;
import vehiclessharing.vehiclessharing.permission.CheckInternetAndLocation;
import vehiclessharing.vehiclessharing.push.CustomFirebaseMessagingService;
import vehiclessharing.vehiclessharing.utils.Helper;
import vehiclessharing.vehiclessharing.utils.PlaceHelper;

public class ConfirmRequestActivity extends AppCompatActivity implements View.OnClickListener, ConfirmRequestAPI.ConfirmRequestCallback {
    private ReceiveRequest receiveRequest;
    private Button btnAccept, btnDeny, btnDirect;
    private CircleImageView imgAvatar;
    private TextView txtSourceLocation, txtDestinationLocation, txtUserName, txtTimeStart, txtNote, txtDistance;
    private String apiToken = "";
    private int userId;
    private DatabaseHelper databaseHelper;
    private RequestInfo yourRequestInfo;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferencesScreen;
    private SharedPreferences.Editor editorScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_request);

        Bundle bundle = getIntent().getExtras();

        String dataReceive = bundle.getString(CustomFirebaseMessagingService.DATA_RECEIVE, "");

        if (dataReceive.length() > 0) {
            Gson gson = new Gson();
            receiveRequest = gson.fromJson(dataReceive, ReceiveRequest.class);

        }

        SharedPreferences sharedPreferences = getSharedPreferences(SessionManager.PREF_NAME_LOGIN, MODE_PRIVATE);
        apiToken = sharedPreferences.getString(SessionManager.KEY_SESSION, "");
        userId = sharedPreferences.getInt(SessionManager.USER_ID, 0);

        databaseHelper = new DatabaseHelper(this);

        addControls();
        addEvents();
        loadContent();
        sharedPreferencesScreen = getSharedPreferences(MainActivity.SCREEN_AFTER_BACK, MODE_PRIVATE);

    }

    private void loadContent() {
        RequestInfo myRequestInfo = databaseHelper.getRequestInfo(userId);

        Location myLocation = new Location("MyLocation");
        myLocation.setLatitude(Double.parseDouble(myRequestInfo.getSourceLocation().getLat()));
        myLocation.setLongitude(Double.parseDouble(myRequestInfo.getSourceLocation().getLng()));
        Location anotherLocation = new Location("AnotherLocation");
        anotherLocation.setLatitude(Double.parseDouble(receiveRequest.getStartLocation().getLat()));
        anotherLocation.setLongitude(Double.parseDouble(receiveRequest.getStartLocation().getLng()));

        float distance = Helper.getKiloMeter(myLocation.distanceTo(anotherLocation));

        txtUserName.setText(receiveRequest.getUserName());
        txtTimeStart.setText(receiveRequest.getStartTime());
        txtNote.setText(receiveRequest.getNote());

        if (receiveRequest.getAvartarLink() != null && !receiveRequest.getAvartarLink().equals("")) {
            Glide.with(this).load(receiveRequest.getAvartarLink()).placeholder(getResources().getDrawable(R.drawable.temp)).into(imgAvatar);
        }
        try {
            String sourceLocation = PlaceHelper.getInstance(this).getAddressByLatLngLocation(receiveRequest.getStartLocation());
            txtSourceLocation.setText(sourceLocation);
            String endLocation = PlaceHelper.getInstance(this).getAddressByLatLngLocation(receiveRequest.getEndLocation());
            txtDestinationLocation.setText(endLocation);
            txtDistance.setText("Cách bạn: " + String.valueOf(distance) + " km");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addEvents() {

        btnAccept.setOnClickListener(this);
        btnDeny.setOnClickListener(this);
        imgAvatar.setOnClickListener(this);
        btnDirect.setOnClickListener(this);
    }


    private void addControls() {
        txtUserName = findViewById(R.id.txtUserName);
        txtSourceLocation = findViewById(R.id.txtSource);
        txtDestinationLocation = findViewById(R.id.txtDestination);
        txtTimeStart = findViewById(R.id.txtTimeStart);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnAccept = findViewById(R.id.btnAccept);
        btnDeny = findViewById(R.id.btnDeny);
        txtNote = findViewById(R.id.txtNote);
        txtDistance = findViewById(R.id.txtDistance);
        btnDirect = findViewById(R.id.btnDirect);
        progressBar = findViewById(R.id.progressBar);


        btnAccept.setVisibility(View.VISIBLE);
        btnDeny.setVisibility(View.VISIBLE);
        btnDirect.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnAccept:
                if (CheckInternetAndLocation.isOnline(this)) {
                    progressBar.setVisibility(View.VISIBLE);
                    btnAccept.setEnabled(false);
                    btnDeny.setEnabled(false);
                    sendConfirm(2);
                }
                break;
            case R.id.btnDeny:
                if (CheckInternetAndLocation.isOnline(this)) {
                    progressBar.setVisibility(View.VISIBLE);
                    btnDeny.setEnabled(false);
                    btnDeny.setEnabled(false);
                    sendConfirm(1);
                }
                break;
            case R.id.btnDirect:

                Intent intent = new Intent(this, VehicleMoveActivity.class);

                intent.putExtra(VehicleMoveActivity.CALL_FROM_WHAT_ACTIVITY, VehicleMoveActivity.CONFIRM_REQUEST);
                startActivity(intent);
                break;
        }

    }

    private void sendConfirm(int confirmId) {
        ConfirmRequestAPI confirmRequestAPI = new ConfirmRequestAPI(this);
        confirmRequestAPI.sendConfirmRequest(apiToken, receiveRequest.getUserId(), confirmId);
    }

    @Override
    public void confirmRequestSuccess(int confirmId) {
        progressBar.setVisibility(View.GONE);
        if (confirmId == 2) {
           acceptRequestSuccess();

        } else {
           denyRequestSuccess();
        }

    }
    private void acceptRequestSuccess(){
        Toast.makeText(this, getResources().getString(R.string.request_accept_send_success), Toast.LENGTH_SHORT).show();
        yourRequestInfo = new RequestInfo();
        yourRequestInfo.setUserId(receiveRequest.getUserId());
        yourRequestInfo.setAvatarLink(receiveRequest.getAvartarLink());
        yourRequestInfo.setTimeStart(receiveRequest.getStartTime());
        yourRequestInfo.setSourceLocation(receiveRequest.getStartLocation());
        yourRequestInfo.setDestLocation(receiveRequest.getEndLocation());
        yourRequestInfo.setVehicleType(receiveRequest.getVehicleType());
        if (databaseHelper.insertRequestNotMe(yourRequestInfo, receiveRequest.getUserId())) {
            Log.d("insertRequest", "success");
        }
        Log.d("accept request", "success");

        btnAccept.setVisibility(View.GONE);
        btnDeny.setVisibility(View.GONE);
        btnDirect.setVisibility(View.VISIBLE);
        editorScreen = sharedPreferencesScreen.edit();
        editorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.WAIT_START_TRIP);
        editorScreen.commit();

    }
    private void denyRequestSuccess(){
        editorScreen = sharedPreferencesScreen.edit();
        editorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.ADDED_REQUEST);
        finish();
    }

    @Override
    public void confirmRequestFailure(String message, int confirmId) {
        progressBar.setVisibility(View.GONE);
        if (message.equals("1")) {
            finish();

            SharedPreferences sharedPreferencesScreen = getSharedPreferences(MainActivity.SCREEN_AFTER_BACK, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencesScreen.edit();
            editor.putInt(MainActivity.SCREEN_NAME, MainActivity.ADDED_REQUEST);
            editor.commit();
            Intent intent = new Intent(this, MainActivity.class);

            startActivity(intent);
        }
    }
}
