package com.logixity.apps.bestwallpapersandringtones;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

/**
 * Created by ahmed on 20/07/2017.
 */

public class ImageAdapter extends PagerAdapter {
    Context context;
    private ArrayList<Object> list;

    ImageAdapter(Context context, ArrayList<Object> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {

        return list.size();

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        GestureImageView imageView = new GestureImageView(context);
        ImageView imageView = new ImageView(context);
        if(list.get(position) instanceof Integer){
            Integer resourceId = (Integer) list.get(position);
            Glide.with(context).fromResource()
                    .load(resourceId).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(list.get(position)).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .into(imageView);
        }

//        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//        imageView.setImageResource(list.get(position));
//        imageView.getController().getSettings().setMaxZoom(4.0f);
//        imageView.getController().enableScrollInViewPager((ViewPager) container);


//        imageView.getController().getSettings().setZoomEnabled(true);
        ((ViewPager) container).addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }
}
