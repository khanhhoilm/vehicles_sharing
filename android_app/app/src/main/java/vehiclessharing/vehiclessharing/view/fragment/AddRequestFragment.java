package vehiclessharing.vehiclessharing.view.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.api.AddRequestAPI;
import vehiclessharing.vehiclessharing.api.CancelRequestAPI;
import vehiclessharing.vehiclessharing.api.RestManager;
import vehiclessharing.vehiclessharing.authentication.SessionManager;
import vehiclessharing.vehiclessharing.interfaces.AuthenticationFail;
import vehiclessharing.vehiclessharing.model.ActiveUser;
import vehiclessharing.vehiclessharing.model.RequestResult;
import vehiclessharing.vehiclessharing.utils.PlaceHelper;
import vehiclessharing.vehiclessharing.view.activity.MainActivity;
import vehiclessharing.vehiclessharing.view.adapter.PlaceAutocompleteAdapter;
import vehiclessharing.vehiclessharing.view.adapter.SpinerVehicleTypeAdapter;

public class AddRequestFragment extends DialogFragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener,
        AddRequestAPI.AddRequestInterfaceCallback {
    // TODO: Rename parameter arguments, choose names that match

    private static final String WHAT_BTN_CLICK = "what_btn_click";
    private static final String PRESENT_ADDRESS = "present_address";
    private String getWhatBtnClick = "";
    private TextView txtTitle, txtTimeStart;

    private Button btnOk, btnCancel;
    private Spinner spType;
    private SpinerVehicleTypeAdapter adapter;
    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;
    private int CUR_PLACE_AUTOCOMPLETE_REQUEST_CODE = 3;
    private int DES_PLACE_AUTOCOMPLETE_REQUEST_CODE = 4;
    private CancelRequestAPI.CancelRequestCallBack cancelRequestCallBack;
    private int userId;
    private String sessionId = "", refreshedToken = "";
    private Context mContext;
    private LatLng srcLatLng, desLatLng;
    private String time = "";


    java.util.Calendar calendar = java.util.Calendar.getInstance();
    java.text.SimpleDateFormat sdf1 = new java.text.SimpleDateFormat("HH:mm");
    java.text.SimpleDateFormat sdf2 = new java.text.SimpleDateFormat("dd/MM/yyyy");
    private static final LatLngBounds myBound = new LatLngBounds(new LatLng(-0, 0), new LatLng(0, 0));

    private ImageView imgClearCurLocation, imgClearDesLocation;
    private TextView txtCurLocation, txtDesLocation;
    private Drawable mDrawable;
    private RestManager mManager;
    private String currentDay;
    private int vehicleType = 1;
    private String presentAdress = "";
    private ProgressBar progressBar;
    private OnFragmentAddRequestListener mListener;

    public AddRequestFragment() {
    }

    public static AddRequestFragment newInstance(String btnClick, String presentAddress) {
        AddRequestFragment fragment = new AddRequestFragment();
        Bundle args = new Bundle();
        args.putString(WHAT_BTN_CLICK, btnClick);
        args.putString(PRESENT_ADDRESS, presentAddress);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            getWhatBtnClick = getArguments().getString(WHAT_BTN_CLICK);
            presentAdress = getArguments().getString(PRESENT_ADDRESS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .enableAutoManage(getActivity(), 0, this)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }
        View view = inflater.inflate(R.layout.fragment_add_request, container, false);

        addControls(view);
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token FCM", "Token Value: " + refreshedToken);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SessionManager.PREF_NAME_LOGIN, Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt(SessionManager.USER_ID, 0);
        sessionId = sharedPreferences.getString(SessionManager.KEY_SESSION, "");
        Log.d("User Info", "User_id: " + String.valueOf(userId) + ", api_token: " + String.valueOf(sessionId));

        // Inflate the layout for this fragment
        return view;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private void addControls(View view) {
        mContext = getActivity();
        txtTitle = (TextView) view.findViewById(R.id.txtNeederDialogTitle);

        txtCurLocation = (EditText) view.findViewById(R.id.txtCurLocate);
        txtDesLocation = (EditText) view.findViewById(R.id.txtDesLocate);
        txtCurLocation.setOnClickListener(this);
        txtDesLocation.setOnClickListener(this);

        txtTimeStart = (TextView) view.findViewById(R.id.txtTimeStart);
        btnOk = (Button) view.findViewById(R.id.btnAddOK);
        btnCancel = (Button) view.findViewById(R.id.btnAddCancel);
        progressBar = view.findViewById(R.id.progressBar);


        imgClearCurLocation = (ImageView) view.findViewById(R.id.imgClearCurLocation);
        //set current location
        imgClearDesLocation = (ImageView) view.findViewById(R.id.imgClearDesLocation);
        txtTimeStart.setText(sdf1.format(calendar.getTime()));
        currentDay = sdf2.format(calendar.getTime());
        txtTimeStart.setOnClickListener(this);
        imgClearCurLocation.setOnClickListener(this);
        imgClearDesLocation.setOnClickListener(this);

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        mDrawable = getResources().getDrawable(R.drawable.ic_warning_red_600_24dp);
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
        if (!presentAdress.equals("")) {
            txtCurLocation.setText(presentAdress);
        } else {
            try {
                txtCurLocation.setText(PlaceHelper.getInstance(mContext).getCurrentPlace(MainActivity.mGoogleMap));
            } catch (Exception e) {
                txtCurLocation.setText("");

            }
        }
        spType = (Spinner) view.findViewById(R.id.spVehicleType);
        List<String> type = new ArrayList<>();
        type.add("Xe máy");
        type.add("Ô tô");
        adapter = new SpinerVehicleTypeAdapter(mContext, type);
        spType.setAdapter(adapter);
        switch (getWhatBtnClick) {
            case "btnFindPeople":
                txtTitle.setText(mContext.getResources().getString(R.string.dialog_find_people));
                spType.setVisibility(View.VISIBLE);
                break;
            case "btnFindVehicles":
                // vehicleType=0;
                txtTitle.setText(mContext.getResources().getString(R.string.dialog_find_vehicle));
                spType.setVisibility(View.GONE);

        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentAddRequestListener) {
            mListener = (OnFragmentAddRequestListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
        mListener = null;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.txtCurLocate: {
                txtCurLocation.setEnabled(false);
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(getActivity());
                    startActivityForResult(intent, CUR_PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                break;
            }
            case R.id.txtDesLocate:
                txtDesLocation.setEnabled(false);
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(getActivity());
                    startActivityForResult(intent, DES_PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                break;
            case R.id.btnAddOK:

                boolean checkEmpty = checkValidation();
                if (!checkEmpty) {
                    btnOk.setEnabled(false);
                    btnCancel.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    sendRequestToServer();
                    MainActivity.btnAddClick = false;
                }
                break;
            case R.id.btnAddCancel:
                dismiss();
                MainActivity.btnAddClick = false;
                break;
            case R.id.imgClearCurLocation:
                txtCurLocation.setText("");
                break;
            case R.id.imgClearDesLocation:
                txtDesLocation.setText("");
                break;
            case R.id.txtTimeStart:
                showTimePicker();
        }
    }

    private void sendRequestToServer() {
        try {
            switch (getWhatBtnClick) {
                case "btnFindPeople":
                    vehicleType = spType.getSelectedItemPosition() + 1;
                    break;
                case "btnFindVehicles":
                    vehicleType = 0;

            }

            srcLatLng = PlaceHelper.getInstance(mContext).getLatLngByName(txtCurLocation.getText().toString());
            final String sourLocation = "{\"lat\":\"" + String.valueOf(srcLatLng.latitude) + "\",\"lng\":\"" + String.valueOf(srcLatLng.longitude) + "\"}";


            desLatLng = PlaceHelper.getInstance(mContext).getLatLngByName(txtDesLocation.getText().toString());
            final String desLocation = "{\"lat\":\"" + String.valueOf(desLatLng.latitude) + "\",\"lng\":\"" + String.valueOf(desLatLng.longitude) + "\"}";

            time = txtTimeStart.getText().toString();
            String deviceId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
            String deviceToken = FirebaseInstanceId.getInstance().getToken();

            Log.d("Token FCM", "Token Value: " + deviceToken);
            Log.d("DeviceId", "Device Id Value: " + deviceId);

            java.util.Calendar calendarCurrent = java.util.Calendar.getInstance();
            java.text.SimpleDateFormat simpleDateFormatCurrent = new java.text.SimpleDateFormat("HH:mm");

            String currentTime = simpleDateFormatCurrent.format(calendarCurrent.getTime());

            AddRequestAPI addRequestAPI = new AddRequestAPI(this);
            addRequestAPI.addRequest(userId, sourLocation, desLocation, time, sessionId, deviceId, vehicleType, deviceToken, currentTime);

        } catch (Exception e){
            Toast.makeText(mContext, "send request to server ", Toast.LENGTH_SHORT).show();
        }

    }

    private void showTimePicker() {
        TimePickerDialog.OnTimeSetListener callBack = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendar.set(java.util.Calendar.HOUR_OF_DAY, i);
                calendar.set(java.util.Calendar.MINUTE, i1);
                txtTimeStart.setText(sdf1.format(calendar.getTime()));
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                mContext,
                callBack,
                calendar.get(java.util.Calendar.HOUR_OF_DAY),
                calendar.get(java.util.Calendar.MINUTE), true
        );
        timePickerDialog.show();
    }

    private boolean checkValidation() {
        String curLocate = txtCurLocation.getText().toString();
        String desLocate = txtDesLocation.getText().toString();
        boolean checkNull = false;
        if (curLocate.equals("")) {
            Toast.makeText(mContext, "Vị trí bắt đầu không được để trống", Toast.LENGTH_SHORT).show();
            checkNull = true;
        } else if (desLocate.equals("")) {
            Toast.makeText(mContext, "Vị trí kết thúc không được để trống", Toast.LENGTH_SHORT).show();
            checkNull = true;
        }
        return checkNull;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        txtCurLocation.setEnabled(true);
        txtDesLocation.setEnabled(true);
        Place place = PlaceAutocomplete.getPlace(getActivity(), data);
        if (requestCode == CUR_PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            //    if (resultCode == 1) {
            if (txtCurLocation != null && place != null) {
                txtCurLocation.setText(place.getAddress());
            }

        } else if (requestCode == DES_PLACE_AUTOCOMPLETE_REQUEST_CODE) {

            if (txtDesLocation != null && place != null) {
                txtDesLocation.setText(place.getAddress());
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void addRequestSuccess(RequestResult requestResult) {
        progressBar.setVisibility(View.GONE);
        List<ActiveUser> listActive = new ArrayList<>();
        Log.d("registerRequest", "registerRequest success");
        if (requestResult.getActiveUsers() != null && requestResult.getActiveUsers().size() > 0) {
            listActive = requestResult.getActiveUsers();
        } else {
            String vType = "";
            if (vehicleType == 0) {
                vType = "chia sẻ xe";
            } else {
                vType = "cần xe chia sẻ";
            }
            Toast.makeText(mContext, "Không có người nào đang " + vType, Toast.LENGTH_SHORT).show();
        }
        mListener.addRequestSuccess(srcLatLng, desLatLng, time, vehicleType, listActive);

        if (isAdded()) {
            dismiss();
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void addRequestUnsuccess(int statusCode) {
        switch (statusCode) {
            case 403:
                AuthenticationFail.unAuthorized(getActivity(), getActivity().getSupportFragmentManager());
                break;
            default:
                Toast.makeText(mContext, getString(R.string.add_request_fail), Toast.LENGTH_SHORT).show();
                btnOk.setEnabled(true);
                btnCancel.setEnabled(true);
                break;
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void addRequestFailure() {
        progressBar.setVisibility(View.GONE);
        if (isAdded()) {
            btnOk.setEnabled(true);
            btnCancel.setEnabled(true);
            Toast.makeText(mContext, getString(R.string.add_request_fail), Toast.LENGTH_SHORT).show();
        }
    }


    public interface OnFragmentAddRequestListener {
        // TODO: Update argument type and name
        void addRequestSuccess(LatLng cur, LatLng des, String time, int type, List<ActiveUser> list);

        void addRequestFailure();

        void cancelRequestSuccess(boolean success);
    }
}
