package com.zappsit.faceone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zappsit.faceone.dtos.DtoDetect;
import com.zappsit.faceone.dtos.DtoLoginSalesForce;
import com.zappsit.faceone.dtos.DtoSendImageToSalesForce;

import java.io.ByteArrayOutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.System.out;

public class Main2Activity extends AppCompatActivity {

    private int progressStatus = 0;
    private String faceset_token = "fc8bc42265d7d8571fc90cd9e016f2c1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void processMegviiCalls(View view) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ImageView image = (ImageView) findViewById(R.id.imageView2);
            image.setImageBitmap(imageBitmap);

            callSalesForceToUpdateContact(imageBitmap);
            //callDetectMegviiAPI(imageBitmap);
        }
    }

    private void callSalesForceToUpdateContact(final Bitmap image) {
        final TextView textView = (TextView) findViewById(R.id.textView3);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        SalesForceLoginClient client = SalesForceServiceGenerator.createService(SalesForceLoginClient.class);
        textView.setText("Step 1/5: Connecting to Salesforce");
        progressBar.setProgress(10);

        //set OAuth key and secret variables
        String sfdcConsumerKey = "3MVG9HxRZv05HarSNNru046ZmSfpqr8ZKwZF9A_lh2WFMbNbRhjgVsiXApDJG9sPSYbsPtGkSS49BKyIGjpWC";
        String sfdcConsumerSecret = "1949525149160328750";

        //set to Force.com user account that has API access enabled
        String sfdcUserName = "benoit.fillon@free.fr";
        String sfdcPassword = "elmagi8kFc!";
        String sfdcToken = "nbLIh9gGodn6snYmCUMzEu7Vd"; //"f4SjYKXHrVG2bI5gQFxUo6h8O";

        //create login password value
        String loginPassword = sfdcPassword + sfdcToken;

        RequestBody grantType = RequestBody.create(MediaType.parse("multipart/form-data"), "password");
        RequestBody clientId = RequestBody.create(MediaType.parse("multipart/form-data"), sfdcConsumerKey);
        RequestBody clientSecret = RequestBody.create(MediaType.parse("multipart/form-data"), sfdcConsumerSecret);

        RequestBody userName = RequestBody.create(MediaType.parse("multipart/form-data"), sfdcUserName);
        RequestBody password = RequestBody.create(MediaType.parse("multipart/form-data"), loginPassword);

        Call<ResponseBody> call = client.getToken(grantType, clientId, clientSecret, userName, password);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    Gson gson = new GsonBuilder().create();
                    DtoLoginSalesForce dto = gson.fromJson(responseBody.charStream(), DtoLoginSalesForce.class);

                    out.println(dto.getInstanceUrl());

                    if (dto.getInstanceUrl().equals("https://eu11.salesforce.com")) {

                        textView.setText("Step 1/5: Connecting to Salesforce - OK");
                        progressBar.setProgress(15);

                        sendImageToSalesForce(image, dto.getInstanceUrl(), dto.getTokenType(), dto.getAccessToken());

                    }
                    else {
                        textView.setText("Failure when calling SalesForce Login API: please resume");
                        progressBar.setProgress(0);
                    }
                }
                else {
                    textView.setText("Failure on Salesforce login: please resume");
                    progressBar.setProgress(0);

                    out.println(response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                out.println("KO: " + throwable.getMessage());
                out.println(throwable.getStackTrace());

                textView.setText("Failure when calling Detect API: please resume");
                progressBar.setProgress(0);
            }
        });

    }

    private void sendImageToSalesForce(final Bitmap image, String serviceUrl, String tokenType, String oAuthToken) {
        final TextView textView = (TextView) findViewById(R.id.textView3);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        SalesForceInstance client = SalesForceInstanceServiceGenerator.createService(SalesForceInstance.class);
        textView.setText("Step 2/5: Sending image to Salesforce");
        progressBar.setProgress(20);

        // encoding image in base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        DtoSendImageToSalesForce dto = new DtoSendImageToSalesForce();
        dto.setEncodedImage(encodedImage);

        Call<ResponseBody> call = client.sendImage(tokenType + " " + oAuthToken, "benoit.fillon@zags.com", dto);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    textView.setText("Step 2/5: Sending image to Salesforce - OK");
                    progressBar.setProgress(25);

                    callDetectMegviiAPI(image);
                }
                else {
                    textView.setText("Failure on Sending image to Salesforce : please resume");
                    out.println(response.errorBody().toString());
                    progressBar.setProgress(0);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                out.println("KO: " + throwable.getMessage());
                out.println(throwable.getStackTrace());

                textView.setText("Failure when sending image to Salesforce: please resume");
                progressBar.setProgress(0);
            }
        });

    }

    private void callDetectMegviiAPI(Bitmap image) {
        final TextView textView = (TextView) findViewById(R.id.textView3);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        textView.setText("Step 3/5: Send image and call Detect");
        progressBar.setProgress(30);

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
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    Gson gson = new GsonBuilder().create();
                    DtoDetect dto = gson.fromJson(responseBody.charStream(), DtoDetect.class);

                    if (dto.getFaces().length == 1) {
                        String face_token = dto.getFaces()[0].getFace_token();

                        textView.setText("Step 3/5: Send image and call Detect - OK");
                        progressBar.setProgress(33);

                        setUserId(face_token);

                    }
                    else {
                        textView.setText("Failure when calling Detect API: please resume");
                        progressBar.setProgress(0);
                    }
                }
                else {
                    textView.setText("Failure when calling Detect API: please resume");
                    progressBar.setProgress(0);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                out.println("KO: " + throwable.getMessage());
                out.println(throwable.getStackTrace());

                textView.setText("Failure when calling Detect API: please resume");
                progressBar.setProgress(0);
            }
        });
    }

    private void setUserId(final String face_token) {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        final TextView textView = (TextView) findViewById(R.id.textView3);

        textView.setText("Step 4/5: Call SetUserId");
        progressBar.setProgress(43);

        // Get text entered
        EditText username = (EditText) findViewById(R.id.editText2);
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
                    textView.setText("Step 4/5: Call SetUserId - OK");

                    progressBar.setProgress(66);
                    associateWithFaceSetId(face_token);

                } else {
                    textView.setText("Failure when calling SetUserId API: please resume");
                    progressBar.setProgress(0);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                out.println("KO: " + throwable.getMessage());
                out.println(throwable.getStackTrace());

                textView.setText("Failure when calling SetUserId API: please resume");
                progressBar.setProgress(0);
            }
        });
    }

    public void associateWithFaceSetId(String face_token) {
        final TextView textView = (TextView) findViewById(R.id.textView3);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        progressBar.setProgress(76);

        textView.setText("Step 5/5: Call AddFace");

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

                if (response.isSuccessful()) {
                    textView.setText("Step 5/5: Call AddFace");
                    progressBar.setProgress(100);
                    Toast.makeText(getApplicationContext(), "Process completed successfully!",Toast.LENGTH_LONG).show();
                }
                else {
                    textView.setText("Failure when calling AddFace API: please resume");
                    progressBar.setProgress(0);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                out.println("KO: " + throwable.getMessage());
                out.println(throwable.getStackTrace());

                textView.setText("Failure when calling AddFace API: please resume");
                progressBar.setProgress(0);
            }
        });
    }
}
