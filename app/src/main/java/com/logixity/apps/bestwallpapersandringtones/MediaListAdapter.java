package com.logixity.apps.bestwallpapersandringtones;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
//
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.InterstitialAd;
//import com.google.android.gms.ads.NativeExpressAdView;

import java.util.ArrayList;
import java.util.Random;

import static com.logixity.apps.bestwallpapersandringtones.RingtonesFragment.soundId;
//import static com.logixity.apps.iphonexringtones.TabActivity.colors;
import static com.logixity.apps.bestwallpapersandringtones.MainActivity.player;

/**
 * Created by Muhammad on 21/03/2017.
 */
public class MediaListAdapter extends ArrayAdapter {

    private Context context;
    private int layoutResourceId;
    private ArrayList<String> data = new ArrayList<String>();
    static IFrag frag;
    ArrayList<Integer> ringToneIDs;

    public MediaListAdapter(Context context, int layoutResourceId, ArrayList<String> data, IFrag fragment, ArrayList<Integer> ringToneIDs) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.ringToneIDs = ringToneIDs;
        this.context = context;
        this.data = data;
        frag = fragment;
        frag.setCurrentView(null);
        frag.setSelectedImage(-1);
    }


    static void stopPlayback() {
        try {
            if (player != null) {
                if (player.isPlaying()) {


                    player.stop();
                    player.reset();
                }

            }

            if (frag.getCurrentView() != null) {
                frag.getCurrentView().setImageResource(R.drawable.play_material);
            }
            frag.setSelectedImage(-1);
            soundId = -1;
        } catch (Exception ex) {
            soundId = -1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;
        Random r = new Random(System.currentTimeMillis());
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.ringToneNameTxt = (TextView) row.findViewById(R.id.ringtone_name);
            holder.image = (ImageView) row.findViewById(R.id.play_btn);
            holder.setRingView = (ImageView) row.findViewById(R.id.set_as_btn);
            holder.setAlarmView = (ImageView) row.findViewById(R.id.set_alarm_btn);
            holder.setMsgView = (ImageView) row.findViewById(R.id.set_sms_btn);
            holder.heartsNum = row.findViewById(R.id.heartTextNum);
            holder.heartsNum.setText("("+r.nextInt(2000)+")");
//            holder.adView = (NativeExpressAdView)row.findViewById(R.id.nativeAdView);

            row.setTag(holder);

//                AdRequest request = null;

//                if (App.instance.testingMode) {
//                    request= new AdRequest.Builder().addTestDevice("55757F6B6D6116FAC42122EC92E5A58C").build();
//                } else {
//                    request= new AdRequest.Builder().build();
//                }
//                holder.adView.loadAd(request);

        } else {
            holder = (ViewHolder) row.getTag();
        }


        holder.colorId= MainActivity.colors.get(r.nextInt(6));
        holder.colorize(context.getResources().getColor(holder.colorId));
//        holder.imageTitle.setText(data.get(position));

        if (frag.getSelectedImage() == position) {
            holder.image.setImageResource(R.drawable.pause_material);
            holder.image.setColorFilter(context.getResources().getColor(holder.colorId));
        } else {
            holder.image.setImageResource(R.drawable.play_material);
            holder.image.setColorFilter(context.getResources().getColor(holder.colorId));
        }
        holder.ringToneNameTxt.setText(data.get(position));
        final int pos = position;
        final ViewHolder h = holder;
        final int color = holder.colorId;
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                App.instance.countIntAd++;
                if (soundId == ringToneIDs.get(pos)) {
                    player.stop();
                    player.release();
                    soundId = -1;
                    player = null;
                    ((ImageView) v).setImageResource(R.drawable.play_material);
                    frag.setSelectedImage(-1);
//                    h.image.setImageResource(R.drawable.play_material);
                    frag.setCurrentView(null);
                    return;
                }
//                try {
//
//                    if (App.instance.shouldShowAd()) {
//                        InterstitialAd fullScreenAd = App.instance.getFullScreenAd();
//
//                        if (fullScreenAd.isLoaded()) {
//                            fullScreenAd.show();
//                        } else {
//                            Log.d("MADDY", "Interstitial Not Loaded");
//                            App.instance.requestNewInterstitial();
//
//                        }
//
//                    } else {
//                        Log.d("MADDY", "Should Not Show Ad");
//                    }
//                } catch (Exception ex) {}
                stopPlayback();
                ((ImageView) v).setImageResource(R.drawable.play_material);
                String resPackageName = "android.resource://" + getContext().getPackageName() + "/";
                player = MediaPlayer.create(getContext(), Uri.parse(resPackageName + ringToneIDs.get(pos)));
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopPlayback();
                    }
                });
                soundId = ringToneIDs.get(pos);
                player.start();
                Log.d("MADDY", "SETTING IMAGE");
                frag.setCurrentView(h.image);
                frag.setSelectedImage(pos);
                frag.getCurrentView().setImageResource(R.drawable.pause_material);
            }
        });
        holder.setRingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUtils.build()
                        .setContext(context)
                        .setRingTone(data.get(pos), ringToneIDs.get(pos), SystemUtils.SET_AS_RINGTONE);
            }
        });
        holder.setAlarmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUtils.build()
                        .setContext(context)
                        .setRingTone(data.get(pos), ringToneIDs.get(pos), SystemUtils.SET_AS_ALARM);
            }
        });
        holder.setMsgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUtils.build()
                        .setContext(context)
                        .setRingTone(data.get(pos), ringToneIDs.get(pos), SystemUtils.SET_AS_MSG);

            }
        });
        //holder.image.setImageBitmap(item.getImage());
        return row;
    }

    static class ViewHolder {
        TextView ringToneNameTxt;
        ImageView image;
        ImageView setRingView;
        ImageView setAlarmView;
        ImageView setMsgView;
        TextView heartsNum;
//        NativeExpressAdView adView;
        int colorId;
        void colorize(int color){
            image.setColorFilter(color);
            setAlarmView.setColorFilter(color);
            setMsgView.setColorFilter(color);
            setRingView.setColorFilter(color);
        }
    }
}
