package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.StatusResponse;

public class FavoriteUserAPI {
    private RestManager restManager;
    private FavoriteCallback favoriteCallback;

    public FavoriteUserAPI(FavoriteCallback favoriteCallback) {
        this.favoriteCallback = favoriteCallback;
        restManager=new RestManager();
    }

    public void like(String apiToken, int partnerId) {
        restManager.getApiService().addToFavorite(apiToken, partnerId).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                    favoriteCallback.favoriteSuccess();
                } else {
                    favoriteCallback.favoriteFailure();
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                favoriteCallback.favoriteFailure();
            }
        });
    }

    public interface FavoriteCallback {
        void favoriteSuccess();

        void favoriteFailure();
    }
}
