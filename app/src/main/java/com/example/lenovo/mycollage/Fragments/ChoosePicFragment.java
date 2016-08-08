package com.example.lenovo.mycollage.Fragments;


import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.lenovo.mycollage.Activities.CollectedPicsActivity;
import com.example.lenovo.mycollage.Utility.BitmapUtil;
import com.example.lenovo.mycollage.R;
import com.example.lenovo.mycollage.Utility.SaveUtil;
import com.facebook.appevents.AppEventsLogger;


/**
 * Created by lenovo on 10/23/2015.
 */
public class ChoosePicFragment extends Fragment implements View.OnClickListener {

    private static final String TAG ="MyCollage:ChoosePicFrag";
    private ImageButton gallaryButton, cameraButton;
    private Context context;

    // REQUEST CODES
    private final static int REQUEST_PICK_IMAGE = 0;
    private final static int REQUEST_TAKE_PHOTO = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.choose_pic_frag, container, false);

        gallaryButton = (ImageButton) view.findViewById(R.id.gallery_button);
        cameraButton = (ImageButton) view.findViewById(R.id.camera_button);
        gallaryButton.setOnClickListener(this);
        cameraButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gallery_button:
                gallaryButtonClicked();
                break;

            case R.id.camera_button:
                cameraButtonClicked();
                break;
            default:
                break;
        }
    }

    private void gallaryButtonClicked() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    private void cameraButtonClicked() {
        Uri uri = Uri.fromFile(SaveUtil.getTempFile(context.getApplicationContext()));
        Intent intent = createIntentForCamera(uri);
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICK_IMAGE:
                    processRecievedImage(data.toUri(0));
                    break;

                case REQUEST_TAKE_PHOTO:
                    Uri uri = Uri.fromFile(SaveUtil.getTempFile(context.getApplicationContext()));
                    processRecievedImage(uri.toString());
                    break;
                default:
                    break;
            }
        }
    }

    private void processRecievedImage(String uriString){
        if (BitmapUtil.imageAddedSoFar == 0) {
            Intent intent = new Intent(context, CollectedPicsActivity.class);
            intent.putExtra(getString(R.string.image_1_uri_flag), uriString);
            startActivity(intent);
        } else {
            ((CollectedPicsActivity) getActivity()).setCurrentURI(uriString);
            ((CollectedPicsActivity) getActivity()).loadImage(
                    getActivity(),
                    ((CollectedPicsActivity) getActivity()).getViews(),
                    Uri.parse(uriString),
                    BitmapUtil.imageAddedSoFar);
        }
    }

    private Intent createIntentForCamera(Uri imageUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        return intent;
    }
}