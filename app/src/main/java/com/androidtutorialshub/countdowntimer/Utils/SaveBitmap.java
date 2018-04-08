package com.androidtutorialshub.countdowntimer.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import android.net.Uri;

public class SaveBitmap {

    private Context myContext;
    private String myFilename;

    public String DEBUG_TAG = "!!SB";

    public SaveBitmap() {
    }

    public SaveBitmap(Context context, String filename) {
        myContext = context;
        myFilename = filename;
    }

    //private Uri mImageUri;

    public void createBitmapFromImage() {

        //Log.d(DEBUG_TAG, "filename is " + filename);

    }
}
