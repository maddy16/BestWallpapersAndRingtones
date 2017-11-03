package com.logixity.apps.bestwallpapersandringtones;

import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by ahmed on 04/06/2017.
 */

public interface IFrag {
    public ImageView getCurrentView();
    public void setCurrentView(ImageView view);
    void setSelectedImage(int i);
    int getSelectedImage();
    ArrayList<ImageView> getAllViews();
}
