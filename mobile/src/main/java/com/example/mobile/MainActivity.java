package com.example.mobile;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }



    public void onClickHandler(View v){


        Intent intent = new Intent(this, howtouse.class);
        String transitionName = "transfer";
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,
                        v,
                        transitionName
                );

        ActivityOptionsCompat options2 =
                ActivityOptionsCompat.makeScaleUpAnimation
                        (v,v.getScrollX(),v.getScrollY(),v.getWidth(),v.getHeight());
        ActivityCompat.startActivity(MainActivity.this, intent, options.toBundle());

    }






}
