package vehiclessharing.vehiclessharing.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hihihehe on 12/5/2017.
 */

public class RatingResponse {
    /*
    "status": {
        "error": 0,
        "message": "Success"
    },
    "rating_info": {
        "total_rating": 3,
        "user_info": {
            "id": 1,
            "phone": "0939267597",
            "name": "Hội Khánh",
            "email": null,
            "google_id": null,
            "facebook_id": null,
            "avatar_link": null,
            "gender": 1,
            "address": null,
            "birthday": null
        }
    }*/
    @SerializedName("status")
    @Expose
    private Status status;
}
