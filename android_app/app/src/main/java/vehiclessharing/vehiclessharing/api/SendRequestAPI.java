package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.ResultSendRequest;

public class SendRequestAPI {
    private RestManager restManager;
    private SendRequestInterface sendRequestInterface;

    public SendRequestAPI(SendRequestInterface sendRequestInterface) {
        this.sendRequestInterface = sendRequestInterface;
        restManager=new RestManager();
    }

    public void sendRequestToChosenUser(String apiToken, int receiverId, String note){
        restManager.getApiService().sendRequestTogether(apiToken,receiverId,note).enqueue(new Callback<ResultSendRequest>() {
            @Override
            public void onResponse(Call<ResultSendRequest> call, Response<ResultSendRequest> response) {
                if(response.isSuccessful() && response.body().getStatus().getError() == 0){
                sendRequestInterface.sendRequestSuccess();}
                else {
                    sendRequestInterface.sendRequestFailure();
                }
            }
            @Override
            public void onFailure(Call<ResultSendRequest> call, Throwable t) {
                sendRequestInterface.sendRequestFailure();
            }
        });
    }

    public interface SendRequestInterface{
        void sendRequestSuccess();
        void sendRequestFailure();
    }

}
