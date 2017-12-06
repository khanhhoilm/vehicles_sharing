package vehiclessharing.vehiclessharing.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import vehiclessharing.vehiclessharing.model.RequestResult;
import vehiclessharing.vehiclessharing.model.ResultSendRequest;
import vehiclessharing.vehiclessharing.model.SignInResult;
import vehiclessharing.vehiclessharing.model.StartStripResponse;
import vehiclessharing.vehiclessharing.model.StatusResponse;
import vehiclessharing.vehiclessharing.model.UserInfo;


public interface ApiService {

    // String BASE_URL = "http://vehiclessharing.viecit.co/";
    String BASE_URL = "https://vehicle-sharing.herokuapp.com/";

    @POST("users/register")
    @FormUrlEncoded
    Call<StatusResponse> signUp(@Field("phone") String phone,
                                @Field("name") String name,
                                @Field("password") String password,
                                @Field("gender") int gender);

    @POST("users/signin")
    @FormUrlEncoded
    Call<SignInResult> signIn(@Field("phone") String phone,
                              @Field("password") String password);

    @POST("users/signout")
    @FormUrlEncoded
    Call<StatusResponse> signOut(@Field("api_token") String apiToken);

    @POST("users/update")
    @FormUrlEncoded
    Call<StatusResponse> updateInfoUser(@Field("api_token") String apiToken, @Field("name") String name, @Field("email") String email,
                                        @Field("avatar_link") String avatarLink, @Field("password") String password, @Field("gender") int gender,
                                        @Field("address") String address, @Field("birthday") String birthday);

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("api/request")
    Call<RequestResult> registerRequest(@Field("user_id") int userId, @Field("source_location") String sourceLocation,
                                        @Field("destination_location") String desLocation, @Field("time_start") String timeStart,
                                        @Field("api_token") String session, @Field("device_id") String devideId, @Field("vehicle_type") int vehicleType,
                                        @Field("device_token") String deviceToken);


    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("api/send-request")
    Call<ResultSendRequest> sendRequestTogether(@Field("api_token") String apiToken, @Field("receiver_id") int receiverId, @Field("note") String note);

    @FormUrlEncoded
    @POST("api/cancel-request")
    Call<StatusResponse> cancelRequest(@Field("api_token") String apiToken);

    @FormUrlEncoded
    @POST("api/confirm-request")
    Call<StatusResponse> confirmRequest(@Field("api_token") String apiToken, @Field("sender_id") int senderId, @Field("confirm_id") int confirmId);

    @FormUrlEncoded
    @POST("api/start-the-trip")
    Call<StartStripResponse> startTheTrip(@Field("api_token") String apiToken);

    @FormUrlEncoded
    @POST("users/show")
    Call<UserInfo> getUserInfo(@Field("api_token") String apiToken, @Field("user_id") int userId);

    /*{
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
    }
}*/

    @FormUrlEncoded
    @POST
    Call<StatusResponse> ratingUserTogether(@Field("api_token") String apiToken, @Field("journey_id") int journeyId,
                                            @Field("rating_value") int ratingValue, @Field("comment") String comment);


}
