package com.example.lenovo.mycollage.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.lenovo.mycollage.Fragments.ChoosePicFragment;
import com.example.lenovo.mycollage.R;
import com.facebook.appevents.AppEventsLogger;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyCollage: MainAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ChoosePicFragment fragment = new ChoosePicFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment,"A").commit();
    }


}
