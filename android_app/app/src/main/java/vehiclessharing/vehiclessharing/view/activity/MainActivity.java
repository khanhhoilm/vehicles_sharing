package vehiclessharing.vehiclessharing.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.amitshekhar.DebugDB;
import com.bumptech.glide.Glide;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.api.UserInfoAPI;
import vehiclessharing.vehiclessharing.api.CancelRequestAPI;
import vehiclessharing.vehiclessharing.api.UpdateListActiveUserAPI;
import vehiclessharing.vehiclessharing.asynctask.CustomMarkerAsync;
import vehiclessharing.vehiclessharing.asynctask.GetAvatar;
import vehiclessharing.vehiclessharing.authentication.SessionManager;
import vehiclessharing.vehiclessharing.view.fragment.AddRequestFragment;
import vehiclessharing.vehiclessharing.view.fragment.SendRequestFragment;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.model.ActiveUser;
import vehiclessharing.vehiclessharing.model.InfomationUser;
import vehiclessharing.vehiclessharing.model.RequestInfo;
import vehiclessharing.vehiclessharing.model.User;
import vehiclessharing.vehiclessharing.permission.CheckWriteStorage;
import vehiclessharing.vehiclessharing.permission.CheckerGPS;
import vehiclessharing.vehiclessharing.utils.DrawRoute;
import vehiclessharing.vehiclessharing.utils.Helper;
import vehiclessharing.vehiclessharing.api.LogoutAPI;
import vehiclessharing.vehiclessharing.utils.PlaceHelper;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        AddRequestFragment.OnFragmentAddRequestListener, CancelRequestAPI.CancelRequestCallBack,
        SendRequestFragment.SendRequestCallBack, UpdateListActiveUserAPI.UpdateListActiveUserCallback,
        UserInfoAPI.GetInfoUserCallback, GetAvatar.GetBitMapAvatarInterface {

    public final static int ADDED_REQUEST = 1;
    public final static int MAIN_ACTIVITY = 0;
    public final static int WAIT_CONFIRM = 2;
    public final static int CONFIRM_ACCEPT = 3;
    public final static int CONFIRM_DENY = 4;
    public final static int RECEIVE_CONFRIM_REQUEST = 5;
    public final static int WAIT_START_TRIP = 6;
    public final static int STARTED_TRIP = 7;
    public final static int END_TRIP = 8;
    public static final int RATING = 9;
    public static final int BLOCK_USER=10;

    public static String SCREEN_AFTER_BACK = "screen_after_back";
    public static String SCREEN_NAME = "screen_name";
    public static String MY_VEHICLE_TYPE = "my_vehicle_type";
    public static String TIME_SEND_REQUEST = "time_send_request";

    private SharedPreferences sharedPreferencesCheckScreen;
    private SharedPreferences.Editor editorScreen;

    private SessionManager sessionManager;
    private NavigationView navigationView = null;
    private Toolbar toolbar = null;
    private View viewHeader = null;
    private TextView txtFullName, txtPhone;
    public static ImageView imgUser;

    public static GoogleMap mGoogleMap = null;
    public static Polyline polyline = null;
    public static CheckerGPS checkerGPS;
    private User userInfo;
    private Marker myMarker = null;
    private Location previousLocation = null;
    public static HashMap<ActiveUser, Marker> markerHashMap;
    public static HashMap<Integer, Marker> numberMarkerInHashMap;
    public static int userId;
    public static String sessionId = "";
    private boolean changeLocation = false;
    private LatLng mySource, myDes;
    private List<ActiveUser> listActiveUser;
    public static List<Polyline> polylineList;
    public static HashMap<Marker, ActiveUser> userHashMap;
    public Marker anotherDesMarker;
    private DatabaseHelper mDatabase;//
    private boolean doubleBackToExitPressedOnce = false;
    private Location myLocation;
    private FrameLayout frameLayoutMarkerInfo;
    private boolean isSending = false;
    private int vehicleType;
    private boolean isCancel = false;
    private Button btnSendArequestToUser;
    private LocationManager locationManager;
    private android.location.LocationListener locationListener;
    public static boolean btnAddClick = false;

    final public static int REQ_PERMISSION = 20;
    private static String DIRECTION_KEY_API = "AIzaSyAGjxiNRAHypiFYNCN-qcmUgoejyZPtS9c";

    private FloatingActionButton btnFindPeople, btnFindVehicles, btnCancelRequest;
    private int checkOnScreen;
    private UpdateListActiveUserAPI listActiveUserAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(MainActivity.this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (!sessionManager.isLoggedIn()) {
            Intent signIn = new Intent(MainActivity.this, SigninActivity.class);
            startActivity(signIn);
            finish();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        SharedPreferences sharedPreferences = getSharedPreferences(SessionManager.PREF_NAME_LOGIN, MODE_PRIVATE);
        userId = sharedPreferences.getInt(SessionManager.USER_ID, 0);
        sessionId = sharedPreferences.getString(SessionManager.KEY_SESSION, "");
        Log.d("SessionId", "SessionId=" + sessionId);


        sharedPreferencesCheckScreen = getSharedPreferences(SCREEN_AFTER_BACK, MODE_PRIVATE);
        checkOnScreen = sharedPreferencesCheckScreen.getInt(SCREEN_NAME, 0);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        userInfo = databaseHelper.getUser(userId);
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent("Main Activity");

        mDatabase = new DatabaseHelper(this);
        listActiveUserAPI = new UpdateListActiveUserAPI(this);
        addControls();
        addEvents();
        setScreen();

    }

    private void setScreen() {
        checkOnScreen = sharedPreferencesCheckScreen.getInt(SCREEN_NAME, 0);
        Log.d("On Screen", "value: " + checkOnScreen);
        switch (checkOnScreen) {
            case ADDED_REQUEST:
                setViewAddedRequest(ADDED_REQUEST);
                break;
            case WAIT_CONFIRM:
                setViewAddedRequest(WAIT_CONFIRM);
                break;
            case CONFIRM_DENY:
                setViewAddedRequest(CONFIRM_DENY);
                break;
            case WAIT_START_TRIP:
                setViewDirect(WAIT_START_TRIP);
                break;
            case STARTED_TRIP:
                setViewDirect(STARTED_TRIP);
                break;
            case END_TRIP:
                setViewRating(END_TRIP);
                break;
            case RATING:
                setViewRating(RATING);
                break;
            default:
                setMainView();
                break;
        }
    }

    private boolean overFiveMinute() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat sdf1 = new java.text.SimpleDateFormat("HH:mm");
        String currentTime = sdf1.format(calendar.getTime());
        String[] currentHourMinute = currentTime.split(":");
        String timeSendRequest = sharedPreferencesCheckScreen.getString(TIME_SEND_REQUEST, "");
        if (!timeSendRequest.equals("")) {
            String[] hourMinuteSend = timeSendRequest.split(":");
            if(currentHourMinute[0].equals(hourMinuteSend[0])){
                return false;
            }else if(Integer.parseInt(currentHourMinute[1])-Integer.parseInt(hourMinuteSend[1])>5){
                return false;
            }
        }
        return true;
    }

    private void setMainView() {
        showAddRequestButton();
    }

    private void setViewRating(int endTrip) {
        Intent intentRating = new Intent(this, RatingActivity.class);
        startActivity(intentRating);
    }

    private void setViewDirect(int waitStartTrip) {
        Intent intentDirect = new Intent(this, VehicleMoveActivity.class);
        startActivity(intentDirect);
    }

    private void showAddRequestButton() {
        btnCancelRequest.setVisibility(View.GONE);
        btnFindVehicles.setVisibility(View.VISIBLE);
        btnFindPeople.setVisibility(View.VISIBLE);
    }

    private void hideAddRequestButton() {
        btnCancelRequest.setVisibility(View.VISIBLE);
        btnFindVehicles.setVisibility(View.GONE);
        btnFindPeople.setVisibility(View.GONE);
    }

    private void disableAllButton() {
        btnFindPeople.setEnabled(false);
        btnFindVehicles.setEnabled(false);
    }

    private void enableAllButton() {
        btnFindVehicles.setEnabled(true);
        btnFindPeople.setEnabled(true);
    }

    private void setViewAddedRequest(final int addedRequest) {
        hideAddRequestButton();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int myVehicleType = sharedPreferencesCheckScreen.getInt(MY_VEHICLE_TYPE, 3);
                if (myVehicleType != 3) {
                    listActiveUserAPI.getListUpdate(sessionId, myVehicleType);
                }

                isSending = false;
                addMarkerAndDrawRoute();
            }
        }, 1000);

    }

    private void addMarkerAndDrawRoute() {
        RequestInfo requestInfo = mDatabase.getRequestInfo(userId);

        if (requestInfo != null && requestInfo.getSourceLocation() != null && requestInfo.getDestLocation() != null) {
            mySource = Helper.convertLatLngLocationToLatLng(requestInfo.getSourceLocation());
            myDes = Helper.convertLatLngLocationToLatLng(requestInfo.getDestLocation());

            DrawRoute drawRoute = new DrawRoute(this, mGoogleMap);
            drawRoute.setmSubject(0);

            drawRoute.drawroadBetween2Location(mySource, myDes, 0);
            try {
                makeCustomMarkerMyself(mySource, myDes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            btnCancelRequest.setVisibility(View.GONE);
            showAddRequestButton();
            mGoogleMap.clear();
            isCancel = true;
            setLocation();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Nhấn 1 lần nữa để thoát app", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void addEvents() {
        btnFindPeople.setOnClickListener(this);
        btnFindVehicles.setOnClickListener(this);
        btnCancelRequest.setOnClickListener(this);
    }

    private void addControls() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnFindPeople = (FloatingActionButton) findViewById(R.id.btnFindPeople);
        btnFindVehicles = (FloatingActionButton) findViewById(R.id.btnFindVehicle);

        viewHeader = navigationView.getHeaderView(0);

        txtPhone = (TextView) viewHeader.findViewById(R.id.txtPhone);
        txtFullName = (TextView) viewHeader.findViewById(R.id.txtFullName);
        imgUser = (ImageView) viewHeader.findViewById(R.id.imgUser);
        txtPhone.setText(userInfo.getPhone());
        txtFullName.setText(userInfo.getName());

        UserInfoAPI userInfoAPI=new UserInfoAPI(this);
        userInfoAPI.getMyInfo(sessionId);

        checkerGPS = new CheckerGPS(MainActivity.this, this);

        btnCancelRequest = (FloatingActionButton) findViewById(R.id.btnCancelRequest);

        frameLayoutMarkerInfo = (FrameLayout) findViewById(R.id.frameContainerMarker);
        frameLayoutMarkerInfo.setVisibility(View.GONE);

        markerHashMap = new HashMap<>();
        userHashMap = new HashMap<>();
        numberMarkerInHashMap = new HashMap<>();
        listActiveUser = new ArrayList<>();
        polylineList = new ArrayList<>();


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Intent historyIntent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(historyIntent);
        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        LogoutAPI logoutAPI=new LogoutAPI();
        logoutAPI.actionLogout(this, getSupportFragmentManager());
        mDatabase.deleteAll();
        sessionManager.logoutUser();
        editorScreen = sharedPreferencesCheckScreen.edit();
        editorScreen.putInt(SCREEN_NAME, MAIN_ACTIVITY);
        editorScreen.commit();
        finish();
    }

    @Override
    public void onClick(View v) {
        String whatbtnClick = "";
        android.support.v4.app.DialogFragment dialogFragment;
        switch (v.getId()) {
            case R.id.btnFindPeople:

                isCancel = false;
                whatbtnClick = "btnFindPeople";
                disableAllButton();
                try {
                    if (!btnAddClick) {
                        String presentAddress = PlaceHelper.getInstance(this).getAddressByLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
                        dialogFragment = AddRequestFragment.newInstance(whatbtnClick, presentAddress);
                        dialogFragment.show(getSupportFragmentManager(), "From Needer");
                        btnAddClick = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                enableAllButton();
                break;
            case R.id.btnFindVehicle:

                disableAllButton();
                isCancel = false;
                whatbtnClick = "btnFindVehicles";
                try {
                    if (!btnAddClick) {
                        btnAddClick = true;
                        String presentAddress = PlaceHelper.getInstance(this).getAddressByLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));

                        dialogFragment = AddRequestFragment.newInstance(whatbtnClick, presentAddress);
                        dialogFragment.show(getSupportFragmentManager(), "From Grabber");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                enableAllButton();
                break;
            case R.id.btnCancelRequest:

                cancelRequest();
                isCancel = true;
                break;
        }

    }

    private void cancelRequest() {
        CancelRequestAPI relateRequestAPI=new CancelRequestAPI(this);
        relateRequestAPI.cancelRequest(sessionId);

        enableAllButton();
        showAddRequestButton();

    }

    private void hideButtonFindVehicleAndPeople() {
        if (btnFindVehicles.getVisibility() == View.VISIBLE && btnFindPeople.getVisibility() == View.VISIBLE) {
            btnFindPeople.setVisibility(View.GONE);
            btnFindVehicles.setVisibility(View.GONE);
            if (btnCancelRequest.getVisibility() == View.GONE) {
                btnCancelRequest.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mGoogleMap != null) {
                        setLocation();
                    }
                } else {
                    checkerGPS.checkLocationPermission();
                }
                return;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        mGoogleMap.setOnMarkerClickListener(this);
        try {
            if (mGoogleMap != null && checkerGPS.checkLocationPermission()) {
                mGoogleMap.setMyLocationEnabled(true);
                if (checkOnScreen == 0) {
                    setLocation();
                }

            }
        } catch (Exception e) {
            Toast.makeText(this, "Check permission", Toast.LENGTH_SHORT).show();
        }
    }

    private void setLocation() {
        if (checkerGPS.checkLocationPermission()) {
            mGoogleMap.setMyLocationEnabled(true);//Enable mylocation

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            getMyLocation(myLocation);
            final int[] firstTime = {1};

            mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    setLocationChange(location, firstTime[0]);
                    firstTime[0] = 0;
                }
            });
        }
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
            }
        });
    }

    private void setLocationChange(Location location, int firstTime) {
        if (!changeLocation) {
            final double[] longitude = {location.getLongitude()};
            final double[] latitude = {location.getLatitude()};
            LatLng myLatLng = new LatLng(latitude[0], longitude[0]);
            myLocation = location;
            if (!location.equals(previousLocation) && previousLocation != null) {

                if (location.distanceTo(previousLocation) > 20.0 || !location.equals(previousLocation)) {
                    if (myMarker != null) {
                        myMarker.remove();
                    }
                    myMarker = mGoogleMap.addMarker(new MarkerOptions().position(myLatLng).title(getString(R.string.here)));
                    myMarker.setTag("here");
                    CameraUpdate cameraUpdate = null;
                    if (firstTime == 1) {
                        cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 17);
                        mGoogleMap.animateCamera(cameraUpdate);
                    }/* else {
                        cameraUpdate = CameraUpdateFactory.newLatLng(myLatLng);
                    }
*/
                    myLocation = location;
                }
            }
            if (previousLocation == null) {
                previousLocation = location;
                myMarker = mGoogleMap.addMarker(new MarkerOptions().position(myLatLng).title(getString(R.string.here)));
                myMarker.setTag("here");
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 17);
                mGoogleMap.animateCamera(cameraUpdate);
            }
        }

    }

    private void getMyLocation(Location location) {
        if (!changeLocation) {
            final double[] longitude = {location.getLongitude()};
            final double[] latitude = {location.getLatitude()};
            LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            myLocation = location;
            if (!location.equals(previousLocation) && previousLocation != null) {

                if (location.distanceTo(previousLocation) > 20.0 || !location.equals(previousLocation)) {
                    if (myMarker != null) {
                        myMarker.remove();

                    }
                    myMarker = mGoogleMap.addMarker(new MarkerOptions().position(myLatLng).title(getString(R.string.here)));
                    myMarker.setTag("here");

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 15);
                    mGoogleMap.animateCamera(cameraUpdate);
                    myLocation = location;
                }
            }
            if (previousLocation == null) {
                previousLocation = location;
                myMarker = mGoogleMap.addMarker(new MarkerOptions().position(myLatLng).title(getString(R.string.here)));
                myMarker.setTag("here");
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 15);
                mGoogleMap.animateCamera(cameraUpdate);
            }
        }

    }


    private void makeCustomMarkerMyself(final LatLng source, final LatLng destination) throws IOException {
        if (mGoogleMap != null) {
            addMarker(source, destination);
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mGoogleMap != null) {
                        addMarker(source, destination);
                    }
                }
            }, 500);

        }
    }

    private void addMarker(LatLng source, LatLng destination) {
        BitmapDescriptor bitmapSource = BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_red_700_36dp);
        BitmapDescriptor bitmapDestination = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_red_700_36dp);
        try {
            Marker sourceMarker = mGoogleMap.addMarker(new MarkerOptions().position(source).title("Chuyến đi của bạn bắt đầu tại: " + PlaceHelper.getInstance(this).getAddressByLatLng(source)).icon(bitmapSource));
            Marker destinationMarker = mGoogleMap.addMarker(new MarkerOptions().position(destination).title("Chuyến đi kết thúc tại: " + PlaceHelper.getInstance(this).getAddressByLatLng(destination)).icon(bitmapDestination));

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(source, 13);
            mGoogleMap.animateCamera(cameraUpdate);

            sourceMarker.setTag("here");
            destinationMarker.setTag("des");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("onMarkerClick", "success");
        try {
            if (marker != null && marker.getTag() != null) {
                if (!marker.getTag().equals("here") && !marker.getTag().equals("des")) {
                    displayInfoMarkerClick(marker);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    private void displayInfoMarkerClick(Marker marker) throws IOException {

        frameLayoutMarkerInfo.setVisibility(View.VISIBLE);
        btnCancelRequest.setVisibility(View.GONE);
        final TextView txtName, txtSourceLocation, txtDeslocation, txtTime, txtTouch, txtDistance;
        final ImageView imgVehicleType;
        final Button btnsend, btncancel;

        txtName = (TextView) findViewById(R.id.txtFullNameUser);
        txtSourceLocation = (TextView) findViewById(R.id.txtSourceLocationUser);
        txtDeslocation = (TextView) findViewById(R.id.txtDesLocationUser);
        txtTime = (TextView) findViewById(R.id.txtTimeUser);
        imgVehicleType = (ImageView) findViewById(R.id.imgVehicleTypeUser);
        txtDistance = (TextView) findViewById(R.id.txtDistance);
        btnsend = (Button) findViewById(R.id.btnSendRequest);
        btncancel = (Button) findViewById(R.id.btnCancelSendRequest);
        btnSendArequestToUser = btnsend;
        String time = "";
        int vType;


        final ActiveUser anotherUser = userHashMap.get(marker);

        if (anotherUser != null) {
            time = anotherUser.getRequestInfo().getTimeStart();
            vType = anotherUser.getRequestInfo().getVehicleType();
            LatLng sourceLocation = Helper.convertLatLngLocationToLatLng(anotherUser.getRequestInfo().getSourceLocation());
            LatLng desLocation = Helper.convertLatLngLocationToLatLng(anotherUser.getRequestInfo().getDestLocation());

            String sourceAddress = PlaceHelper.getInstance(this).getAddressByLatLng(sourceLocation);

            String destinationAddress = PlaceHelper.getInstance(this).getAddressByLatLng(desLocation);
            String name = anotherUser.getUserInfo().getName();
            txtName.setText(name);
            txtSourceLocation.setText(sourceAddress);
            txtDeslocation.setText(destinationAddress);
            txtTime.setText(time);

            if (isSending) {
                btnsend.setVisibility(View.GONE);
                btnsend.setAlpha(0.5f);
            } else {
                btnsend.setVisibility(View.VISIBLE);
                btnsend.setAlpha(1.0f);
            }

            switch (vType) {
                case 0:
                    imgVehicleType.setImageResource(R.drawable.ic_accessibility_indigo_700_24dp);
                    break;
                case 2:
                    imgVehicleType.setImageResource(R.drawable.ic_directions_car_indigo_700_24dp);
                    break;
                case 1:
                    imgVehicleType.setImageResource(R.drawable.ic_motorcycle_indigo_a700_24dp);
                    break;
            }
            if (DrawRoute.polylineNotCurUser != null) {
                DrawRoute.polylineNotCurUser.remove();
            }
            BitmapDescriptor bitmapDestination = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_drop_indigo_500_24dp);
            if (anotherDesMarker != null) {
                anotherDesMarker.remove();
            }
            anotherDesMarker = mGoogleMap.addMarker(new MarkerOptions().position(desLocation).title("Chuyến đi của " + anotherUser.getUserInfo().getName() + " kết thúc tại đây ").icon(bitmapDestination));
            DrawRoute draw = new DrawRoute(this, mGoogleMap);
            if (polylineList.size() > 1) {
                polylineList.get(1).remove();

            }
            if (vType == 0) {
                draw.drawroadBetween4Location(mySource, sourceLocation, desLocation, myDes, 1);
            } else {
                draw.drawroadBetween4Location(sourceLocation, mySource, myDes, desLocation, 1);
            }

            Location location = new Location(sourceAddress);
            location.setLatitude(sourceLocation.latitude);
            location.setLongitude(sourceLocation.longitude);
            if (myLocation == null) {
                myLocation = new Location("MyLocation");
                myLocation.setLatitude(mySource.latitude);
                myLocation.setLongitude(mySource.longitude);
            }
            float distance = Helper.getKiloMeter(myLocation.distanceTo(location));
            txtDistance.setText(String.valueOf(distance) + " km");

            btnsend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmSureSendRequestToAnotherUser(anotherUser);

                }
            });
            btncancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frameLayoutMarkerInfo.setVisibility(View.GONE);
                    btnCancelRequest.setVisibility(View.VISIBLE);
                }
            });
            txtName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intenSeeProfile = new Intent(MainActivity.this, AnotherUserProfileActivity.class);
                    intenSeeProfile.putExtra(AnotherUserProfileActivity.USER_ID, anotherUser.getUserInfo().getId());
                    startActivity(intenSeeProfile);
                }
            });
        }
    }

    private void confirmSureSendRequestToAnotherUser(ActiveUser userIsChosen) {
        android.support.v4.app.DialogFragment dialogFragment;
        dialogFragment = SendRequestFragment.newInstance(sessionId, userIsChosen.getUserInfo().getId(), this);
        dialogFragment.show(getSupportFragmentManager(), "SendRequest");

    }

    @Override
    public void addRequestSuccess(LatLng cur, LatLng des, String time, final int type, List<ActiveUser> list) {
        try {
            isCancel = false;


            vehicleType = type;
            String requestName = "";
            if (type == 0) {
                requestName = "Bạn đã tạo thành công yêu cầu tìm xe";
            } else {
                requestName = "Bạn đã tạo thành công yêu cầu cho đi nhờ xe";
            }
            Toast.makeText(this, requestName, Toast.LENGTH_SHORT).show();

            frameLayoutMarkerInfo.setVisibility(View.GONE);
            btnCancelRequest.setVisibility(View.VISIBLE);

            if (list != null && list.size() > 0) {
                listActiveUser = list;
            }
            changeLocation = true;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(cur, 17);
            mGoogleMap.animateCamera(cameraUpdate);

            try {
                makeCustomMarkerMyself(cur, des);
            } catch (IOException e) {
                e.printStackTrace();
            }
            DrawRoute drawRoute = new DrawRoute(this, mGoogleMap);
            drawRoute.setmSubject(0);

            drawRoute.drawroadBetween2Location(cur, des, 0);
            mySource = cur;
            myDes = des;

            hideButtonFindVehicleAndPeople();
            if (list.size() > 0) {
                if (numberMarkerInHashMap.size() > 0) {
                    numberMarkerInHashMap.clear();
                }
                for (int i = 0; i < list.size(); i++) {
                    ActiveUser activeUser = list.get(i);
                    new CustomMarkerAsync(this, i).execute(activeUser);
                }
            }
            RequestInfo requestInfo = new RequestInfo();
            requestInfo.setVehicleType(type);
            requestInfo.setTimeStart(time);
            requestInfo.setSourceLocation(Helper.convertLatLngToLatLngLocation(cur));
            requestInfo.setDestLocation(Helper.convertLatLngToLatLngLocation(des));

            CheckWriteStorage.getInstance(this, this).isStoragePermissionGranted();
            mDatabase.insertRequest(requestInfo, userId);
            myLocation = new Location("MyLocation");
            myLocation.setLatitude(cur.latitude);
            myLocation.setLongitude(cur.longitude);
        } catch (Exception e) {
            Toast.makeText(this, "addRequestSuccess interface error", Toast.LENGTH_SHORT).show();
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listActiveUserAPI.getListUpdate(sessionId, vehicleType);
            }
        }, 30000);

        editorScreen = sharedPreferencesCheckScreen.edit();
        editorScreen.putInt(SCREEN_NAME, ADDED_REQUEST);
        editorScreen.putInt(MY_VEHICLE_TYPE, type);
        editorScreen.commit();
        checkOnScreen = ADDED_REQUEST;

    }

    @Override
    public void addRequestFailure() {
        Toast.makeText(this, "Tạo yêu cầu thất bại", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cancelRequestSuccess(boolean success) {
        isSending = false;

        showAddRequestButton();
        enableAllButton();
        btnCancelRequest.setEnabled(true);

        if (listActiveUser.size() > 0) {
            for (ActiveUser user : listActiveUser) {
                if (markerHashMap.containsKey(user)) {
                    Marker markerRm = markerHashMap.get(user);
                    markerRm.remove();
                }
            }
            markerHashMap.clear();
        }
        mGoogleMap.clear();
        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                getMyLocation(location);
                Log.d("onLocationChanged", "onLocationChanged");
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        mDatabase.deleteRequest(userId);
        setLocation();

        SharedPreferences.Editor editor = sharedPreferencesCheckScreen.edit();
        editor.putInt(SCREEN_NAME, MAIN_ACTIVITY);
        editor.commit();
    }

    @Override
    public void cancelRequestFailed() {

        showAddRequestButton();
        btnCancelRequest.setEnabled(true);
        enableAllButton();
        try {
            if (listActiveUser.size() > 0) {
                for (ActiveUser user : listActiveUser) {
                    if (markerHashMap.containsKey(user)) {
                        Marker markerRm = markerHashMap.get(user);
                        markerRm.remove();
                    }
                }
                markerHashMap.clear();
            }
            mGoogleMap.clear();
            locationListener = new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    getMyLocation(location);
                    Log.d("onLocationChanged", "onLocationChanged");
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            mDatabase.deleteRequest(userId);
            setLocation();

            SharedPreferences.Editor editor = sharedPreferencesCheckScreen.edit();
            editor.putInt(SCREEN_NAME, MAIN_ACTIVITY);
            editor.commit();
        }catch (Exception e) {
            SharedPreferences.Editor editor = sharedPreferencesCheckScreen.edit();
            editor.putInt(SCREEN_NAME, MAIN_ACTIVITY);
            editor.commit();
        }
    }

    @Override
    public void sendRequestSuccess() {
        isSending = true;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isSending = false;

                checkOnScreen = sharedPreferencesCheckScreen.getInt(SCREEN_NAME, 0);
                if (checkOnScreen == WAIT_CONFIRM) {

                    editorScreen = sharedPreferencesCheckScreen.edit();
                    editorScreen.putInt(SCREEN_NAME, ADDED_REQUEST);
                    editorScreen.commit();
               }
            }
        }, 300000);

        frameLayoutMarkerInfo.setVisibility(View.GONE);
        btnCancelRequest.setVisibility(View.VISIBLE);
    }

    @Override
    public void getListActiveUserUpdateSuccess(List<ActiveUser> activeUsers) {

        if (!isCancel) {
            if (numberMarkerInHashMap.size() > 0) {
                for (int i = 0; i < numberMarkerInHashMap.size(); i++) {
                    Marker marker = numberMarkerInHashMap.get(i);
                    marker.remove();
                }
                numberMarkerInHashMap.clear();
                markerHashMap.clear();
                userHashMap.clear();

            }
            if (activeUsers.size() > 0) {

                for (int i = 0; i < activeUsers.size(); i++) {
                    ActiveUser activeUser = activeUsers.get(i);

                    new CustomMarkerAsync(this, i).execute(activeUser);
                }
            }

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (checkOnScreen == ADDED_REQUEST || checkOnScreen == WAIT_CONFIRM) {
                        listActiveUserAPI.getListUpdate(sessionId, vehicleType);
                    }
                }
            }, 30000);
        }
    }

    @Override
    public void getListActiveUserFailure(String message) {
        if (!isCancel) {
            if (message.equals("1")) {
                if (numberMarkerInHashMap.size() > 0) {
                    for (int i = 0; i < numberMarkerInHashMap.size(); i++) {
                        Marker marker = numberMarkerInHashMap.get(i);
                        marker.remove();
                    }
                    numberMarkerInHashMap.clear();
                    markerHashMap.clear();
                    userHashMap.clear();
                }
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    listActiveUserAPI.getListUpdate(sessionId, vehicleType);
                }
            }, 30000);
        }
    }

    @Override
    public void noRequestInCurrent() {
        Toast.makeText(this, getResources().getString(R.string.no_request), Toast.LENGTH_SHORT).show();
        showAddRequestButton();

        if (listActiveUser.size() > 0) {
            for (ActiveUser user : listActiveUser) {
                if (markerHashMap.containsKey(user)) {
                    Marker markerRm = markerHashMap.get(user);
                    markerRm.remove();

                }
            }
            markerHashMap.clear();
        }
        mGoogleMap.clear();
        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                getMyLocation(location);
                Log.d("onLocationChanged", "onLocationChanged");
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        mDatabase.deleteRequest(userId);
        setLocation();

        SharedPreferences.Editor editor = sharedPreferencesCheckScreen.edit();
        editor.putInt(SCREEN_NAME, MAIN_ACTIVITY);
        editor.commit();

    }

    @Override
    public void unAuthorize() {
        logout();
    }

    @Override
    public void getInfoUserSuccess(InfomationUser userInfo) {
        if(userInfo.getUserInfo().getAvatarLink()!=null&&!userInfo.getUserInfo().getAvatarLink().equals("")){
            Glide.with(this).load(userInfo.getUserInfo().getAvatarLink()).placeholder(getResources().getDrawable(R.drawable.temp)).centerCrop().into(imgUser);
            new GetAvatar(this,this).execute(userInfo.getUserInfo().getAvatarLink());

        }else {
            imgUser.setImageResource(R.drawable.temp);
        }
    }

    @Override
    public void getUserInfoFailure(String message) {

    }

    @Override
    public void getBitMapSuccess(Bitmap bitmap) {
            if(bitmap!=null && !bitmap.equals(""))
            imgUser.setImageBitmap(bitmap);
    }
}
