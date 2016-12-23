package com.zappsit.faceone;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by bfillon on 22/12/2016.
 */

public class SalesForceServiceGenerator {

    public static final String API_BASE_URL = "https://login.salesforce.com/services/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder().readTimeout(1, TimeUnit.MINUTES).connectTimeout(1, TimeUnit.MINUTES);

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }

}
