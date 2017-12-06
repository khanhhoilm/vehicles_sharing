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

    @SerializedName("user_info")
    @Expose
    private User infoUser;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getInfoUser() {
        return infoUser;
    }

    public void setInfoUser(User infoUser) {
        this.infoUser = infoUser;
    }
}
