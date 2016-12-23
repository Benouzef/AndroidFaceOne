package com.zappsit.faceone.dtos;

/**
 * Created by bfillon on 26/11/2016.
 */

public class FaceSet {
    public String getFaceset_token() {
        return faceset_token;
    }

    public String getOuter_id() {
        return outer_id;
    }

    public String getDisplayname() {
        return displayname;
    }

    public String getTags() {
        return tags;
    }

    private String faceset_token;
    private String outer_id;
    private String displayname;
    private String tags;
}
