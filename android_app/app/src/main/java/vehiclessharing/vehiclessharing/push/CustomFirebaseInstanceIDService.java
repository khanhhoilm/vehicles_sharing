package vehiclessharing.vehiclessharing.push;

import android.content.SharedPreferences;
import android.net.http.RequestQueue;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Hihihehe on 11/6/2017.
 */


public class CustomFirebaseInstanceIDService extends FirebaseInstanceIdService{
    private static final String TAG = CustomFirebaseInstanceIDService.class.getSimpleName();
    public static String DEVICE_TOKEN_REFRESH="device_token_refresh";
    public static String DEVICE_TOKEN="device_token";
    private RequestQueue queue;
  //  private TokenObject tokenObject;
  //  private MySharedPreference mySharedPreference;
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Token Value: " + refreshedToken);
        SharedPreferences deviceTokenShare=getSharedPreferences(DEVICE_TOKEN_REFRESH,MODE_PRIVATE);
        SharedPreferences.Editor editor=deviceTokenShare.edit();
        editor.putString(DEVICE_TOKEN,refreshedToken);
        editor.commit();
      //  sendTheRegisteredTokenToWebServer(refreshedToken);
    }
    private void sendTheRegisteredTokenToWebServer(final String token){
 /*queue = Volley.newRequestQueue(getApplicationContext());
        mySharedPreference = new MySharedPreference(getApplicationContext());
        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, Helper.PATH_TO_SERVER_IMAGE_UPLOAD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                tokenObject = gson.fromJson(response, TokenObject.class);
                if (null == tokenObject) {
                    Toast.makeText(getApplicationContext(), "Can't send a token to the server", Toast.LENGTH_LONG).show();
                    mySharedPreference.saveNotificationSubscription(false);
                } else {
                    Toast.makeText(getApplicationContext(), "Token successfully send to server", Toast.LENGTH_LONG).show();
                    mySharedPreference.saveNotificationSubscription(true);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Helper.TOKEN_TO_SERVER, token);
                return params;
            }
        };
        queue.add(stringPostRequest);
*/
    }
}
