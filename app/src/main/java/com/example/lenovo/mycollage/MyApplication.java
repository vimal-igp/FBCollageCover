package com.example.lenovo.mycollage;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.lenovo.mycollage.Utility.BitmapUtil;
import com.facebook.FacebookSdk;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by lenovo on 10/23/2015.
 */
public class MyApplication extends Application {
    private Context context;
    private static final String TAG = "MyCollage:App";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //printHashKey();
        FacebookSdk.sdkInitialize(context);
    }

    // In low memory scenario- we release the bitmaps
    public void onLowMemory() {
            super.onLowMemory();
            BitmapUtil.releaseBitmapReferences(BitmapUtil.bitmaps);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        BitmapUtil.releaseBitmapReferences(BitmapUtil.bitmaps);

    }

    private void printHashKey(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.lenovo.mycollage",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Toast.makeText(context, "KeyHash: "+ Base64.encodeToString(md.digest(), Base64.DEFAULT), Toast.LENGTH_SHORT).show();
                Log.d(TAG,"KeyHash: "+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }
}

