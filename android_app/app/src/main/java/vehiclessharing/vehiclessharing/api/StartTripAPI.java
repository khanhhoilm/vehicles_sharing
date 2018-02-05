package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.JourneyInfo;
import vehiclessharing.vehiclessharing.model.StartStripResponse;

/**
 * Created by Hihihehe on 12/4/2017.
 */

public class StartTripAPI {
    private RestManager restManager;
    private StartTripRequestCallback requestCallback;

    public StartTripAPI(StartTripRequestCallback requestCallback) {
        this.requestCallback = requestCallback;
        restManager=new RestManager();
    }

    private StartTripAPI() {
    }

    public void sendNotiStartTripToUserTogether(String apiToken,int partnerId,int vehiclesType) {
        restManager.getApiService().startTheTrip(apiToken,partnerId,vehiclesType).enqueue(new Callback<StartStripResponse>() {
            @Override
            public void onResponse(Call<StartStripResponse> call, Response<StartStripResponse> response) {
                if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                    requestCallback.startTripSuccess(response.body().getJourneyInfo());
                } else {
                    requestCallback.startTripFailure("start trip failed");
                }
            }

            @Override
            public void onFailure(Call<StartStripResponse> call, Throwable t) {
                requestCallback.startTripFailure("onFailure");
            }
        });
    }

    public interface StartTripRequestCallback {
        void startTripSuccess(JourneyInfo journeyInfo);

        void startTripFailure(String message);

    }
}
