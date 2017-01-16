package com.zappsit.faceone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by bfillon on 16/01/2017.
 */

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private ImageView imageView;
    private Bitmap returnedBitmap;
    private Bitmap image;

    public DownloadImageTask(ImageView imageView, Bitmap b) {
        this.imageView = imageView;
        this.returnedBitmap = b;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            image = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            image = null;
        }
        return image;
    }

    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            imageView.setImageBitmap(result);
            returnedBitmap = result;
        }
    }
}
