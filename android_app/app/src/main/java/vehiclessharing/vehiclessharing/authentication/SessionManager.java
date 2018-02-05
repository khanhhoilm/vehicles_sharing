package vehiclessharing.vehiclessharing.authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import vehiclessharing.vehiclessharing.view.activity.SigninActivity;

/**
 * Created by Hihihehe on 9/23/2017.
 */

public class SessionManager {
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;

    public static final String PREF_NAME_LOGIN = "vsharing_is_login";

    public static final String IS_LOGIN = "is_loggedin";

    public static final String KEY_NAME = "name";

    public static final String KEY_SESSION = "session_id";
    public static final String USER_ID="user_id";


    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME_LOGIN, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void createLoginSession(int userId, String session_id){
        editor.putBoolean(IS_LOGIN, true);

        editor.putInt(USER_ID, userId);

        editor.putString(KEY_SESSION, session_id);
        editor.commit();
    }

    public void logoutUser(){

        editor.clear();
        editor.commit();


        Intent signInIntent = new Intent(_context, SigninActivity.class);

        signInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        signInIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(signInIntent);
    }
    public boolean isLoggedIn(){
        Log.d("isLoggedIn","Sessionid: "+pref.getString(KEY_SESSION,""));
        return pref.getBoolean(IS_LOGIN, false);
    }
}
