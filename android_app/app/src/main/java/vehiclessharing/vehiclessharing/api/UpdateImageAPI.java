package vehiclessharing.vehiclessharing.api;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.PathImageUpload;
import vehiclessharing.vehiclessharing.model.StatusResponse;

/**
 * Created by Hihihehe on 1/11/2018.
 */

public class UpdateImageAPI {
    private RestUploadFile restUploadFile;
    private RestManager restManager;
    private UpdateImageCallback updateImageCallback;

    public UpdateImageAPI(UpdateImageCallback callback) {
        updateImageCallback=callback;
        restManager=new RestManager();
        restUploadFile=new RestUploadFile();
    }

    public void getURLImage(MultipartBody.Part body){
        final Call<PathImageUpload> req = restUploadFile.getApiService().postImage(body);
        req.enqueue(new Callback<PathImageUpload>() {
            @Override
            public void onResponse(Call<PathImageUpload> call, Response<PathImageUpload> response) {
                if (response.code() == 200) {
                    updateImageCallback.getURLImageSuccess(response.body().getFilePath());
                    //Toast.makeText(EditProfileActivity.this, "Cập nhật hình ảnh thành công", Toast.LENGTH_SHORT).show();
                }else {
                    updateImageCallback.getURLImageFailure();
                }
                // Do Something
            }

            @Override
            public void onFailure(Call<PathImageUpload> call, Throwable t) {
                updateImageCallback.updateImageFailure();
            }
        });

    }

    public void updateImage(String apiToken,String avatarLink){
        restManager.getApiService().updateAvatar(apiToken,avatarLink).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if(response.isSuccessful()) {
                    updateImageCallback.updateImageSuccess();
                }else {
                    updateImageCallback.updateImageFailure();
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                updateImageCallback.updateImageFailure();
            }
        });
    }
    public void updateInfoUser(String apiToken, String name, String email,String avatarLink, int gender,String address, String birthday){
        restManager.getApiService().updateInfoUser(apiToken, name,email,avatarLink, gender, address, birthday).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body().getStatus().getError() == 0) {
                   updateImageCallback.updateInfoUserSuccess();
                }else {
                    updateImageCallback.updateInfoUserFailure();
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {

            updateImageCallback.updateInfoUserFailure();
            }
        });
    }
    public interface UpdateImageCallback{
        void getURLImageSuccess(String url);
        void getURLImageFailure();

        void updateImageSuccess();
        void updateImageFailure();

        void updateInfoUserSuccess();
        void updateInfoUserFailure();
    }
}
