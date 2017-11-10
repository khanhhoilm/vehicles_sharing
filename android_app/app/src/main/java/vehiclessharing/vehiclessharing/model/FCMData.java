package vehiclessharing.vehiclessharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hihihehe on 11/10/2017.
 */

public class FCMData {
    @SerializedName("token_success_number")
    @Expose
    private Integer resultPush;

    public Integer getResultPush() {
        return resultPush;
    }

    public void setResultPush(Integer resultPush) {
        this.resultPush = resultPush;
    }
}
