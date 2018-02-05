package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.model.SignInResult;

public class SignInAPI {
    private RestManager restManager;
    private SignInInterfaceCallback signInInterfaceCallback;
    private static SignInAPI instance;

    public static SignInAPI getInstance(SignInInterfaceCallback signInInterfaceCallback)
    {
        if(instance==null){
            instance=new SignInAPI(signInInterfaceCallback);
        }
        return instance;
    }


    private SignInAPI(SignInInterfaceCallback signInInterfaceCallback) {
        this.signInInterfaceCallback = signInInterfaceCallback;
        restManager = new RestManager();
    }

    public void signIn(String phone, String password) {
        restManager.getApiService().signIn(phone, password).enqueue(new Callback<SignInResult>() {
            @Override
            public void onResponse(Call<SignInResult> call, Response<SignInResult> response) {
                if (response.isSuccessful()) {
                    switch (response.body().getStatus().getError()) {
                        case 0:
                            signInInterfaceCallback.signInSuccess(response.body());
                            break;
                        case 1:
                            signInInterfaceCallback.signInUnsuccess(response.body());
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<SignInResult> call, Throwable t) {
                signInInterfaceCallback.signInFailure();
            }
        });
    }

    public interface SignInInterfaceCallback {
        void signInSuccess(SignInResult signInResult);

        void signInUnsuccess(SignInResult signInResult);

        void signInFailure();
    }
}
