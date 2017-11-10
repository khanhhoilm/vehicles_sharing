package vehiclessharing.vehiclessharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hihihehe on 11/10/2017.
 */

public class ResultSendRequest {
    @SerializedName("status")
    @Expose
    private Status status;

    @SerializedName("FCM_info")
    @Expose
    private FCMData fcmData;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public FCMData getFcmData() {
        return fcmData;
    }

    public void setFcmData(FCMData fcmData) {
        this.fcmData = fcmData;
    }
}
