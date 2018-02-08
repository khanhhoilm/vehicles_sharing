package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.StatusResponse;

public class CancelTripAPI {
    private RestManager restManager;
    private CancelTripInterface cancelTripInterface;

    public CancelTripAPI(CancelTripInterface cancelTrip) {
        cancelTripInterface = cancelTrip;
        restManager=new RestManager();
    }

    public void cancel(String apiToken, int vehicleType, String comment) {
        restManager.getApiService().cancelTrip(apiToken, vehicleType, comment).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                    cancelTripInterface.cancelTripSuccess();
                } else
                    cancelTripInterface.cancelTripFailed();
                {
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                cancelTripInterface.cancelTripFailed();
            }
        });
    }

    public interface CancelTripInterface {

        void cancelTripSuccess();

        void cancelTripFailed();
    }
}
