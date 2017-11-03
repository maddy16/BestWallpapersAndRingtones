package com.logixity.apps.bestwallpapersandringtones;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import static com.logixity.apps.bestwallpapersandringtones.MainActivity.player;
import static com.logixity.apps.bestwallpapersandringtones.RingtonesFragment.selectedFile;
import static com.logixity.apps.bestwallpapersandringtones.RingtonesFragment.soundId;
import static com.logixity.apps.bestwallpapersandringtones.SystemUtils.SET_AS_ALARM;
import static com.logixity.apps.bestwallpapersandringtones.SystemUtils.SET_AS_MSG;
import static com.logixity.apps.bestwallpapersandringtones.SystemUtils.SET_AS_RINGTONE;

/**
 * Created by ahmed on 16/10/2017.
 */

public class MediaRecyclerAdapter extends RecyclerView.Adapter<MediaRecyclerAdapter.ViewHolder> {

    static IFrag frag;
    ArrayList<Object> ringToneIDs;
    String tonePrefix;
    private Context context;
    private ArrayList<String> data = new ArrayList<String>();

    private MediaRecyclerAdapter(Context context, IFrag fragment) {
        this.context = context;
        frag = fragment;
        frag.setCurrentView(null);
        frag.setSelectedImage(-1);
    }

    MediaRecyclerAdapter(Context context, ArrayList<String> data, IFrag fragment, ArrayList<Object> ringToneIDs, String tonePrefix) {
        this(context, fragment);
        this.ringToneIDs = ringToneIDs;
        this.data = data;
        this.tonePrefix = tonePrefix;

    }

    static void stopPlayback() {
        try {
            if (player != null) {
                if (player.isPlaying()) {


                    player.stop();
                    player.reset();
                }

            }
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (ImageView view : frag.getAllViews()) {
                        if (frag.getCurrentView() != null && view != frag.getCurrentView())
                            view.setImageResource(R.drawable.play_material);
                    }
                }
            }, 10);

            if (frag.getCurrentView() != null) {
                frag.getCurrentView().setImageResource(R.drawable.play_material);
            }
            frag.setSelectedImage(-1);
            soundId = -1;
            selectedFile = null;
        } catch (Exception ex) {
            soundId = -1;
            selectedFile = null;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_list_single_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(position!=0 && position%2==0) {
            AdRequest adRequest = new AdRequest.Builder().build();
            holder.adView.loadAd(adRequest);
        } else {
            holder.adView.setVisibility(View.GONE);
        }
        Random r = new Random(System.currentTimeMillis());
        holder.colorId = MainActivity.colors.get(r.nextInt(6));
        holder.colorize(context.getResources().getColor(holder.colorId));
        if (frag.getSelectedImage() == position) {
            holder.image.setImageResource(R.drawable.pause_material);
            holder.image.setColorFilter(context.getResources().getColor(holder.colorId));
        } else {
            holder.image.setImageResource(R.drawable.play_material);
            holder.image.setColorFilter(context.getResources().getColor(holder.colorId));
        }

        final MediaRecyclerAdapter.ViewHolder h = holder;
        final int color = holder.colorId;
//        holder.imageTitle.setText(data.get(position));

        if (ringToneIDs != null) {
            if(ringToneIDs.get(position) instanceof Integer)
                holder.ringToneNameTxt.setText(data.get(position));
            else
                holder.ringToneNameTxt.setText(tonePrefix+" "+(position+1));
            holder.completeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.setting_dialog);
                    ImageView ringImage = (ImageView) dialog.findViewById(R.id.dialog_set_ring);
                    ImageView notiImage = (ImageView) dialog.findViewById(R.id.dialog_set_noti);
                    ImageView alarmImage = (ImageView) dialog.findViewById(R.id.dialog_set_alarm);
                    ringImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ringToneIDs.get(position) instanceof Integer) {
                                Integer resourceId = (Integer) ringToneIDs.get(position);

                                SystemUtils.instance.setContext(context).setRingTone(data.get(position), resourceId, SET_AS_RINGTONE);
                            } else {
                                SystemUtils.instance.setContext(context).setRingTone(tonePrefix+" "+(position+1), ringToneIDs.get(position), SET_AS_RINGTONE);
                            }

                            dialog.hide();
                        }
                    });
                    notiImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ringToneIDs.get(position) instanceof Integer) {
                                Integer resourceId = (Integer) ringToneIDs.get(position);

                                SystemUtils.instance.setContext(context).setRingTone(data.get(position), resourceId, SET_AS_MSG);
                            } else {
                                SystemUtils.instance.setContext(context).setRingTone(tonePrefix+" "+(position+1), ringToneIDs.get(position), SET_AS_MSG);
                            }
                            dialog.hide();
                        }
                    });
                    alarmImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ringToneIDs.get(position) instanceof Integer) {
                                Integer resourceId = (Integer) ringToneIDs.get(position);

                                SystemUtils.instance.setContext(context).setRingTone(data.get(position), resourceId, SET_AS_ALARM);
                            } else {
                                SystemUtils.instance.setContext(context).setRingTone(tonePrefix+" "+(position+1), ringToneIDs.get(position), SET_AS_ALARM);
                            }
                            dialog.hide();
                        }
                    });
                    dialog.show();
                }
            });
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                App.instance.countIntAd++;
                    InterstitialAd fullScreenAd = App.instance.getFullScreenAd();
                    if(App.instance.shouldShowAd()) {
                        if(fullScreenAd!=null && fullScreenAd.isLoaded()) {
                            fullScreenAd.show();
                        } else {
                            App.instance.requestNewInterstitial();
                        }
                    }
                    if(selectedFile == null && !(ringToneIDs.get(position) instanceof File)) {
                        if (soundId == (Integer) ringToneIDs.get(position)) {
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
                    } else if(selectedFile !=null && ringToneIDs.get(position) instanceof  File ) {
                        if (selectedFile == (File) ringToneIDs.get(position)) {
                            player.stop();
                            player.release();
                            selectedFile = null;
                            player = null;
                            ((ImageView) v).setImageResource(R.drawable.play_material);
                            frag.setSelectedImage(-1);
//                    h.image.setImageResource(R.drawable.play_material);
                            frag.setCurrentView(null);
                            return;
                        }
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
                    String resPackageName = "android.resource://" + context.getPackageName() + "/";
                    if(ringToneIDs.get(position) instanceof Integer)
                        player = MediaPlayer.create(context, Uri.parse(resPackageName + ringToneIDs.get(position)));
                    else
                        player = MediaPlayer.create(context, Uri.fromFile((File) ringToneIDs.get(position)));
                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            stopPlayback();
                        }
                    });
                    if (ringToneIDs.get(position) instanceof Integer)
                        soundId = (Integer) ringToneIDs.get(position);
                    else
                        selectedFile = (File) ringToneIDs.get(position);
                    player.start();
                    Log.d("MADDY", "SETTING IMAGE");
                    frag.setCurrentView(h.image);
                    frag.setSelectedImage(position);
                    frag.getCurrentView().setImageResource(R.drawable.pause_material);
                }
            });
            holder.setRingView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ringToneIDs.get(position) instanceof Integer) {
                        Integer resourceId = (Integer) ringToneIDs.get(position);

                        SystemUtils.instance.setContext(context).setRingTone(data.get(position), resourceId, SET_AS_RINGTONE);
                    } else {
                        SystemUtils.instance.setContext(context).setRingTone(tonePrefix+" "+(position+1), ringToneIDs.get(position), SET_AS_RINGTONE);
                    }

                }
            });
            holder.setAlarmView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ringToneIDs.get(position) instanceof Integer) {
                        Integer resourceId = (Integer) ringToneIDs.get(position);

                        SystemUtils.instance.setContext(context).setRingTone(data.get(position), resourceId, SET_AS_ALARM);
                    } else {
                        SystemUtils.instance.setContext(context).setRingTone(tonePrefix+" "+(position+1), ringToneIDs.get(position), SET_AS_ALARM);
                    }
                }
            });
            holder.setMsgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ringToneIDs.get(position) instanceof Integer) {
                        Integer resourceId = (Integer) ringToneIDs.get(position);

                        SystemUtils.instance.setContext(context).setRingTone(data.get(position), resourceId, SET_AS_MSG);
                    } else {
                        SystemUtils.instance.setContext(context).setRingTone(tonePrefix+" "+(position+1), ringToneIDs.get(position), SET_AS_MSG);
                    }

                }
            });
        }
        //holder.image.setImageBitmap(item.getImage());
    }

    @Override
    public int getItemCount() {

        return ringToneIDs.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView ringToneNameTxt;
        ImageView image;
        ImageView setRingView;
        ImageView setAlarmView;
        ImageView setMsgView;
        TextView heartsNum;
        View completeView;
        int colorId;
        NativeExpressAdView adView;

        public ViewHolder(View view) {
            super(view);
            Random r = new Random(System.currentTimeMillis());
            ringToneNameTxt = (TextView) view.findViewById(R.id.ringtone_name);
            image = (ImageView) view.findViewById(R.id.play_btn);
            setRingView = (ImageView) view.findViewById(R.id.set_as_btn);
            setAlarmView = (ImageView) view.findViewById(R.id.set_alarm_btn);
            setMsgView = (ImageView) view.findViewById(R.id.set_sms_btn);
            adView = view.findViewById(R.id.ringListNative);
            heartsNum = view.findViewById(R.id.heartTextNum);
            heartsNum.setText("(" + r.nextInt(2000) + ")");
            completeView = view;
            frag.getAllViews().add(image);

//            img = (ImageView) view.findViewById(R.id.list_item_img);
        }

        void colorize(int color) {
            image.setColorFilter(color);
            setAlarmView.setColorFilter(color);
            setMsgView.setColorFilter(color);
            setRingView.setColorFilter(color);
        }
    }
}
