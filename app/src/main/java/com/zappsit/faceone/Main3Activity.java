package com.zappsit.faceone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zappsit.faceone.dtos.DtoDetect;
import com.zappsit.faceone.dtos.DtoLoginSalesForce;
import com.zappsit.faceone.dtos.DtoSendImageToSalesForce;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.System.out;

public class Main3Activity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private AccessTokenTracker mAccessTokenTracker;
    private Bitmap bitmapToPlayWith;
    private String faceset_token = "fc8bc42265d7d8571fc90cd9e016f2c1";

    private void loginToMyFbApp() {
        if (AccessToken.getCurrentAccessToken() != null) {
            mAccessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                    mAccessTokenTracker.stopTracking();
                    if(currentAccessToken == null) {
                        //(the user has revoked your permissions -
                        //by going to his settings and deleted your app)
                        //do the simple login to FaceBook
                        //case 1
                        DoSimpleLoginToFaceBook();
                    }
                    else {
                        //you've got the new access token now.
                        //AccessToken.getToken() could be same for both
                        //parameters but you should only use "currentAccessToken"
                        //case 2
                        fetchProfile();
                    }
                }
            };
            mAccessTokenTracker.startTracking();
            AccessToken.refreshCurrentAccessTokenAsync();
        }
        else {
            //do the simple login to FaceBook
            DoSimpleLoginToFaceBook();
        }
    }

    private void DoSimpleLoginToFaceBook() {
        LoginButton loginButton = (LoginButton) findViewById(R.id.buttonFaceBook);
        loginButton.setReadPermissions(Arrays.asList("public_profile"));

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                out.println(loginResult.getAccessToken().toString());
                if(Profile.getCurrentProfile() == null) {
                    profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            // profile2 is the new profile
                            //Log.v("facebook - profile", profile2.getFirstName());
                            profileTracker.stopTracking();
                        }
                    };
                    // no need to call startTracking() on mProfileTracker
                    // because it is called by its constructor, internally.
                }
                else {
                    Profile profile = Profile.getCurrentProfile();
                    //Log.v("facebook - profile", profile.getFirstName());
                }
            }

            @Override
            public void onCancel() {
                // App code
                out.println("Cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                out.println(exception.getMessage());
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        loginToMyFbApp();
    }


    public void processMegviiCalls(View view) {
        ImageView imageViewDebug = (ImageView) findViewById(R.id.imageViewDebug);

        callSalesForceToUpdateContact(((BitmapDrawable)imageViewDebug.getDrawable()).getBitmap());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            callbackManager.onActivityResult(requestCode, resultCode, data);


    }

    //Debug Hash Android: wI7QfPQ73l54buZ30VWAe0SYZTU=
    //Generated with: keytool -exportcert -alias androiddebugkey -keystore \Users\bfillon\.android\debug.keystore | C:\Users\bfillon\Downloads\openssl-1.0.2j-x64_86-win64\openssl.exe sha1 -binary | C:\Users\bfillon\Downloads\openssl-1.0.2j-x64_86-win64\openssl.exe base64


    private void fetchProfile() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // this is where you should have the profile
                        out.println("fetched info " + object.toString());
                        try {
                            //imageViewFB
                            ProfilePictureView profilePictureView;


                            profilePictureView = (ProfilePictureView) findViewById(R.id.imageprofile);
                            profilePictureView.setPresetSize(ProfilePictureView.LARGE);
                            //profilePictureView.setDrawingCacheEnabled(true);
                            profilePictureView.setProfileId(object.getString("id"));

                            //ImageView fbImage = ( ( ImageView)profilePictureView.getChildAt( 0));
                            //bitmap  = ( (BitmapDrawable) fbImage.getDrawable()).getBitmap();
                            //Bitmap bitmap = profilePictureView.getDrawingCache();

                            String profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                            ImageView imageViewDebug = (ImageView) findViewById(R.id.imageViewDebug);
                            new DownloadImageTask(imageViewDebug, bitmapToPlayWith).execute(profilePicUrl);

                            //ImageView v = (ImageView) findViewById(R.id.imageViewFB);
                            //v.setImageBitmap(getFacebookProfilePicture(object.getString("id")));


                        } catch (org.json.JSONException e){
                            out.println(e.getMessage());
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,picture.type(large)"); //write the fields you need
        request.setParameters(parameters);
        request.executeAsync();
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

        EditText username = (EditText) findViewById(R.id.editText2);
        String contactEmail = username.getText().toString();

        Call<ResponseBody> call = client.sendImage(tokenType + " " + oAuthToken, contactEmail, dto);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();

                    String contactId = "";
                    try {
                        contactId = responseBody.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out.println(contactId);
                    contactId = contactId.replace("\"", "");
                    out.println(contactId);
                    //Gson gson = new GsonBuilder().create();
                    //String contactId = gson.fromJson(responseBody.charStream(), String.class);

                    textView.setText("Step 2/5: Sending image to Salesforce - OK");
                    progressBar.setProgress(25);

                    callDetectMegviiAPI(image, contactId);
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

    private void callDetectMegviiAPI(Bitmap image, final String contactId) {
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

                        setUserId(face_token, contactId);

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

    private void setUserId(final String face_token, String contactId) {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        final TextView textView = (TextView) findViewById(R.id.textView3);

        textView.setText("Step 4/5: Call SetUserId");
        progressBar.setProgress(43);

        // Get text entered

        String userId = contactId;


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
