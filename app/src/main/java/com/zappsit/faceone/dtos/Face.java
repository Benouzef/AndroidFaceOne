package com.zappsit.faceone.dtos;

/**
 * Created by bfillon on 26/11/2016.
 */

public class Face {
    public String getFace_token() {
        return face_token;
    }

    public FaceRectangle getFace_rectangle() {
        return face_rectangle;
    }

    private String face_token;
    private FaceRectangle face_rectangle;
}
