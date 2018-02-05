package vehiclessharing.vehiclessharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hihihehe on 12/4/2017.
 */

public class UserInfo {
    @SerializedName("status")
    @Expose
    private Status status;

    @SerializedName("information")
    @Expose
    private InfomationUser infomationUser;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public InfomationUser getInfomationUser() {
        return infomationUser;
    }

    public void setInfomationUser(InfomationUser infomationUser) {
        this.infomationUser = infomationUser;
    }
}
