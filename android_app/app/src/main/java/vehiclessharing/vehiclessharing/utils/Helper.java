package vehiclessharing.vehiclessharing.utils;

import com.google.android.gms.maps.model.LatLng;

import vehiclessharing.vehiclessharing.model.LatLngLocation;

/**
 * Created by Hihihehe on 10/6/2017.
 */

public class Helper {
    public static LatLng convertLatLngLocationToLatLng(LatLngLocation latLngLocation)
    {
        return new LatLng(Double.parseDouble(latLngLocation.getLat()),Double.parseDouble(latLngLocation.getLng()));
    }
    public static LatLngLocation convertLatLngToLatLngLocation(LatLng latLng)
    {
        LatLngLocation latLngLocation=new LatLngLocation();
        latLngLocation.setLat(String.valueOf(latLng.latitude));
        latLngLocation.setLng(String.valueOf(latLng.longitude));
        return latLngLocation;
    }
}
