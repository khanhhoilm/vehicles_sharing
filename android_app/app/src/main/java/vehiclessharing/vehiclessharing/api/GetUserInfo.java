package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.User;
import vehiclessharing.vehiclessharing.model.UserInfo;

/**
 * Created by Hihihehe on 12/4/2017.
 */

public class GetUserInfo {
    private static RestManager restManager;
    private static GetInfoUserCallback getInfoUserCallback;
    private static final GetUserInfo ourInstance = new GetUserInfo();

    public static GetUserInfo getInstance(GetInfoUserCallback callback) {
        getInfoUserCallback=callback;
        restManager=new RestManager();
        return ourInstance;
    }

    public void getUserInfoFromAPI(String apiToken,int userId) {
        restManager.getApiService().getUserInfo(apiToken,userId).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if(response.isSuccessful()&&response.body().getInfoUser()!=null) {
                    getInfoUserCallback.getInfoUserSuccess(response.body().getInfoUser());
                }else {
                    getInfoUserCallback.getUserInfoFailure("Response unsuccessful or info user null");
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                getInfoUserCallback.getUserInfoFailure("onFailure");
            }
        });
    }


    public interface GetInfoUserCallback {
        void getInfoUserSuccess(User userInfo);

        void getUserInfoFailure(String message);

    }
}
