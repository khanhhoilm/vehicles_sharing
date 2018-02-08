package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.StatusResponse;

public class RatingUserTogetherAPI {
    private RestManager restManager;
    private RatingCallback ratingCallback;

    public RatingUserTogetherAPI(RatingCallback ratingCallback) {
        this.ratingCallback = ratingCallback;
        restManager=new RestManager();
    }

    public void rating(String apiToken, int journeyId, final float ratingValue, String comment) {
        restManager.getApiService().ratingUserTogether(apiToken, journeyId, ratingValue, comment).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                    ratingCallback.ratingSuccess();
                } else {
                    ratingCallback.ratingFailure("Response unsucessful ");
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                ratingCallback.ratingFailure("onFailure");
            }
        });
    }

    public interface RatingCallback {
        void ratingSuccess();

        void ratingFailure(String message);
    }
}
