package com.logixity.apps.bestwallpapersandringtones;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {
    ImageView logo;
    ImageView compName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        logo = (ImageView) findViewById(R.id.companyIcon);
        compName = (ImageView) findViewById(R.id.companyTextIcon);
        logo.postDelayed(new Runnable() {
            @Override
            public void run() {
                zoomInLogo();
            }
        },1);
    }
    void zoomInLogo(){
        Animation bubbleAnim = AnimationUtils.loadAnimation(Splash.this,R.anim.bubble_zoom_in_anim);
        bubbleAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }
            @Override
            public void onAnimationEnd(Animation animation) { zoomOutLogo(); }
            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        logo.startAnimation(bubbleAnim);
    }
    void zoomOutLogo(){
        Animation bubbleAnim = AnimationUtils.loadAnimation(Splash.this,R.anim.bubble_zoom_out_anim);
        bubbleAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }
            @Override
            public void onAnimationEnd(Animation animation) {
                logo.setVisibility(View.VISIBLE);
                fadeInLogixity();
//                animateLogo();
//                Intent i = new Intent(Splash.this,MainActivity.class);
//                startActivity(i);
//                finish();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        logo.startAnimation(bubbleAnim);
    }
    void fadeInLogixity(){
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(900);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                compName.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        compName.startAnimation(fadeIn);
    }
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadGame();

            }
        },2500);
        super.onPostCreate(savedInstanceState);
    }
    private void loadGame(){
        ((App)getApplication()).loadAds();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                exitLogo();
            }
        },600);
    }
    private void exitLogo(){
//        Animation zoomOut = AnimationUtils.loadAnimation(Splash.this,R.anim.zoom_out);
        Animation zoomOut = new AlphaAnimation(1, 0);
//        Animation fadeOut = new AlphaAnimation(1, 0);
        zoomOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                compName.setVisibility(View.INVISIBLE);
                logo.setVisibility(View.INVISIBLE);
                Intent i = new Intent(Splash.this,MainActivity.class);
                startActivity(i);
                finish();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        zoomOut.setDuration(500);
//        fadeOut.setDuration(500);

        logo.startAnimation(zoomOut);
        compName.startAnimation(zoomOut);


    }
}
