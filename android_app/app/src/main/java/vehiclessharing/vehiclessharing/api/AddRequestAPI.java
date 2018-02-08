package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import vehiclessharing.vehiclessharing.model.RequestResult;

public class AddRequestAPI {
    private RestManager restManager;
    private AddRequestInterfaceCallback addRequestInterfaceCallback;

    public AddRequestAPI(AddRequestInterfaceCallback addRequestInterfaceCallback) {
        this.addRequestInterfaceCallback = addRequestInterfaceCallback;
        restManager = new RestManager();
    }

    public void addRequest(int userId, String sourLocation, String desLocation, String time, String sessionId, String deviceId, int vehicleType, String fcmToken, String currentTime) {
        restManager.getApiService().registerRequest(userId, sourLocation, desLocation, time, sessionId, deviceId,
                vehicleType, fcmToken, currentTime).enqueue(new Callback<RequestResult>() {
            @Override
            public void onResponse(Call<RequestResult> call, Response<RequestResult> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus().getError() == 0) {
                    addRequestInterfaceCallback.addRequestSuccess(response.body());
                }else {
                    addRequestInterfaceCallback.addRequestUnsuccess(response.code());
                }
            }

            @Override
            public void onFailure(Call<RequestResult> call, Throwable t) {
                addRequestInterfaceCallback.addRequestFailure();

            }
        });
    }

    public interface AddRequestInterfaceCallback {
        void addRequestSuccess(RequestResult requestResult);

        void addRequestUnsuccess(int statusCode);

        void addRequestFailure();
    }
}
