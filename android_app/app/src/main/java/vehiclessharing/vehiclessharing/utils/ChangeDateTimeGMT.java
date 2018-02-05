package vehiclessharing.vehiclessharing.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Hihihehe on 1/13/2018.
 */

public class ChangeDateTimeGMT {
    public static String convertToDate(String stringData)
            throws java.text.ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);//yyyy-MM-dd'T'HH:mm:ss
        SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        DateFormat gmtFormat = new SimpleDateFormat();

        Date initDate = sdf.parse(stringData);

        TimeZone gmtTime = TimeZone.getTimeZone("GMT");
        gmtFormat.setTimeZone(gmtTime);

        String formattedTime = gmtFormat.format(initDate);
        return formattedTime;
    }


}
