package com.zappsit.faceone;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by bfillon on 22/12/2016.
 */

public interface SalesForceLoginClient {
    @Multipart
    @POST("oauth2/token")
    Call<ResponseBody> getToken(@Part("grant_type") RequestBody grantType, @Part("client_id") RequestBody clientId,
                                 @Part ("client_secret") RequestBody clientSecret, @Part ("username") RequestBody userName,
                                @Part ("password") RequestBody password
    );
}
