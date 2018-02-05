package vehiclessharing.vehiclessharing.view.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.HashMap;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.api.CancelTripAPI;
import vehiclessharing.vehiclessharing.api.EndTheTripAPI;
import vehiclessharing.vehiclessharing.api.SendSOSToAPI;
import vehiclessharing.vehiclessharing.api.StartTripAPI;
import vehiclessharing.vehiclessharing.api.UserInfoAPI;
import vehiclessharing.vehiclessharing.authentication.SessionManager;
import vehiclessharing.vehiclessharing.view.fragment.CancelTripDialog;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.model.ConfirmRequest;
import vehiclessharing.vehiclessharing.model.InfomationUser;
import vehiclessharing.vehiclessharing.model.JourneyInfo;
import vehiclessharing.vehiclessharing.model.RequestInfo;
import vehiclessharing.vehiclessharing.permission.CallPermission;
import vehiclessharing.vehiclessharing.permission.CheckInternetAndLocation;
import vehiclessharing.vehiclessharing.permission.CheckerGPS;
import vehiclessharing.vehiclessharing.push.CustomFirebaseMessagingService;
import vehiclessharing.vehiclessharing.service.AlarmReceiver;
import vehiclessharing.vehiclessharing.utils.DrawRoute;
import vehiclessharing.vehiclessharing.utils.Helper;
import vehiclessharing.vehiclessharing.utils.PlaceHelper;

//import com.amitshekhar.DebugDB;

public class VehicleMoveActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener,
        StartTripAPI.StartTripRequestCallback, UserInfoAPI.GetInfoUserCallback, EndTheTripAPI.EndTripRequestCallback,
        CancelTripAPI.CancelTripInterface, SendSOSToAPI.SendSOSCallback {

    public static String CALL_FROM_WHAT_ACTIVITY = "call_from_what_activity";

    public static String START_TRIP = "start_the_trip";
    public static String ALARM_START = "alarm_start";
    public static String CONFIRM_REQUEST = "confirm_request";
    public static String RECEIVE_CONFIRM_REQUEST = "receive_confirm_request";
    public static String JOURNEY_ID = "journey_id";

    private GoogleMap mGoogleMap;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private RequestInfo myRequestInfo, yourRequestInfo;
    private int myUserId;
    private HashMap<String, Marker> listMarker;
    private Location lastLocation = null;
    private Marker mySourceMarker, hereMarker;
    private Location desLocation;
    private LocationManager locationManager;
    private android.location.LocationListener locationListener;
    private ConfirmRequest confirmRequest;
    private FloatingActionButton btnStartTrip, btnCancelTrip;
    public static FloatingActionButton btnEndTrip;
    private String apiToken = "", phone = "";
    private int journeyId = 0;
    final private static int REQ_PERMISSION = 20;//Value request permission
    private CheckerGPS checkerGPS;
    private String callFrom = "";

    public static AlarmManager alarmManager;
    public static PendingIntent pendingIntent;
    private SharedPreferences sharedPreferencesScreen;
    private SharedPreferences.Editor editorScreen;
    private Button btnSOS;
    private String myAddress = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_move);

        Bundle bundle = getIntent().getExtras();

        String dataReceive = bundle.getString(CustomFirebaseMessagingService.DATA_RECEIVE, "");
        callFrom = bundle.getString(CALL_FROM_WHAT_ACTIVITY, "");
        journeyId = bundle.getInt("journey_id", 0);
        Log.d("journeyId", String.valueOf(journeyId));

        if (!dataReceive.equals("")) {
            Gson gson = new Gson();
            confirmRequest = gson.fromJson(dataReceive, ConfirmRequest.class);
        }

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        sharedPreferences = getSharedPreferences(SessionManager.PREF_NAME_LOGIN, MODE_PRIVATE);
        myUserId = sharedPreferences.getInt(SessionManager.USER_ID, 0);
        apiToken = sharedPreferences.getString(SessionManager.KEY_SESSION, "");
        databaseHelper = new DatabaseHelper(this);


        myRequestInfo = databaseHelper.getRequestInfo(myUserId);
        yourRequestInfo = databaseHelper.getRequestInfoNotMe(myUserId);

        sharedPreferencesScreen = getSharedPreferences(MainActivity.SCREEN_AFTER_BACK, MODE_PRIVATE);

        listMarker = new HashMap<>();
        if (confirmRequest != null && callFrom.equals("receive_confirm_request")) {
        }
        UserInfoAPI userInfoAPI=new UserInfoAPI(this);
               userInfoAPI.getUserInfoFromAPI(apiToken, yourRequestInfo.getUserId());

        checkerGPS = new CheckerGPS(this, this);

        addControls();
        addEvents();
        if (!callFrom.equals(ALARM_START) && !callFrom.equals(START_TRIP)) {
            addAlarm();
        }
        if (journeyId == 0) {
            journeyId = sharedPreferencesScreen.getInt(JOURNEY_ID, 0);
        }
        Log.d("journeyId", String.valueOf(journeyId));
    }

    private void addAlarm() {
        try {
            int hour;
            int minute;
            String[] time;
            if (myRequestInfo.getVehicleType() != 0) {
                time = myRequestInfo.getTimeStart().split(":");
                hour = Integer.parseInt(time[0]);
                minute = Integer.parseInt(time[1]);
            } else {
                time = yourRequestInfo.getTimeStart().split(":");
                hour = Integer.parseInt(time[0]);
                minute = Integer.parseInt(time[1]);
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            Intent myIntent = new Intent(this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    alarmManager.cancel(pendingIntent);
                }
            }, 3000);
        } catch (Exception e) {

        }
    }

    private void addEvents() {
        btnStartTrip.setOnClickListener(this);
        btnCancelTrip.setOnClickListener(this);
        btnEndTrip.setOnClickListener(this);
        btnSOS.setOnClickListener(this);
    }

    private void addControls() {
        btnStartTrip = findViewById(R.id.btnStartStrip);
        btnCancelTrip = findViewById(R.id.btnCancelStrip);
        btnEndTrip = findViewById(R.id.btnEndTrip);
        btnSOS = findViewById(R.id.btnSOS);

        if (callFrom.equals(CONFIRM_REQUEST)) {
            if (VehicleMoveActivity.alarmManager != null && VehicleMoveActivity.pendingIntent != null) {
                VehicleMoveActivity.alarmManager.cancel(VehicleMoveActivity.pendingIntent);
            }
            showStartFloatingButton(true);

            editorScreen = sharedPreferencesScreen.edit();
            editorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.WAIT_START_TRIP);
            editorScreen.commit();

        } else if (callFrom.equals(RECEIVE_CONFIRM_REQUEST)) {
            showStartFloatingButton(true);

            editorScreen = sharedPreferencesScreen.edit();
            editorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.WAIT_START_TRIP);
            editorScreen.commit();
        } else if (callFrom.equals(START_TRIP)) {

            editorScreen = sharedPreferencesScreen.edit();
            editorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.STARTED_TRIP);
            editorScreen.commit();
            showStartFloatingButton(false);
            btnCancelTrip.setVisibility(View.GONE);

        } else {
            int screenName = sharedPreferencesScreen.getInt(MainActivity.SCREEN_NAME, 0);
            if (screenName == MainActivity.WAIT_START_TRIP) {
                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                showStartFloatingButton(true);
            } else if (screenName == MainActivity.STARTED_TRIP) {
                showStartFloatingButton(false);
            } else if (screenName == MainActivity.RATING) {
                finish();
                Intent intent = new Intent(this, RatingActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    private void showStartFloatingButton(boolean show) {
        if (show) {
            btnStartTrip.setVisibility(View.VISIBLE);
            btnCancelTrip.setVisibility(View.VISIBLE);
            btnSOS.setVisibility(View.GONE);
            btnEndTrip.setVisibility(View.GONE);
        } else {
            btnStartTrip.setVisibility(View.GONE);
            btnCancelTrip.setVisibility(View.GONE);
            btnSOS.setVisibility(View.VISIBLE);
            btnEndTrip.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.start_trip, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.call) {
            if (!phone.equals("")) {
                if (CallPermission.checkCall(this, this)) {

                }
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phone));

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            CallPermission.REQUEST_CALL_PHONE);
                }
                startActivity(callIntent);
            } else {
                Toast.makeText(this, "Số điện thoại không có sẵn", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //add source and des marker for me
        if (myRequestInfo != null && myRequestInfo.getSourceLocation() != null && myRequestInfo.getDestLocation() != null) {
            setMarkerForUser();
        }
        setLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setLocation();
                } else {
                    checkerGPS.checkLocationPermission();
                }
                return;
            }
            case CallPermission.REQUEST_CALL_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (phone.equals("")) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + phone));

                        startActivity(callIntent);
                    }
                } else {
                    CallPermission.checkCall(this, this);
                }
        }
    }


    public void setLocation() {

        checkerGPS.checkLocationPermission();

        mGoogleMap.setMyLocationEnabled(true);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("onLocationChanged", "onLocationChanged Location listener");
                if (mySourceMarker != null) {
                    updateMarker(location, false);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 1, locationListener);

        Location myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        getMyLocation(myLocation);
        final boolean[] firstTime = {true};

        mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                Log.d("onMyLocationChange", "onMyLocationChange");
                if (mySourceMarker != null) {
                    updateMarker(location, firstTime[0]);
                    firstTime[0] = false;
                }
            }
        });
    }

    private void getMyLocation(Location location) {
        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        hereMarker = mGoogleMap.addMarker(new MarkerOptions().title("Bạn đang ở đây").position(myLatLng));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 20);
        mGoogleMap.animateCamera(cameraUpdate);
    }

    private void updateMarker(Location location, boolean firstTime) {
        Log.d("onMyLocationChange", "onMyLocationChange update");

        LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        hereMarker.setPosition(newLatLng);
        if (!firstTime) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(newLatLng));
        } else {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, 15));
        }
    }

    public void setMarkerForUser() {
        if (myRequestInfo != null && yourRequestInfo != null) {
            LatLng mySouceLatLng = Helper.convertLatLngLocationToLatLng(myRequestInfo.getSourceLocation());
            LatLng myDestLatLng = Helper.convertLatLngLocationToLatLng(myRequestInfo.getDestLocation());

            desLocation = new Location("DesLocation");
            desLocation.setLatitude(myDestLatLng.latitude);
            desLocation.setLongitude(myDestLatLng.longitude);

            mySourceMarker = mGoogleMap.addMarker(new MarkerOptions().title("Bạn bắt đầu").position(mySouceLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_red_700_36dp)));
            listMarker.put("Source " + String.valueOf(myUserId), mySourceMarker);
            Marker myDesMarker = mGoogleMap.addMarker(new MarkerOptions().position(myDestLatLng).title("Bạn kết thúc ở đây")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_red_700_36dp)));
            listMarker.put("Des " + String.valueOf(myUserId), myDesMarker);

            //add source and des marker for another user
            LatLng yourSourceLatLng = Helper.convertLatLngLocationToLatLng(yourRequestInfo.getSourceLocation());
            LatLng yourDesLatLng = Helper.convertLatLngLocationToLatLng(yourRequestInfo.getDestLocation());

            String markerStart = "", markerEnd = "";
            if (yourRequestInfo.getVehicleType() == 0) {
                markerStart = "Bạn đến đây chở";
            } else {
                markerStart = "Người chở bắt đầu ở đây";
            }

            Marker yourSourceMarker = mGoogleMap.addMarker(new MarkerOptions().position(yourSourceLatLng).title(markerStart).icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_indigo_500_24dp)
            ));
            Marker yourDesMarker = mGoogleMap.addMarker(new MarkerOptions().position(yourDesLatLng).title("Đến nơi").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_indigo_500_24dp)
            ));
            listMarker.put("Source " + yourRequestInfo.getUserId(), yourSourceMarker);
            listMarker.put("Des " + yourRequestInfo.getUserId(), yourDesMarker);

            DrawRoute drawRoute = new DrawRoute(this, mGoogleMap);
            if (myRequestInfo.getVehicleType() != 0) {
                drawRoute.drawroadBetween4Location(mySouceLatLng, yourSourceLatLng, yourDesLatLng, myDestLatLng, 1);
            } else {
                drawRoute.drawroadBetween4Location(yourSourceLatLng, mySouceLatLng, myDestLatLng, yourDesLatLng, 1);
            }

            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mySouceLatLng, 20));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartStrip:
                btnCancelTrip.setVisibility(View.GONE);
                startTrip();
                break;
            case R.id.btnEndTrip: {

                SharedPreferences sharedEndTrip = getSharedPreferences(CustomFirebaseMessagingService.SHARE_PREFER_END_TRIP, MODE_PRIVATE);
                boolean isEndTrip = sharedEndTrip.getBoolean(CustomFirebaseMessagingService.IS_END_TRIP, false);
                endTrip();
                break;
            }
            case R.id.btnCancelStrip:
                cancelTrip();
                break;
            case R.id.btnSOS: {
                sendSOSToServer();
                if (CallPermission.checkCall(this, this)) {

                }
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:113"));

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            CallPermission.REQUEST_CALL_PHONE);
                }
                startActivity(callIntent);

                break;
            }
        }
    }

    private void sendSOSToServer() {
        try {
            myAddress = PlaceHelper.getInstance(this).getCurrentPlace(mGoogleMap);
        } catch (Exception e) {
            Log.d("sendSOSToServer","Error get current location");
        }
        SendSOSToAPI sosToAPI=new SendSOSToAPI(this);
        sosToAPI.sendSOS(apiToken, myAddress, myRequestInfo.getVehicleType());

    }

    private void cancelTrip() {

        android.support.v4.app.DialogFragment dialogFragment;
        dialogFragment = CancelTripDialog.newInstance(MainActivity.sessionId, myRequestInfo.getVehicleType(), this);
        dialogFragment.show(getSupportFragmentManager(), "CancelTrip");

    }

    public void startTrip() {
        StartTripAPI startTripAPI=new StartTripAPI(this);
        startTripAPI.sendNotiStartTripToUserTogether(apiToken, yourRequestInfo.getUserId(), myRequestInfo.getVehicleType());
    }

    public void endTrip() {
        if (CheckInternetAndLocation.isOnline(this)) {

            Log.d("journeyId", "endTrip" + String.valueOf(journeyId));
            if (journeyId != 0) {
                EndTheTripAPI endTheTripAPI=new EndTheTripAPI(this);
                endTheTripAPI.endTheTripWithUserTogether(apiToken, journeyId);

                editorScreen = sharedPreferencesScreen.edit();
                editorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.RATING);
                editorScreen.commit();
            } else {
                Toast.makeText(this, "Có lỗi xảy ra, không thể kết thúc chuyến đi", Toast.LENGTH_SHORT).show();
                moveToMainActiity();
            }
        }
    }

    @Override
    public void startTripSuccess(JourneyInfo journeyInfo) {
        Toast.makeText(this, "Gửi thành công, bắt đầu di chuyển thôi", Toast.LENGTH_SHORT).show();

        btnStartTrip.setVisibility(View.GONE);
        journeyId = journeyInfo.getDetail().getJourneyId();

        Log.d("journeyId", "start Trip success" + String.valueOf(journeyId));
        btnEndTrip.setVisibility(View.VISIBLE);
        btnSOS.setVisibility(View.VISIBLE);

        editorScreen = sharedPreferencesScreen.edit();
        editorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.STARTED_TRIP);
        editorScreen.putInt(JOURNEY_ID, journeyId);
        editorScreen.commit();
    }

    @Override
    public void startTripFailure(String message) {
        Log.d("startTrip", message);

        Log.d("journeyId", "startTripFailureS" + String.valueOf(journeyId));
        editorScreen = sharedPreferencesScreen.edit();
        editorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.STARTED_TRIP);
        if (journeyId != 0) {
            editorScreen.putInt(JOURNEY_ID, journeyId);
        }
        editorScreen.commit();
        showStartFloatingButton(false);
    }

    @Override
    public void getInfoUserSuccess(InfomationUser infomationUser) {
        phone = infomationUser.getUserInfo().getPhone();
        SharedPreferences sharedPreferencesPhone = getSharedPreferences("phone_partner", MODE_PRIVATE);
        SharedPreferences.Editor editorPhone = sharedPreferencesPhone.edit();
        editorPhone.putString("phone_number", phone);
        editorPhone.commit();
        Log.d("phone number: ", phone);
    }

    @Override
    public void getUserInfoFailure(String message) {
        Log.d("getUserInfo", message);
    }

    @Override
    public void endTripSuccess() {
        moveToRatingScreen();
    }

    private void moveToRatingScreen() {
        Intent intent = new Intent(this, RatingActivity.class);
        intent.putExtra("journey_id", journeyId);
        startActivity(intent);
    }

    @Override
    public void endTripFailure(String message) {
        if (message.equals("unsuccessful")) {

        }
        moveToRatingScreen();
        Log.d("endTrip", message);
    }

    @Override
    public void endTripFailureBecauseDanger() {
        Toast.makeText(this, "Chuyến đi đã bị báo nguy hiểm", Toast.LENGTH_SHORT).show();
        moveToMainActiity();
    }

    @Override
    public void cancelTripSuccess() {

        moveToMainActiity();
    }

    @Override
    public void cancelTripFailed() {
        Toast.makeText(this, "Hủy chuyến đi thất bại", Toast.LENGTH_SHORT).show();

        moveToMainActiity();


    }

    private void moveToMainActiity() {
        editorScreen = sharedPreferencesScreen.edit();
        editorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.MAIN_ACTIVITY);
        editorScreen.commit();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    @Override
    public void sendSOSSuccess() {
        editorScreen = sharedPreferencesScreen.edit();
        editorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.MAIN_ACTIVITY);
        editorScreen.commit();
    }

    @Override
    public void sendSOSFailed() {
        sendSOSToServer();
    }
}
