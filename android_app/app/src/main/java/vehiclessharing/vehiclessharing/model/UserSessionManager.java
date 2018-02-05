package vehiclessharing.vehiclessharing.model;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import co.vehiclessharing.R;


/**
 * Created by Tuan on 20/03/2017.
 */

public class UserSessionManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    //Context
    private Context context;

    // Google plus Client
    static public GoogleApiClient mGoogleApiClient;

    // Constructor
    public UserSessionManager(Context context){
        this.context = context;

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getResources().getString(R.string.default_web_client_id1))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this).build();
        mGoogleApiClient.connect();
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("google base class", "onConnected invoked");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("google base class", "onConnectionSuspended invoked");
//        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("google base class", "onConnectionFailed invoked");
    }
}
