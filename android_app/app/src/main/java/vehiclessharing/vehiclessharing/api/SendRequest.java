package vehiclessharing.vehiclessharing.api;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.ResultSendRequest;
import co.vehiclessharing.R;

/**
 * Created by Hihihehe on 11/10/2017.
 */

public class SendRequest {

    private RestManager restManager;
    //   private Dialog dialogConfirmSend;

    public static SendRequest getInstance() {


        return new SendRequest();
    }

    public void sendRequestTogether(final DialogInterface dialogSend, final Activity mActivity, String sessionId, int receiverId) {
        Log.d("sendRequestTogether","api_token: "+sessionId+", receiver_id: "+String.valueOf(receiverId));
        restManager.getApiService().sendRequestTogether(sessionId, receiverId).enqueue(new Callback<ResultSendRequest>() {
            @Override
            public void onResponse(Call<ResultSendRequest> call, Response<ResultSendRequest> response) {
                if (response.isSuccessful() && response.body().getFcmData().getResultPush().equals("0")) {
                    dialogSend.dismiss();
                    Toast.makeText(mActivity, mActivity.getString(R.string.wait_accept), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResultSendRequest> call, Throwable t) {
                Toast.makeText(mActivity, mActivity.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
