package com.logixity.apps.bestwallpapersandringtones;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ahmed on 21/06/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
//    boolean isOnline;
    private ArrayList<Object> galleryList;
//    private ArrayList<File> onlineFiles;
//    private ArrayList<Object> objectList;
    private Context context;

    MyAdapter(Context context, ArrayList<Object> galleryList) {
        this.galleryList = galleryList;
        this.context = context;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder viewHolder, int i) {
//        if (objectList!=null) {
//            if(galleryList.get(i) instanceof Integer) {
//                Integer listItem = (Integer) galleryList.get(i);
//                Glide.with(context).fromResource()
//                        .load(listItem).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                        .into(viewHolder.img);
//            } else if(galleryList.get(i) instanceof File) {
//                File listItem = (File) galleryList.get(i);
//                Glide.with(context)
//                        .load(listItem).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                        .into(viewHolder.img);
//            }
//
//
////            viewHolder.img.setOnClickListener(new MainActivity.ImageZoomer(galleryList.get(i), i));
//        }
//        if (!isOnline) {
//            Glide.with(context).fromResource()
//                    .load(galleryList.get(i)).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                    .into(viewHolder.img);
//
//            viewHolder.img.setOnClickListener(new MainActivity.ImageZoomer(galleryList.get(i), i));
//        } else {
//            Glide.with(context)
//                    .load(onlineFiles.get(i)).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                    .into(viewHolder.img);
//            viewHolder.img.setOnClickListener(new MainActivity.ImageZoomer(onlineFiles.get(i),i));
//        }
        if(galleryList.get(i) instanceof Integer) {
            Integer listItem = (Integer) galleryList.get(i);
            Glide.with(context).fromResource()
                    .load(listItem).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .into(viewHolder.img);
        } else if(galleryList.get(i) instanceof File) {
            File listItem = (File) galleryList.get(i);
            Glide.with(context)
                    .load(listItem).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .into(viewHolder.img);
        }
        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewHolder.img.setOnClickListener(new MainActivity.ImageZoomer(galleryList.get(i), i));

//        viewHolder.img.setImageResource((galleryList.get(i)));
    }

    @Override
    public int getItemCount() {
        return  galleryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img;

        public ViewHolder(View view) {
            super(view);
            img = (ImageView) view.findViewById(R.id.list_item_img);
        }
    }
}