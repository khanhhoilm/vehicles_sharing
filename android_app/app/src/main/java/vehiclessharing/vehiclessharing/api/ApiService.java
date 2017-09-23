package vehiclessharing.vehiclessharing.api;



import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import vehiclessharing.vehiclessharing.model.SignInResult;
import vehiclessharing.vehiclessharing.model.SignUpResult;


public interface ApiService {

    String BASE_URL = "http://vehiclessharing.viecit.co/";

    @POST("users")
    @FormUrlEncoded
    Call<SignUpResult> signUp(@Field("phone") String phone,
                              @Field("name") String name,
                              @Field("password") String password,
                              @Field("gender") int gender);

    @POST("users/signin")
    @FormUrlEncoded
    Call<SignInResult> signIn(@Field("phone") String phone,
                              @Field("password") String password);

    /*@GET("/appdev/catpage?token=dee03981&cid=2&limit=10&page=1")
    Call<List<ItemCategory>> getItemByCategory();

    @GET("/appdev/catpage?token=dee03981")
    Call<List<ItemCategory>> getItemByCategoryDynamic(@Query("cid") int cid, @Query("limit") int limit, @Query("page") int page, @Query("prior") int prior);

    @GET("/appdev/getobjectdetail?token=dee03981")
    Call<ItemDetail> getArticleDetail(@Query("id") String id);*/


}
