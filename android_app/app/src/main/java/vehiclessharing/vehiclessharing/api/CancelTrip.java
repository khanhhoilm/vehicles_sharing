package vehiclessharing.vehiclessharing.api;

import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.controller.fragment.CancelTripDialog;
import vehiclessharing.vehiclessharing.model.StatusResponse;

/**
 * Created by Hihihehe on 1/2/2018.
 */

public class CancelTrip {
    private static RestManager restManager;
    private static CancelTripInterface cancelTripInterface;

    public static CancelTrip newInstance(CancelTripInterface cancel) {
        cancelTripInterface=cancel;
        return new CancelTrip();
    }

    public void cancel(String apiToken, int vehicleType, String comment){
        restManager.getApiService().cancelTrip(apiToken,vehicleType,comment).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if(response.isSuccessful()) {
                    cancelTripInterface.cancelTripSuccess();
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {

            }
        });
    }
    public interface CancelTripInterface {
        // TODO: Update argument type and name
        void cancelTripSuccess();
    }
}
