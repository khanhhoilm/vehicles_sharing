package vehiclessharing.vehiclessharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hihihehe on 12/5/2017.
 */

public class StartStripResponse {
    @SerializedName("status")
    @Expose
    private Status status;

    @SerializedName("journey_info")
    @Expose
    private JourneyInfo journeyInfo;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public JourneyInfo getJourneyInfo() {
        return journeyInfo;
    }

    public void setJourneyInfo(JourneyInfo journeyInfo) {
        this.journeyInfo = journeyInfo;
    }
}
