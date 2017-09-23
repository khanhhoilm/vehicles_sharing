package vehiclessharing.vehiclessharing;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import vehiclessharing.vehiclessharing.activity.MainActivity;

/**
 * Created by Hihihehe on 9/10/2017.
 */

public class VSharing extends Application{
    public static Context mContext;
    public static android.support.v7.app.ActionBar	actionBar;
    public static AssetManager mAssetManager;
    public static Resources mResource;
    public static boolean isTabletVersion;
    public static SharedPreferences globalPrefs;
    public static TextView actionBarTitleTV;
    private static final String PROPERTY_ID = "UA-46730129-2";
    private static int forward=0;

    public static int getForward() {
        return forward;
    }

    public static void setForward(int forward) {
        VSharing.forward = forward;
    }

    // Logging TAG
    private static final String TAG = "TUOI TRE";

    public static int GENERAL_TRACKER = 0;

    @Override
    public void onCreate() {

        super.onCreate();
        mContext = getApplicationContext();
        mAssetManager = getAssets();
        mResource = getResources();
        //isTabletVersion = mResource.getBoolean(R.bool.isTablet);
     //   isTabletVersion = mResource.getBoolean(R.bool.isTablet);
       // mInstance=this;
        //isTabletVersion = false;
        globalPrefs = mContext.getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);

    }

    public static boolean isNetworkAvailable(Context context) {

        try {
            Process p1 = Runtime.getRuntime().exec(
                    "ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal == 0);
            return reachable;
        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;

    }

    public static void globalToast(String toastMessage) {

        Toast.makeText(mContext, toastMessage, Toast.LENGTH_SHORT).show();

    }
    public static void globalToastCheck3G() {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);


        if ((tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSPAP)) {
            Log.d("Type", "3g");// for 3g HSDPA networktype will be return as
            Toast.makeText(mContext, "Bạn Đang Dùng 3G Để Mở Video", Toast.LENGTH_SHORT).show();
            // and speed will also matters to decide 3g network type

        }
        //if(mobileConnected){
        //	Toast.makeText(mContext, "Bạn Đang Dùng 3G Để Mở Video", Toast.LENGTH_SHORT).show();
        //}else
        //{
        //	Toast.makeText(mContext, "false", Toast.LENGTH_SHORT).show();
        //}


    }
  /*  public static synchronized VSharing getInstance() {
        return mInstance;
    }
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

*/
    public static Context getGlobalContext() {
        return mContext;
    }

    public static String loadJSONFromAsset(String jsonFileName) {

        String json = null;

        try {

            InputStream is = mAssetManager.open(jsonFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {

            ex.printStackTrace();
            return null;

        } catch (Exception ex) {

            ex.printStackTrace();
            return null;

        }

        return json;

    }

    public static int getId(String resourceName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resourceName);
            return idField.getInt(idField);
        } catch (Exception e) {
            throw new RuntimeException("No resource ID found for: "
                    + resourceName + " / " + c, e);
        }
    }

    public static void setOrientation(Activity activity) {

        if (!isTabletVersion) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg:
        // roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a
        // company.
    }

//	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
//
//	synchronized Tracker getTracker(TrackerName trackerId) {
//		if (!mTrackers.containsKey(trackerId)) {
//
//			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
//			Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics
//					.newTracker(R.xml.app_tracker)
//					: (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics
//							.newTracker(PROPERTY_ID) : analytics
//							.newTracker(R.xml.ecommerce_tracker);
//			mTrackers.put(trackerId, t);
//
//		}
//		return mTrackers.get(trackerId);
//	}

    public static String getDeviceUniqueID() {

        String identifier = null;
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null)
            identifier = tm.getDeviceId();
        if (identifier == null || identifier.length() == 0)
            identifier = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);

        return identifier;

    }

    public static android.support.v7.app.ActionBar getActionBar() {
        return actionBar;
    }

    public static void setActionBar(android.support.v7.app.ActionBar actionBar2) {
        VSharing.actionBar = actionBar2;
    }

    public static String getActionBarTitleTV() {
        if(actionBarTitleTV.getText()!=null){

            return actionBarTitleTV.getText().toString();

        }
        return "";
    }

    public static void setActionBarTitleTV(TextView actionBarTitleTV2) {
        VSharing.actionBarTitleTV = actionBarTitleTV2;
    }


}
