package bomba.com.mobiads.bamba.Services;

import bomba.com.mobiads.bamba.data.ApiResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by WAKY on 1/7/2017.
 */
public interface UsersService {
    @Multipart
    @POST("register_premium.php")
    Call<ApiResponse> premium_register(@Part("name") RequestBody name, @Part("phone") RequestBody phone, @Part("gender") RequestBody gender, @Part("location") RequestBody location, @Part MultipartBody.Part ringtone);

    @FormUrlEncoded
    @POST("register_std.php")
    Call<ApiResponse> normal_register(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("add_call.php")
    Call<ApiResponse> call_register(@Field("phone") String phone, @Field("duration") int duration, @Field("time") String time, @Field("recepient") String recepient);
}
