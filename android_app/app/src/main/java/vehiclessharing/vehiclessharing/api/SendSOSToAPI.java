package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.StatusResponse;

/**
 * Created by Hihihehe on 1/11/2018.
 */

public class SendSOSToAPI {
    private RestManager restManager;
    private SendSOSCallback sendSOSCallback;

    public SendSOSToAPI(SendSOSCallback sendSOSCallback) {
        this.sendSOSCallback = sendSOSCallback;
        restManager=new RestManager();
    }

    public void sendSOS(final String apiToken, final String address, final int vehicleType){
        final String comment="Nguy hiểm";
        restManager.getApiService().sendSOS(apiToken,vehicleType,address,comment).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if(response.isSuccessful()&&response.body().getStatus().getError()==0) {
                    sendSOSCallback.sendSOSSuccess();
                }else {
                   sendSOSCallback.sendSOSFailed();                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                sendSOSCallback.sendSOSFailed();
            }
        });
    }

    public interface SendSOSCallback{
        void sendSOSSuccess();
        void sendSOSFailed();
    }
}
