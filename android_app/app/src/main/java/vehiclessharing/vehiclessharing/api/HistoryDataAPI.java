package vehiclessharing.vehiclessharing.api;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.History;
import vehiclessharing.vehiclessharing.model.UserHistoryInfo;

/**
 * Created by Hihihehe on 12/13/2017.
 */

public class HistoryDataAPI {
    private HistoryDriverCallback historyDriverCallback;
    private HistoryHikerCallback historyHikerCallback;
    private RestManager restManager, historyRest;

    private HistoryDataAPI getHistoryData;
    private int page;
    private boolean isCalling = false;

    public HistoryDataAPI(HistoryDriverCallback historyDriverCallback) {
        this.historyDriverCallback = historyDriverCallback;
        restManager=new RestManager();
    }

    public HistoryDataAPI(HistoryHikerCallback historyHikerCallback) {
        this.historyHikerCallback = historyHikerCallback;
        restManager=new RestManager();
    }

    public void getHistoryDriver(final String apiToken, final String userType) {
        restManager.getApiService().getHistory(apiToken, userType).enqueue(new Callback<History>() {
            @Override
            public void onResponse(Call<History> call, Response<History> response) {
                if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                    historyDriverCallback.getHistoryDriverSuccess(response.body().getUserHistoryInfo().get(0), userType);
                }
            }

            @Override
            public void onFailure(Call<History> call, Throwable t) {
                historyDriverCallback.getHistoryFailured("onFailure");
            }
        });
    }

    public void getHistoryHiker(final String apiToken, final String userType) {
        restManager.getApiService().getHistory(apiToken, userType).enqueue(new Callback<History>() {
            @Override
            public void onResponse(Call<History> call, Response<History> response) {
                if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                    historyHikerCallback.getHistoryHikerSuccess(response.body().getUserHistoryInfo().get(0), userType);
                }
            }

            @Override
            public void onFailure(Call<History> call, Throwable t) {
                historyHikerCallback.getHistoryFailured("onFailure");
            }
        });
    }


    public void getHistoryAnotherDriver(final String apiToken, final String userType, int anotherUserId) {
        try {
        historyRest.getApiService().getHistoryAnotherUser(apiToken, userType, anotherUserId).enqueue(new Callback<History>() {
            @Override
            public void onResponse(Call<History> call, Response<History> response) {
                if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                    historyDriverCallback.getHistoryDriverSuccess(response.body().getUserHistoryInfo().get(0), userType);
                }
            }

            @Override
            public void onFailure(Call<History> call, Throwable t) {
                historyDriverCallback.getHistoryFailured("onFailure");
            }
        });
        } catch (Exception e) {
            Log.d("exception", "gethistory");
        }
    }

    public void getHistoryAnotherHiker(final String apiToken, final String userType, int anotherUserId) {
        try {
            historyRest.getApiService().getHistoryAnotherUser(apiToken, userType, anotherUserId).enqueue(new Callback<History>() {
                @Override
                public void onResponse(Call<History> call, Response<History> response) {
                    if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                        historyHikerCallback.getHistoryHikerSuccess(response.body().getUserHistoryInfo().get(0), userType);
                    }
                }

                @Override
                public void onFailure(Call<History> call, Throwable t) {
                    historyHikerCallback.getHistoryFailured("onFailure");
                }
            });
        } catch (Exception e) {
            Log.d("exception", "gethistory");
        }
    }

    public interface HistoryDriverCallback {
        void getHistoryDriverSuccess(UserHistoryInfo userHistoryInfo, String userType);

        void getHistoryFailured(String message);
    }

    public interface HistoryHikerCallback {
        void getHistoryHikerSuccess(UserHistoryInfo userHistoryInfo, String userType);

        void getHistoryFailured(String message);
    }

}
