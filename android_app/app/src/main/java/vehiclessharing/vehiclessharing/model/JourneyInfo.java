package vehiclessharing.vehiclessharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hihihehe on 12/5/2017.
 */

public class JourneyInfo {
    @SerializedName("start_time")
    @Expose
    private String startTime;

    @SerializedName("detail")
    @Expose
    private DetailJourney detail;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public DetailJourney getDetail() {
        return detail;
    }

    public void setDetail(DetailJourney detail) {
        this.detail = detail;
    }
}
