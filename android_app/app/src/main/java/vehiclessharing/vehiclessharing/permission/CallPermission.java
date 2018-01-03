package vehiclessharing.vehiclessharing.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Hihihehe on 12/29/2017.
 */

public class CallPermission {
    public final static int REQUEST_CALL_PHONE = 1;

    public static boolean checkCall(Context mContext, Activity mActivity) {
        int checkPermissionCallPhone = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE);

        if (checkPermissionCallPhone != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    mActivity,
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CALL_PHONE);

            return false;
        } else {
            return true;
        }
    }

    public static boolean checkCallSinch(Context mContext, Activity mActivity) {
        int checkPermissionCallPhone = ActivityCompat.checkSelfPermission(mContext, Manifest.permission_group.PHONE);
        if (checkPermissionCallPhone != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    mActivity,
                    new String[]{Manifest.permission_group.PHONE},
                    REQUEST_CALL_PHONE);

            return false;
        } else {
            checkPermissionCallPhone = ActivityCompat.checkSelfPermission(mContext, Manifest.permission_group.CONTACTS);
            if (checkPermissionCallPhone != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        mActivity,
                        new String[]{Manifest.permission_group.CONTACTS},
                        REQUEST_CALL_PHONE);

                return false;
            } else {
                checkPermissionCallPhone = ActivityCompat.checkSelfPermission(mContext, Manifest.permission_group.MICROPHONE);
                if (checkPermissionCallPhone != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            mActivity,
                            new String[]{Manifest.permission_group.MICROPHONE},
                            REQUEST_CALL_PHONE);

                    return false;
                }
            }
            return true;
        }
    }
}
