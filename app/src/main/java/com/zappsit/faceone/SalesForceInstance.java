package com.zappsit.faceone;

import com.zappsit.faceone.dtos.DtoSendImageToSalesForce;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by bfillon on 22/12/2016.
 */

public interface SalesForceInstance {

    @POST("apexrest/ContactsToUpdate/")
    Call<ResponseBody> sendImage(@Header("Authorization") String authorization, @Query("id") String id, @Body DtoSendImageToSalesForce imageAsBase64);
}
