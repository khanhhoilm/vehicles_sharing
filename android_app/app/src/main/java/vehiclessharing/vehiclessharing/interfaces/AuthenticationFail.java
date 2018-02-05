package vehiclessharing.vehiclessharing.interfaces;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import vehiclessharing.vehiclessharing.authentication.SessionManager;
import vehiclessharing.vehiclessharing.view.activity.MainActivity;
import vehiclessharing.vehiclessharing.database.DatabaseHelper;
import vehiclessharing.vehiclessharing.api.LogoutAPI;

/**
 * Created by Hihihehe on 1/13/2018.
 */

public class AuthenticationFail {
    public static void unAuthorized(Activity mActivity, android.support.v4.app.FragmentManager supportFragmentManager){
        LogoutAPI logoutAPI=new LogoutAPI();
        logoutAPI.actionLogout(mActivity, supportFragmentManager);
        DatabaseHelper mDatabase=new DatabaseHelper(mActivity);
        mDatabase.deleteAll();
        SessionManager sessionManager=new SessionManager(mActivity);
        sessionManager.logoutUser();
        SharedPreferences sharedPreferences=mActivity.getSharedPreferences(MainActivity.SCREEN_AFTER_BACK,Context.MODE_PRIVATE);
        SharedPreferences.Editor editorScreen=sharedPreferences.edit();
        editorScreen.putInt(MainActivity.SCREEN_NAME, MainActivity.MAIN_ACTIVITY);
        editorScreen.commit();
        mActivity.finish();
    }
}
