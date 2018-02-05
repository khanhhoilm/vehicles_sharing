package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.StatusResponse;


public class SignUpAPI {
    private RestManager restManager;
    private SignUpInterface signUpInterface;
    private static SignUpAPI instance;
    public static SignUpAPI getInstance(SignUpInterface signUpInterface){
        if(instance==null){
            instance=new SignUpAPI(signUpInterface);
        }
        return instance;
    }

    private SignUpAPI(SignUpInterface signUpInterface) {
        this.signUpInterface = signUpInterface;
        restManager=new RestManager();
    }

    public void signUp(String phone,String name, String password, int gender){
        restManager.getApiService().signUp(phone,name,password,gender).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if(response.isSuccessful()){
                    switch (response.body().getStatus().getError()) {
                        case 0:
                            signUpInterface.signUpSuccess();
                            break;
                        case 1:
                            signUpInterface.signUpUnsuccess();
                            break;
                    }
                }else {
                    signUpInterface.signUpFailure();
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                signUpInterface.signUpFailure();
            }
        });
    }

    public interface SignUpInterface{
        void signUpSuccess();
        void signUpUnsuccess();
        void signUpFailure();
    }
}
