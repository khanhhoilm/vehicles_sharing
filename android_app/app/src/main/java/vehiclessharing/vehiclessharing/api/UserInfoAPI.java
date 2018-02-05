package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.InfomationUser;
import vehiclessharing.vehiclessharing.model.User;
import vehiclessharing.vehiclessharing.model.UserInfo;

/**
 * Created by Hihihehe on 12/4/2017.
 */

public class UserInfoAPI {
    private RestManager restManager;
    private GetInfoUserCallback getInfoUserCallback;

    public UserInfoAPI(GetInfoUserCallback getInfoUserCallback) {
        this.getInfoUserCallback = getInfoUserCallback;
        restManager=new RestManager();
    }

    public void getUserInfoFromAPI(String apiToken, int userId) {
        restManager.getApiService().getUserInfo(apiToken,userId).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if(response.isSuccessful()&&response.body().getStatus().getError()==0) {
                    getInfoUserCallback.getInfoUserSuccess(response.body().getInfomationUser());
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
    public void getMyInfo(String apiToken) {
        restManager.getApiService().getMyInfo(apiToken).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if(response.isSuccessful()&&response.body().getStatus().getError()==0) {
                    getInfoUserCallback.getInfoUserSuccess(response.body().getInfomationUser());
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
        void getInfoUserSuccess(InfomationUser userInfo);

        void getUserInfoFailure(String message);
    }
}
