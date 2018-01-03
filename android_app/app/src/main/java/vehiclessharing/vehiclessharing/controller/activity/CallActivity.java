package vehiclessharing.vehiclessharing.controller.activity;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

import co.vehiclessharing.R;
import vehiclessharing.vehiclessharing.permission.CallPermission;

public class CallActivity extends AppCompatActivity {

    private Button btnCall;
    private TextView callState;
    private SinchClient sinchClient;
    private String receptId = "";
    private com.sinch.android.rtc.calling.Call call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        btnCall = (Button) findViewById(R.id.button);
        callState = findViewById(R.id.callState);
        CallPermission.checkCallSinch(this,this);
        if (MainActivity.userId == 1) {
            receptId="11";
        }else {
            receptId="1";
        }
        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(String.valueOf(MainActivity.userId))
                .applicationKey(getResources().getString(R.string.sinch_app_key))
                .applicationSecret(getResources().getString(R.string.sinch_app_secret))
                .environmentHost(getResources().getString(R.string.sinch_host_name))
                .build();
        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();
        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());


        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (call == null) {
                    if (sinchClient.isStarted()) {
                        call = sinchClient.getCallClient().callUser(receptId);
                        call.addCallListener(new SinchCallListener());
                        btnCall.setText("Hang Up");
                        callState.setText("Calling...");

                    }else {
                       // sinchClient.startListeningOnActiveConnection();
                        sinchClient.start();
                        call = sinchClient.getCallClient().callUser(receptId);
                        call.addCallListener(new SinchCallListener());
                        btnCall.setText("Hang Up");
                        callState.setText("Calling...");

                    }
                } else {
                    call.hangup();
                }
            }
        });

    }

    private class SinchCallClientListener implements CallClientListener {

        @Override
        public void onIncomingCall(CallClient callClient, com.sinch.android.rtc.calling.Call incomingCall) {
            call = incomingCall;
            call.answer();
            call.addCallListener(new SinchCallListener());
            btnCall.setText("Hang Up");
        }
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallProgressing(com.sinch.android.rtc.calling.Call call) {
            callState.setText("ringing");

        }

        @Override
        public void onCallEstablished(com.sinch.android.rtc.calling.Call call) {
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            callState.setText("connected");
        }

        @Override
        public void onCallEnded(com.sinch.android.rtc.calling.Call call) {
            call = null;
            btnCall.setText("Call");
            callState.setText("");
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }

        @Override
        public void onShouldSendPushNotification(com.sinch.android.rtc.calling.Call call, List<PushPair> list) {

        }
    }
}
