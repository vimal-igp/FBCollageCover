package com.example.lenovo.mycollage.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.lenovo.mycollage.Fragments.ChoosePicFragment;
import com.example.lenovo.mycollage.Utility.BitmapUtil;
import com.example.lenovo.mycollage.R;

/**
 * Created by lenovo on 10/23/2015.
 */
public class CollectedPicsActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MyCollage: CollectPic";
    private Context context;
    private ImageView[] views = new ImageView[BitmapUtil.numOfImages];
    private String currentURI;
    private Button createCollage;
    private TextView msg;

    ChoosePicFragment fragment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collected_pics);
        fragment = new ChoosePicFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container2,fragment,"B").commit();
        context =this;
        createCollage =(Button)findViewById(R.id.create_collage_button);
        createCollage.setOnClickListener(this);

        String imageUri1 = getIntent().getExtras().getString(getString(R.string.image_1_uri_flag));
        msg = (TextView)findViewById(R.id.collected_pics_msg);

        views[0] = (ImageView)findViewById(R.id.pic1);
        views[1] = (ImageView)findViewById(R.id.pic2);
        views[2] = (ImageView)findViewById(R.id.pic3);
        loadImage(context, views, Uri.parse(imageUri1), BitmapUtil.imageAddedSoFar);
    }


    public void setCurrentURI(String uri){currentURI=uri;}

    public ImageView[] getViews(){return views;}

    public void loadImage(Context context, ImageView[] views, Uri uri, int index) {
        try {
                Bitmap tmp = BitmapUtil.bitmapFromUri(context, uri);
                if(BitmapUtil.isImageSizeValid) {
                    BitmapUtil.bitmaps[index] = tmp;
                    views[index].setImageBitmap(BitmapUtil.bitmaps[index]);
                    BitmapUtil.imageAddedSoFar++;
                    BitmapUtil.printBitmap(BitmapUtil.bitmaps);

                    if (BitmapUtil.imageAddedSoFar == 3) {
                        // hide fragment
                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                        createCollage.setEnabled(true);
                        msg.setText(getString(R.string.all_pics_added_msg));
                    }
                }
                else {
                    BitmapUtil.T(context, "Min image size: " + BitmapUtil.UPLOADED_IMAGE_MIN_HEIGHT + "*" + BitmapUtil.UPLOADED_IMAGE_MIN_HEIGHT);
                }
        } catch (Exception e) {
            Log.e("DEBUG_MYCOLLAGE", e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BitmapUtil.releaseBitmapReferences(BitmapUtil.bitmaps);
        BitmapUtil.imageAddedSoFar=0;
    }

    //releasing static bitmap references: may cause memory leaks
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BitmapUtil.releaseBitmapReferences(BitmapUtil.bitmaps);
        BitmapUtil.imageAddedSoFar=0;
        BitmapUtil.printBitmap(BitmapUtil.bitmaps);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.create_collage_button){
            startActivity(new Intent(this,FinalCollageActivity.class));
        }
    }
}
