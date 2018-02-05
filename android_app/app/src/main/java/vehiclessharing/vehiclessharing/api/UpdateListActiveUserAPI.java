package vehiclessharing.vehiclessharing.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.ActiveUser;
import vehiclessharing.vehiclessharing.model.RequestResult;

/**
 * Created by Hihihehe on 12/18/2017.
 */

public class UpdateListActiveUserAPI {
    private RestManager restManager;
    private UpdateListActiveUserAPI updateListActiveUser;
    private UpdateListActiveUserCallback updateListActiveUserCallback;

    public UpdateListActiveUserAPI(UpdateListActiveUserCallback updateListActiveUserCallback) {
        this.updateListActiveUserCallback = updateListActiveUserCallback;
        restManager=new RestManager();
    }

    public void getListUpdate(String apiToken, int vehicleType) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat sdf1 = new java.text.SimpleDateFormat("HH:mm");
        String currentTime = sdf1.format(calendar.getTime());
        restManager.getApiService().updateListActiveUser(apiToken, vehicleType, currentTime).enqueue(new Callback<RequestResult>() {
            @Override
            public void onResponse(Call<RequestResult> call, Response<RequestResult> response) {
                if(response.code()==200) {
                    if (response.isSuccessful() && response.body().getStatus().getError() == 0 && response.body().getActiveUsers() != null
                            && response.body().getActiveUsers().size() > 0) {
                        updateListActiveUserCallback.getListActiveUserUpdateSuccess(response.body().getActiveUsers());
                    } else if (response.body().getStatus().getError() == 1) {
                        updateListActiveUserCallback.getListActiveUserFailure("1");
                    } else {
                        updateListActiveUserCallback.getListActiveUserFailure("onFailure");
                    }
                }else if(response.code()==401){
                    updateListActiveUserCallback.unAuthorize();
                }
            }

            @Override
            public void onFailure(Call<RequestResult> call, Throwable t) {
                updateListActiveUserCallback.getListActiveUserFailure("onFailure");
            }
        });
    }


    public interface UpdateListActiveUserCallback {
        void getListActiveUserUpdateSuccess(List<ActiveUser> activeUsers);
        void getListActiveUserFailure(String message);
        void noRequestInCurrent();
        void unAuthorize();
    }
}
