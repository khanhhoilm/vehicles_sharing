package vehiclessharing.vehiclessharing.model;

/**
 * Created by Hihihehe on 9/27/2017.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("api_token")
    @Expose
    private String apiToken;
    @SerializedName("user_info")
    @Expose
    private User userInfo;

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public User getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(User userInfo) {
        this.userInfo = userInfo;
    }
}
