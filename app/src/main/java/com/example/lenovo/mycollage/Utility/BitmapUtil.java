package com.example.lenovo.mycollage.Utility;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lenovo on 10/23/2015.
 */
public class BitmapUtil {

    private static final String TAG = "MyCollage: ImData ";

    //Image Data CONSTANTS
    public static final int numOfImages = 3;
    private static final int MAX_BITMAP_HEIGHT = 300;
    public static final int UPLOADED_IMAGE_MIN_HEIGHT = MAX_BITMAP_HEIGHT;

    /* caution: static reference may create memory leaks
     release all references after their use;
        RUNTIME DATA
    */
    public static int imageAddedSoFar =0;
    public static Bitmap[] bitmaps = new Bitmap[numOfImages];
    public static boolean isImageSizeValid = false;


    // utility functions
    public static void printBitmap(Bitmap[] bitmaps){
        for(int i=0;i<bitmaps.length; i++){
            if(bitmaps[i]==null)
            {Log.d(TAG, "null");}
            else
            Log.d(TAG, bitmaps[i].toString()+" res:"+ bitmaps[i].getWidth()+" * "+bitmaps[i].getHeight());
        }
    }

    public static void releaseBitmapReferences(Bitmap[] bitmaps){
        for(int i=0;i<bitmaps.length; i++){
            bitmaps[i]=null;
        }
    }

    public static void T(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static Bitmap bitmapFromUri(Context context, Uri photoUri)
            throws FileNotFoundException, IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, dbo);
        inputStream.close();

        int rotatedWidth, rotatedHeight;
        int orientation = 0;

        if (photoUri.toString().contains("content:/")){
            orientation = getOrientation(context, photoUri);
            Log.d(TAG, "Orientation: " + orientation);
        } else {
            int orientationFormExif = getOrientationFromExif(photoUri, context);
            orientation = decodeExifOrientation(orientationFormExif);
            Log.d(TAG, "Orientation form Exif: " + orientation);
        }

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
            Log.d(TAG, "set width height"+ rotatedWidth+"*"+rotatedHeight);
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        if(rotatedHeight< UPLOADED_IMAGE_MIN_HEIGHT || rotatedWidth < UPLOADED_IMAGE_MIN_HEIGHT){
            isImageSizeValid = false;
            return null;
        }
        else {
            isImageSizeValid = true;
            Bitmap srcBitmap = readScaledBitmapFromUri(photoUri, context,
                    rotatedWidth, rotatedHeight, orientation);
            srcBitmap = setProperOrientation(orientation, srcBitmap);
            return srcBitmap;
        }
    }

     /*
     * if the orientation is not 0 (or -1, which means we don't know), we have
     * to do a rotation.
     */

    private static Bitmap setProperOrientation(int orientation, Bitmap srcBitmap) {
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0,
                    srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
        }
        return srcBitmap;
    }

    private static Bitmap readScaledBitmapFromUri(Uri photoUri, Context context, int width, int height, int orientation)
            throws FileNotFoundException, IOException {
        Log.d(TAG, "Read Scaled Bitmap: " + width + " " + height);
        InputStream is;
        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if(orientation == 0 || orientation == 180) {
            if (height > MAX_BITMAP_HEIGHT) {
                float ratio = calculateScaleRatio(height);
                srcBitmap = readRoughScaledBitmap(is, ratio);
                ratio = calculateScaleRatio(srcBitmap.getHeight());
                srcBitmap = scaleBitmap(srcBitmap, ratio);
            } else {
                Log.d(TAG, "NOT Scaled Bitmap ");
                srcBitmap = BitmapFactory.decodeStream(is);
            }
        }
        else{
            if (width > MAX_BITMAP_HEIGHT) {
                float ratio = calculateScaleRatio(width);
                srcBitmap = readRoughScaledBitmap(is, ratio);
                ratio = calculateScaleRatio(srcBitmap.getWidth());
                srcBitmap = scaleBitmap(srcBitmap, ratio);
            } else {
                srcBitmap = BitmapFactory.decodeStream(is);
            }
        }
        is.close();
        return srcBitmap;
    }

    private static float calculateScaleRatio(int height) {
        float hRatio = ((float) height) / ((float) MAX_BITMAP_HEIGHT);
        return hRatio;
    }

    private static Bitmap readRoughScaledBitmap(InputStream is, float maxRatio) {
        Bitmap result;
        // Create the bitmap from file
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = (int) maxRatio;
        result = BitmapFactory.decodeStream(is, null, options);
        if (result != null) {
            Log.i(TAG, "Read Scaled Bitmap Result wtf: "
                    + result.getWidth() + " " + result.getHeight());
            Log.i(TAG, "MaxRatio wtf: " + maxRatio);
        }
        return result;
    }

    private static Bitmap scaleBitmap(Bitmap bitmap, float ratio) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(1f / ratio, 1f / ratio);

        Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return result;
    }

    private static int getOrientation(Context context, Uri photoUri) {
		/* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION },
                null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    private static int getOrientationFromExif(Uri imageUri, Context context) {
        int orientation = -1;
        Log.d(TAG, "imageUri = " + imageUri);
        File imageFile = new File(imageUri.getPath());
        try {
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orientation;
    }

    private static int decodeExifOrientation(int orientation) {
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                orientation = 0;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                orientation = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                orientation = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                orientation = 270;
                break;
            default:
                break;
        }
        return orientation;
    }

    public static Bitmap getMergedBitmap() {

        Bitmap mergedBitmap;
        int width=0;
        int height= bitmaps[0].getHeight();

        for(int i=0;i<numOfImages;i++)
        {
            width += bitmaps[i].getWidth();
        }
        mergedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mergedBitmap);
        canvas.drawBitmap(bitmaps[0], 0f, 0f, null);
        canvas.drawBitmap(bitmaps[1], bitmaps[0].getWidth(), 0f, null);
        canvas.drawBitmap(bitmaps[2], bitmaps[0].getWidth() + bitmaps[1].getWidth(), 0f, null);
        return mergedBitmap;
    }
}
