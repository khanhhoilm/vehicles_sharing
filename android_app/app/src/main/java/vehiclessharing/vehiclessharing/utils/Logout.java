package vehiclessharing.vehiclessharing.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vehiclessharing.vehiclessharing.api.RestManager;
import vehiclessharing.vehiclessharing.authentication.SessionManager;
import vehiclessharing.vehiclessharing.model.StatusResponse;

/**
 * Created by Hihihehe on 11/7/2017.
 */

public class Logout {
    //private Activity mActivity;
    private static RestManager apiLogout;
    private static boolean isLogout = false;
    //private FragmentManager fragmentManager;

    public Logout() {
    }

    /* public Logout(Activity mActivity) {
         this.mActivity = mActivity;
         SharedPreferences sharedPreferences = this.mActivity.getSharedPreferences(SessionManager.PREF_NAME_LOGIN, Context.MODE_PRIVATE);
         int userId = sharedPreferences.getInt(SessionManager.USER_ID, 3);
         String sessionId = sharedPreferences.getString(SessionManager.KEY_SESSION, "");
     }*/
    public static boolean actionLogout(final Activity activity, final FragmentManager fragmentManager) {
        //final boolean isLogout=false;
        final SharedPreferences sharedPreferences = activity.getSharedPreferences(SessionManager.PREF_NAME_LOGIN, Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt(SessionManager.USER_ID, 3);
        String sessionId = sharedPreferences.getString(SessionManager.KEY_SESSION, "");
        apiLogout.getApiService().signOut(sessionId).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                Log.d("LogOut","success");
                if (response.isSuccessful() && response.body().getStatus().getError()!=null) {
                    if(response.body().getStatus().getError()==0) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(SessionManager.USER_ID);
                        editor.remove(SessionManager.KEY_SESSION);
                        editor.putBoolean(SessionManager.IS_LOGIN, false);

                        editor.commit();
                        /*activity.finish();
                        fragmentManager
                                .beginTransaction()
                                .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                                .replace(R.id.frameContainer, new Signin_Fragment()).commit();

             */           isLogout = true;
                    }
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                Log.d("Logout","failure");
            }
        });
        activity.finish();
        return isLogout;
    }
}
