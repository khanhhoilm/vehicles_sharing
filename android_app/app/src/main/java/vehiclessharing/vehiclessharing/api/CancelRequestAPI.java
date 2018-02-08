package vehiclessharing.vehiclessharing.api;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.StatusResponse;

public class CancelRequestAPI {

    private RestManager restManager;
    private CancelRequestCallBack cancelRequestCallBack;

    public CancelRequestAPI(CancelRequestCallBack cancelRequestCallBack) {
        this.cancelRequestCallBack = cancelRequestCallBack;
        restManager=new RestManager();
    }

    public void cancelRequest(String apiToken) {
        restManager.getApiService().cancelRequest(apiToken).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus().getError() == 0) {
                    cancelRequestCallBack.cancelRequestSuccess(true);
                } else {
                    cancelRequestCallBack.cancelRequestSuccess(false);
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                Log.d("CancelRequest", "failed");
                cancelRequestCallBack.cancelRequestFailed();
            }
        });
    }


    public interface CancelRequestCallBack {
        void cancelRequestSuccess(boolean success);
        void cancelRequestFailed();
    }

}
