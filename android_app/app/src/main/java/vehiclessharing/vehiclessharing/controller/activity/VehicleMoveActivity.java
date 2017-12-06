package vehiclessharing.vehiclessharing.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.util.HashMap;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.api.GetUserInfo;
import vehiclessharing.vehiclessharing.api.StartTripAPI;
import vehiclessharing.vehiclessharing.authentication.SessionManager;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.model.ConfirmRequest;
import vehiclessharing.vehiclessharing.model.JourneyInfo;
import vehiclessharing.vehiclessharing.model.RequestInfo;
import vehiclessharing.vehiclessharing.model.User;
import vehiclessharing.vehiclessharing.push.CustomFirebaseMessagingService;
import vehiclessharing.vehiclessharing.utils.DrawRoute;
import vehiclessharing.vehiclessharing.utils.Helper;

public class VehicleMoveActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener,
        StartTripAPI.StartTripRequestCallback, GetUserInfo.GetInfoUserCallback {

    private GoogleMap mGoogleMap;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private RequestInfo myRequestInfo, yourRequestInfo;
    private int myUserId;
    private HashMap<String, Marker> listMarker;
    private Location lastLocation = null;
    private Marker mySourceMarker;
    private Location desLocation;
    private LocationManager locationManager;
    private android.location.LocationListener locationListener;
    private ConfirmRequest confirmRequest;
    private FloatingActionButton btnStartTrip, btnEndTrip;
    private String apiToken = "", phone = "";
    private int journeyId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_move);

        Bundle bundle = getIntent().getExtras();
        String dataReceive = bundle.getString(CustomFirebaseMessagingService.DATA_RECEIVE, "");
        journeyId = bundle.getInt("journey_id", 0);
        Gson gson = new Gson();
        confirmRequest = gson.fromJson(dataReceive, ConfirmRequest.class);


        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        sharedPreferences = getSharedPreferences(SessionManager.PREF_NAME_LOGIN, MODE_PRIVATE);
        myUserId = sharedPreferences.getInt(SessionManager.USER_ID, 0);
        apiToken = sharedPreferences.getString(SessionManager.KEY_SESSION, "");
        databaseHelper = new DatabaseHelper(this);

        myRequestInfo = databaseHelper.getRequestInfo(myUserId);
        yourRequestInfo = databaseHelper.getRequestInfoNotMe(myUserId);

        listMarker = new HashMap<>();
        GetUserInfo.getInstance(this).getUserInfoFromAPI(apiToken, confirmRequest.getUserId());

        addControls();
        addEvents();

    }

    private void addEvents() {
        btnStartTrip.setOnClickListener(this);
        btnEndTrip.setOnClickListener(this);
    }

    private void addControls() {
        btnStartTrip = (FloatingActionButton) findViewById(R.id.btnStartStrip);

        btnEndTrip = (FloatingActionButton) findViewById(R.id.btnEndTrip);

        if (journeyId != 0) {
            btnEndTrip.setVisibility(View.VISIBLE);
        } else {
            btnEndTrip.setVisibility(View.GONE);
        }

        if (myRequestInfo.getVehicleType() != 0) {
            btnStartTrip.setVisibility(View.VISIBLE);
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
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);
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
        mGoogleMap.setMyLocationEnabled(true);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("onLocationChanged", "onLocationChanged Location listener");
                if (mySourceMarker != null) {
                    updateMarker(location);
                } /*else {
                    getMyLocation(location);
                }*/
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
        // if(previousLocation)
        //    getMyLocation(myLocation);

        mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                Log.d("onMyLocationChange", "onMyLocationChange");
                if (mySourceMarker != null) {
                    updateMarker(location);
                } /*else {
                    getMyLocation(location);
                }*/
            }
        });
    }

    private void getMyLocation(Location location) {
        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mySourceMarker = mGoogleMap.addMarker(new MarkerOptions().title("Bạn bắt đầu").position(myLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_red_700_36dp)));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 17);
        mGoogleMap.animateCamera(cameraUpdate);
    }

    private void updateMarker(Location location) {
       /* if (mySourceMarker != null) {
            mySourceMarker.remove();
        }*/
        Log.d("onMyLocationChange", "onMyLocationChange update");

        LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mySourceMarker.setPosition(newLatLng);

        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(newLatLng));

    }

    public void setMarkerForUser() {
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
        LatLng yourSourceLatLng = Helper.convertLatLngLocationToLatLng(confirmRequest.getStartLocation());
        LatLng yourDesLatLng = Helper.convertLatLngLocationToLatLng(confirmRequest.getEndLocation());

        Marker yourSourceMarker = mGoogleMap.addMarker(new MarkerOptions().position(yourSourceLatLng).title("Rước").icon(
                BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_indigo_500_24dp)
        ));
        Marker yourDesMarker = mGoogleMap.addMarker(new MarkerOptions().position(yourDesLatLng).title("Đến nơi").icon(
                BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_indigo_500_24dp)
        ));
        listMarker.put("Source " + yourRequestInfo.getUserId(), yourSourceMarker);
        listMarker.put("Des " + yourRequestInfo.getUserId(), yourDesMarker);

        DrawRoute drawRoute = new DrawRoute(this);
        if (myRequestInfo.getVehicleType() == 0) {
            drawRoute.drawroadBetween4Location(mySouceLatLng, yourSourceLatLng, yourDesLatLng, myDestLatLng, 1);
        } else {
            drawRoute.drawroadBetween4Location(yourSourceLatLng, mySouceLatLng, myDestLatLng, yourDesLatLng, 1);
        }
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mySouceLatLng, 17));
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartStrip:
                startTrip();
                break;
            case R.id.btnEndTrip:
                Intent intent = new Intent(this, RatingActivity.class);
                intent.putExtra("journey_id", journeyId);
                startActivity(intent);
        }
    }

    public void startTrip() {
        StartTripAPI.getInstance(this).sendNotiStartTripToUserTogether(apiToken);
    }

    @Override
    public void startTripSuccess(JourneyInfo journeyInfo) {
        Toast.makeText(this, "Gửi thành công, bắt đầu di chuyển thôi", Toast.LENGTH_SHORT).show();

        btnStartTrip.setVisibility(View.GONE);
        journeyId = journeyInfo.getDetail().getJourneyId();
        btnEndTrip.setVisibility(View.VISIBLE);
    }

    @Override
    public void startTripFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getInfoUserSuccess(User userInfo) {
        phone = userInfo.getPhone();
    }

    @Override
    public void getUserInfoFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
