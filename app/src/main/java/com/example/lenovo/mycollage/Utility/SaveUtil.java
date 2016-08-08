package com.example.lenovo.mycollage.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * Created by lenovo on 10/25/2015.
 */
public class SaveUtil {

    private static final String TAG = "MyCollage: SaveUtil";
    public static String path;


    public static String save(Bitmap bitmap){
        path = createPathWithImageName();
        Log.d(TAG, "Saved file Path: " + path);
        try {
            FileOutputStream out = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    public static String createPathWithImageName(){
        String lvl1 = "Pictures";
        String lvl2 = "MyCollage";

        File folderLvl1 = checkPath(Environment.getExternalStorageDirectory(), lvl1);
        File folderLvl2 = checkPath(folderLvl1, lvl2);

       return folderLvl2.getPath()+"/"+ "IMG_"+ DateFormat.format("yyyyMMdd_kkmmss", new Date())+".jpg";
    }

    private static File checkPath(File pathLvl1, String lvl2) {
        File pathLvl2 = new File(pathLvl1, lvl2);
        if (!pathLvl2.exists()) {
            pathLvl2.mkdir();
        }
        return pathLvl2;
    }

    public static File getTempFile(Context context) {
        String fileName = "temp_photo.jpg";
        File path = new File(Environment.getExternalStorageDirectory(), context.getPackageName());
        if (!path.exists()) {
            path.mkdir();
        }
        return new File(path, fileName);
    }
}
