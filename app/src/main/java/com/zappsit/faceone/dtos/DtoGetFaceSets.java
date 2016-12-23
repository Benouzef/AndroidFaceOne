package com.zappsit.faceone.dtos;

/**
 * Created by bfillon on 26/11/2016.
 */

public class DtoGetFaceSets {
    private String request_id;
    private int time_used;

    public FaceSet[] getFacesets() {
        return facesets;
    }

    public int getTime_used() {
        return time_used;
    }

    public String getRequest_id() {
        return request_id;
    }

    private FaceSet[] facesets;

}
