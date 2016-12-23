package com.zappsit.faceone.dtos;

/**
 * Created by bfillon on 22/12/2016.
 */

public class DtoLoginSalesForce {

    // {"access_token":"00D0Y000000pBVh!ARQAQAyaxe.ewKAETlRu0Z6JQjw8Pe3_pyzX8qlCLGxGTBcuB1Ljq.jOzfUDPy7fSqCq8oOoHoiG6cRKqN20gg.zyxMV5zyS",
    // "instance_url":"https://eu11.salesforce.com",
    // "id":"https://login.salesforce.com/id/00D0Y000000pBVhUAM/0050Y000000RWYJQA4",
    // "token_type":"Bearer",
    // "issued_at":"1482408950977",
    // "signature":"SbVMlqKYW6JKOT419Uu9js4/V6VaOANwRKqLkrZrOS8="}

    private String access_token;
    private String instance_url;
    private String id;
    private String token_type;
    private String issued_at;
    private String signature;

    public String getAccessToken() {
        return access_token;
    }

    public String getInstanceUrl() {
        return instance_url;
    }

    public String getId() {
        return id;
    }

    public String getTokenType() {
        return token_type;
    }

    public String getIssuedAt() {
        return issued_at;
    }

    public String getSignature() {
        return signature;
    }


}
