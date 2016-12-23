package com.zappsit.faceone.dtos;

/**
 * Created by bfillon on 26/11/2016.
 */

public class DtoDetect  {
    public String getRequest_id() {
        return request_id;
    }

    public int getTime_used() {
        return time_used;
    }

    public String getImage_id() {
        return image_id;
    }

    public Face[] getFaces() {
        return faces;
    }

    private String request_id;
    private int time_used;
    private String image_id;
    private Face[] faces;
}
