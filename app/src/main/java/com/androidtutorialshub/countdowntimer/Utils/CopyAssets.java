package com.androidtutorialshub.countdowntimer.Utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CopyAssets {

    public String DEBUG_TAG = "!!COPYASSETS";
    Context context;

    public CopyAssets(Context context) {
        this.context = context;
    }

    public void CopyAssets(Context context) {
        String appBasePath = Environment.getExternalStorageDirectory().toString() + "/cfm4407"; // Hopefully no one else is using this

        //Log.d(DEBUG_TAG, "sdCardPath is " + sdCardPath);
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("sample_images");
        } catch (IOException e) {
            Log.d(DEBUG_TAG, "Failed to get asset file list.", e);
        }

        for (String filename : files) {
            InputStream in;
            OutputStream out;
            try {
                in = assetManager.open("sample_images/" + filename);
                //Log.d(DEBUG_TAG, "filename is " + filename);
                File outFile = new File(appBasePath + "/images/", filename);
                //Log.d(DEBUG_TAG, "outFile is " + outFile);

                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                //in = null;
                out.flush();
                out.close();
                //out = null;
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

/*
    private String getFileList() {
        String sdCardPath = Environment.getExternalStorageDirectory().toString() + "/cfm_images/";
        String fileList = "";
        File dir = new File(sdCardPath);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                fileList = fileList.concat(child.toString()).concat("\n");
            }
        } else {
            // Handle the case where dir is not really a directory.
            // Checking dir.isDirectory() above would not be sufficient
            // to avoid race conditions with another process that deletes
            // directories.
        }
        return fileList;
    }
*/
}
