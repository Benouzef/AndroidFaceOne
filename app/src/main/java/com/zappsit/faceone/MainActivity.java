package com.zappsit.faceone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zappsit.faceone.dtos.DtoDetect;
import com.zappsit.faceone.dtos.DtoGetFaceSets;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.System.out;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private String face_token;
    private String faceset_token;

    public void switchToV2(View view) {
        Intent intent= new Intent(this,Main2Activity.class);
        startActivity(intent);
    }


    public void getFaceSet(View view) {
        MegviiClient client = ServiceGenerator.createService(MegviiClient.class);

        RequestBody api_key = RequestBody.create(MediaType.parse("multipart/form-data"), "oxzC5V_7DvpM7uNQITr2ICdBKs1S1f2V");
        RequestBody api_secret = RequestBody.create(MediaType.parse("multipart/form-data"), "IElJ3_FXpUrOxeFMYzcUziNLQq-WLX3W");

        Call<ResponseBody> call = client.getFaceSetForFaceOne(api_key, api_secret);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                out.println("OK");
                out.println(response.code());

                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    Gson gson = new GsonBuilder().create();
                    DtoGetFaceSets dto = gson.fromJson(responseBody.charStream(), DtoGetFaceSets.class);



                    if (dto.getFacesets().length == 1) {
                        faceset_token = dto.getFacesets()[0].getFaceset_token();
                        out.println(faceset_token);
                        Toast.makeText(getApplicationContext(), "Récupération du FaceSet_token effectuée",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        faceset_token = "";
                        Toast.makeText(getApplicationContext(), "Récupération du FaceSet_token échouée",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Récupération du FaceSet_token : erreur",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                out.println(throwable.getMessage());
                out.println(throwable.getStackTrace());
                out.println("KO");
                Toast.makeText(getApplicationContext(), "Récupération du FaceSet_token : échec de communication",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Called when the user clicks the Send button */
    public void associateWithFaceSetId(View view) {
        // Do something in response to button
        // new SendPostRequest().execute();
        MegviiClient client = ServiceGenerator.createService(MegviiClient.class);

        RequestBody api_key = RequestBody.create(MediaType.parse("multipart/form-data"), "oxzC5V_7DvpM7uNQITr2ICdBKs1S1f2V");
        RequestBody api_secret = RequestBody.create(MediaType.parse("multipart/form-data"), "IElJ3_FXpUrOxeFMYzcUziNLQq-WLX3W");



        RequestBody facesettoken = RequestBody.create(MediaType.parse("multipart/form-data"), faceset_token);
        RequestBody tokens = RequestBody.create(MediaType.parse("multipart/form-data"), face_token);

        Call<ResponseBody> call = client.addFaceForFaceOne(api_key, api_secret, facesettoken, tokens);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                out.println("OK");
                out.println(response.code());

                if (response.isSuccessful()) {
                    out.println("OK BIS!");
                    Toast.makeText(getApplicationContext(), "associateWithFaceSetId : succès",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "associateWithFaceSetId : erreur",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                out.println(throwable.getMessage());
                out.println(throwable.getStackTrace());
                out.println("KO");
                Toast.makeText(getApplicationContext(), "associateWithFaceSetId : échec de communication",Toast.LENGTH_SHORT).show();
            }
        });



    }

    /** Called when the user clicks the button "2- Set User Id"*/
    public void setUserId(View view) {

        // Get text entered
        EditText username = (EditText) findViewById(R.id.editText);
        String userId = username.getText().toString();

        MegviiClient client = ServiceGenerator.createService(MegviiClient.class);

        RequestBody api_key = RequestBody.create(MediaType.parse("multipart/form-data"), "oxzC5V_7DvpM7uNQITr2ICdBKs1S1f2V");
        RequestBody api_secret = RequestBody.create(MediaType.parse("multipart/form-data"), "IElJ3_FXpUrOxeFMYzcUziNLQq-WLX3W");
        RequestBody token = RequestBody.create(MediaType.parse("multipart/form-data"), face_token);
        RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), userId);

        Call<ResponseBody> call = client.setUserId(api_key, api_secret, user_id, token);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                out.println("OK");
                out.println(response.code());

                if (response.isSuccessful()) {
                    out.println("OK BIS !");
                    Toast.makeText(getApplicationContext(), "setUserId : succès",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "setUserId : erreur",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                out.println(throwable.getMessage());
                out.println(throwable.getStackTrace());
                out.println("KO");
                Toast.makeText(getApplicationContext(), "setUserId : échec de communication",Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Toutes les méthodes pour Detect sont ci-apres

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ImageView image = (ImageView) findViewById(R.id.imageView1);
            image.setImageBitmap(imageBitmap);

            callDetectMegviiAPI(imageBitmap);
        }
    }



    public void detect(View view) {
        // Do something in response to button

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 2);
        }
    }

    private void callDetectMegviiAPI(Bitmap image) {
        MegviiClient client = ServiceGenerator.createService(MegviiClient.class);

        RequestBody api_key = RequestBody.create(MediaType.parse("multipart/form-data"), "oxzC5V_7DvpM7uNQITr2ICdBKs1S1f2V");
        RequestBody api_secret = RequestBody.create(MediaType.parse("multipart/form-data"), "IElJ3_FXpUrOxeFMYzcUziNLQq-WLX3W");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] array = bos.toByteArray();
        MultipartBody.Part file = MultipartBody.Part.createFormData("image_file", "image.png",
                RequestBody.create(MediaType.parse("multipart/form-data"), array));


        Call<ResponseBody> call = client.detect(api_key, api_secret, file);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                out.println("OK");
                out.println(response.code());

                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    Gson gson = new GsonBuilder().create();
                    DtoDetect dto = gson.fromJson(responseBody.charStream(), DtoDetect.class);

                    out.println(dto.getImage_id());

                    if (dto.getFaces().length == 1) {
                        face_token = dto.getFaces()[0].getFace_token();
                        out.println(dto.getFaces()[0].getFace_token());

                        Toast.makeText(getApplicationContext(), "Récupération du Face_token effectuée",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        face_token = "";
                        Toast.makeText(getApplicationContext(), "Récupération du Face_token échouée",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Récupération du Face_token : erreur",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                out.println(throwable.getMessage());
                out.println(throwable.getStackTrace());
                out.println("KO");
                Toast.makeText(getApplicationContext(), "Récupération du Face_token : échec de communication",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
