package com.example.lenovo.mycollage.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lenovo.mycollage.R;
import com.example.lenovo.mycollage.Utility.SaveUtil;
import com.example.lenovo.mycollage.Utility.BitmapUtil;
import com.facebook.CallbackManager;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.File;

/**
 * Created by lenovo on 10/23/2015.
 */
public class FinalCollageActivity extends AppCompatActivity implements View.OnClickListener{

    private Context context;
    private ImageView pic;
    private Bitmap collageBitmap;
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;
    private static final String TAG = "MyCollage: FinColAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_collage);

        initializeViewAndStuffs();
        initializeFacebookStuff();
    }

    private void initializeViewAndStuffs(){
        context = this;
        pic = (ImageView)findViewById(R.id.collage_pic);
        collageBitmap = BitmapUtil.getMergedBitmap();
        pic.setImageBitmap(collageBitmap);

        findViewById(R.id.save_image_button).setOnClickListener(this);
        findViewById(R.id.share_image_button).setOnClickListener(this);
    }

    private void initializeFacebookStuff(){
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                //BitmapUtil.T(context,"Success");
            }

            @Override
            public void onCancel() {
                BitmapUtil.T(context,"Cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                BitmapUtil.T(context,"Error");
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.save_image_button:
                saveImage();
                break;
            case R.id.share_image_button:
                shareImage();
                break;
            default:
                break;
        }
    }

    private void saveImage(){
        SaveUtil.save(collageBitmap);
        BitmapUtil.T(context, getString(R.string.image_saved_msg));
    }

    private void shareImage(){
        SaveUtil.save(collageBitmap);
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("image/png");
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(SaveUtil.path)));
        Log.d(TAG, "image created:" + SaveUtil.createPathWithImageName());
        startActivityForResult(sendIntent, 0);
    }

    private void shareImageViaFacebook(){
        SaveUtil.save(collageBitmap);
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(collageBitmap)
                    .build();
            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();
            shareDialog.show(content);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
}
