package vehiclessharing.vehiclessharing.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Hihihehe on 1/17/2018.
 */

public class CheckCamera {
    public static int REQUEST_CAMERA=30;
    public static boolean checkCamera(Context mContext, Activity mActivity) {
        int checkPermissionCallPhone = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);

        if (checkPermissionCallPhone != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    mActivity,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);

            return false;
        } else {
            return true;
        }
    }

}
