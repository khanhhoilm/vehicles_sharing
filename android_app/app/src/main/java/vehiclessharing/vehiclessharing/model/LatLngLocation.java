package vehiclessharing.vehiclessharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hihihehe on 10/15/2017.
 */

public class LatLngLocation {
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;

    public LatLngLocation() {
    }

    public LatLngLocation(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String convertLatLngToStringToDatabase()
    {
        return this.lat+";"+this.lng;
    }
    public static LatLngLocation convertStringFromDatabaseToLatLng(String latLng)
    {
        String latLngLocation[]=latLng.split(";");
        return new LatLngLocation(latLngLocation[0],latLngLocation[1]);
    }
}
