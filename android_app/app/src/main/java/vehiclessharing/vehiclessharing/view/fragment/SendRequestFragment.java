package vehiclessharing.vehiclessharing.view.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import co.vehiclessharing.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.api.RestManager;
import vehiclessharing.vehiclessharing.api.SendRequestAPI;
import vehiclessharing.vehiclessharing.view.activity.MainActivity;
import vehiclessharing.vehiclessharing.model.ResultSendRequest;

public class SendRequestFragment extends DialogFragment implements View.OnClickListener,SendRequestAPI.SendRequestInterface {
    private static final String API_TOKEN = "api_token";
    private static final String RECEIVER_ID = "receiver_id";

    private String apiToken;
    private int receiverId;
    private EditText txtNote;
    private Button btnSend, btnCancel;
    private RestManager restManager;
    private Activity mActivity;
    private static SendRequestCallBack sendRequestCallBack;

    public SendRequestFragment() {

    }

    public static SendRequestFragment newInstance(String apiToken, int receiverId, SendRequestCallBack callBack) {
        sendRequestCallBack = callBack;
        SendRequestFragment fragment = new SendRequestFragment();
        Bundle args = new Bundle();
        args.putString(API_TOKEN, apiToken);
        args.putInt(RECEIVER_ID, receiverId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            apiToken = getArguments().getString(API_TOKEN);
            receiverId = getArguments().getInt(RECEIVER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_request, container, false);
        mActivity = getActivity();
        addControls(view);
        addEvents();

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private void addEvents() {
        btnSend.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }

    private void addControls(View view) {
        txtNote = (EditText) view.findViewById(R.id.txtNote);
        btnSend = (Button) view.findViewById(R.id.btnSendrequest);
        btnCancel = (Button) view.findViewById(R.id.btnCancelSend);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSendrequest:
                sendRequestToChosenUser();
                break;
            case R.id.btnCancelSend:
                dismiss();
                break;
        }
    }

    public void sendRequestToChosenUser() {
        Log.d("sendRequestTogether", "api_token: " + apiToken + ", receiver_id: " + String.valueOf(receiverId));
        String note = txtNote.getText().toString();

        SendRequestAPI sendRequestAPI=new SendRequestAPI(this);
        sendRequestAPI.sendRequestToChosenUser(apiToken,receiverId,note);
    }

    @Override
    public void sendRequestSuccess() {
        if (isAdded()) {
            dismiss();
            Toast.makeText(mActivity, mActivity.getString(R.string.wait_accept), Toast.LENGTH_SHORT).show();
        }
        sendRequestCallBack.sendRequestSuccess();

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat sdf1 = new java.text.SimpleDateFormat("HH:mm");
        String currentTime = sdf1.format(calendar.getTime());

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(MainActivity.SCREEN_AFTER_BACK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MainActivity.SCREEN_NAME, MainActivity.WAIT_CONFIRM);
        editor.putString(MainActivity.TIME_SEND_REQUEST, currentTime);
        editor.commit();
    }

    @Override
    public void sendRequestFailure() {
       /* Toast.makeText(mActivity, "Send failed", Toast.LENGTH_SHORT).show();
        if (isAdded()) {
            dismiss();
        }*/
        Toast.makeText(mActivity, mActivity.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
    }

    public interface SendRequestCallBack {
        void sendRequestSuccess();
    }
}




