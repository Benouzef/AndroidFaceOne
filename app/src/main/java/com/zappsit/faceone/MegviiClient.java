package com.zappsit.faceone;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by bfillon on 26/11/2016.
 */

public interface MegviiClient {
    @Multipart
    @POST("detect")
    Call<ResponseBody> detect(@Part("api_key") RequestBody api_key, @Part("api_secret") RequestBody api_secret,
                              @Part MultipartBody.Part image);

    @Multipart
    @POST("face/setuserid")
    Call<ResponseBody> setUserId(@Part ("api_key") RequestBody api_key, @Part("api_secret") RequestBody api_secret,
                                 @Part ("user_id") RequestBody user_id, @Part ("face_token") RequestBody face_token);

    @Multipart
    @POST("faceset/getfacesets")
    Call<ResponseBody> getFaceSetForFaceOne(@Part ("api_key") RequestBody api_key, @Part("api_secret") RequestBody api_secret);

    @Multipart
    @POST("faceset/addface")
    Call<ResponseBody> addFaceForFaceOne(@Part ("api_key") RequestBody api_key, @Part("api_secret") RequestBody api_secret,
                                         @Part ("faceset_token") RequestBody faceset_token, @Part("face_tokens") RequestBody face_tokens);

}
