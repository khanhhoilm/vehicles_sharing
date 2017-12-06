package vehiclessharing.vehiclessharing.api;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.StatusResponse;

/**
 * Created by Hihihehe on 11/10/2017.
 */

public class RelateRequestAPI {

    private RestManager restManager;
    private static CancelRequestCallBack cancelRequestCallBack;
    //   private Dialog dialogConfirmSend;


    public static RelateRequestAPI getInstance(CancelRequestCallBack callBack) {

        cancelRequestCallBack = callBack;
        return new RelateRequestAPI();
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
            }
        });
    }


  /*  public void addRequest(int userId, final String sourLocation, final String desLocation, final String time, String sessionId, String deviceId,
                          final int vehicleType, String deviceToken) {

        restManager.getApiService().registerRequest(userId, sourLocation, desLocation, time, sessionId, deviceId, vehicleType, deviceToken).enqueue(new Callback<RequestResult>() {
            @Override
            public void onResponse(Call<RequestResult> call, Response<RequestResult> response) {
                if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                    List<ActiveUser> activeUserList = new ArrayList<ActiveUser>();
                    cancelRequestCallBack.addRequestSuccessful(sourLocation, desLocation, time, vehicleType, response.body().getActiveUsers());
                    //mListener.addRequestSuccess(srcLatLng, desLatLng, time, vehicleType, response.body().getActiveUsers());
                }

            }

            @Override
            public void onFailure(Call<RequestResult> call, Throwable t) {

            }
        });

    }
*/
    public interface CancelRequestCallBack {
        void cancelRequestSuccess(boolean success);

      //  void addRequestSuccessful(LatLng cur, LatLng des, String time, int type, List<ActiveUser> list );
    }

}
