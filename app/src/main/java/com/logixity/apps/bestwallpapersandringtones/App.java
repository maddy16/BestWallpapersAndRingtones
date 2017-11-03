package com.logixity.apps.bestwallpapersandringtones;

import android.app.Application;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.InterstitialAd;
//import com.google.android.gms.ads.MobileAds;

/**
 * Created by ahmed on 13/06/2017.
 */

public class App extends Application {
    InterstitialAd mInterstitialAd;
    InterstitialAd mVideoInterstitialAd;

    static final int MODE_RINGTONES = 1;
    static final int MODE_WALLPAPERS = 2;
    static App instance;
    boolean testingMode = false;
    WallpapersFragment wallpapersFragment;
    int countIntAd = 0;
    void loadAds(){
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1474727696191193~2859872061"); // App Id admob
        mVideoInterstitialAd = new InterstitialAd(this);
        mInterstitialAd = new InterstitialAd(this);
        if(testingMode){
            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); // Test Ad Id
            mVideoInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); // Test Ad Id
        } else{
            mInterstitialAd.setAdUnitId("ca-app-pub-1474727696191193/5333281729");
            mVideoInterstitialAd.setAdUnitId("ca-app-pub-1474727696191193/1650732172");
        }
        requestNewInterstitial();
    }
    public void requestNewInterstitial() {
        AdRequest adRequest = null;
        AdRequest adVRequest = null;
        if(testingMode){
            adRequest= new AdRequest.Builder()
                    .addTestDevice("55757F6B6D6116FAC42122EC92E5A58C")
                    .build();
            adVRequest= new AdRequest.Builder()
                    .addTestDevice("55757F6B6D6116FAC42122EC92E5A58C")
                    .build();
        } else {
            adRequest = new AdRequest.Builder()
                    .build();
            adVRequest = new AdRequest.Builder()
                    .build();
        }
        mInterstitialAd.loadAd(adRequest);
        mVideoInterstitialAd.loadAd(adVRequest);
    }
    boolean shouldShowAd(){
        boolean show=false;
        if(countIntAd==0)
            show=true;
        countIntAd++;
        if(countIntAd==2)
            countIntAd=0;
        return show;
    }
    public InterstitialAd getFullScreenAd(){
        if(mVideoInterstitialAd.isLoaded())
            return mVideoInterstitialAd;
        return mInterstitialAd;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
//        Toast.makeText(this, "Application Created", Toast.LENGTH_SHORT).show();
    }
    public void setWallpapersFragment(WallpapersFragment wallpapersFragment){
        this.wallpapersFragment = wallpapersFragment;
    }
}
