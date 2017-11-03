package com.logixity.apps.bestwallpapersandringtones;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

//import com.google.android.gms.ads.InterstitialAd;


/**
 * Created by ahmed on 02/06/2017.
 */

public class SystemUtils extends AppCompatActivity {

    static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;
    static final int SET_AS_RINGTONE = 1;
    static final int SET_AS_ALARM = 2;
    static final int SET_AS_MSG = 3;
    static SystemUtils instance = new SystemUtils();
    private Context context;

    private SystemUtils() {

    }

    public static SystemUtils build() {
        return instance;
    }

    public SystemUtils setContext(Context context) {
        instance.context = context;
        return instance;
    }

    public void checkWriteExternalStoragePermission() {
        ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d("MADDY", requestCode + "");
        switch (requestCode) {

            case SystemUtils.MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MADDY", "Permissions Granted");

                } else {
                    Log.d("MADDY", "Permission Denied");
                    Toast.makeText(context, "Application requires Permissions to set Ringtone.", Toast.LENGTH_LONG).show();
                }
                return;

            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private boolean checkSystemWritePermission() {
        boolean retVal = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(context);
            Log.d("MADDY", "Can Write Settings: " + retVal);
            if (!retVal) {
                Toast.makeText(context, "Please Allow System to Write Settings.", Toast.LENGTH_LONG).show();
                openAndroidPermissionsMenu();
            }
        }
        return retVal;
    }

    @TargetApi(23)
    private void openAndroidPermissionsMenu() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    void setRingTone(String name, Object toneId, int setOption) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions(name, toneId, setOption);
        } else {
            try {
                permissionAllowed(name, toneId, setOption);
            } catch (Exception ex) {
                Toast.makeText(context, "Operation Failed. Try Again.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @TargetApi(23)
    public void checkPermissions(String name, Object toneId, int setOption) {
        if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            checkWriteExternalStoragePermission();
        } else {
            try {
                permissionAllowed(name, toneId, setOption);
            } catch (Exception ex) {
                Toast.makeText(context, "Operation Failed. Try Again.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void permissionAllowed(String name, Object toneId, int setOption) throws Exception {
//        String name = data.get(indexOfTone);
        InterstitialAd fullScreenAd = App.instance.getFullScreenAd();
        if (fullScreenAd != null && fullScreenAd.isLoaded()) {
            fullScreenAd.show();
        } else {
            App.instance.requestNewInterstitial();
        }

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES) + "";
        File f = new File(path + "/", name + ".ogg");

//        Uri mUri = Uri.parse(context.getPackageName() + "/" + ringToneIDs.get(indexOfTone));
        Uri mUri = Uri.parse(context.getPackageName() + "/" + toneId);
        ContentResolver mCr = context.getContentResolver();


        try {
            byte[] readData = new byte[1024];
            InputStream fis = null;
            if (toneId instanceof Integer) {
                Integer resourceId = (Integer) toneId;
                fis = context.getResources().openRawResource(resourceId);
            } else {
                fis = new FileInputStream((File) toneId);
            }
            FileOutputStream fos = new FileOutputStream(f);
            int i = fis.read(readData);
            while (i != -1) {
                fos.write(readData, 0, i);
                i = fis.read(readData);
            }
            fos.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
        Toast.makeText(context, "Tone Saved in " + path, Toast.LENGTH_LONG).show();
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, f.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, name);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.MediaColumns.SIZE, f.length());
        values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(f.getAbsolutePath());
        Uri newUri = mCr.insert(uri, values);
        if (newUri == null) {
            Cursor c = mCr.query(uri, new String[]{MediaStore.Audio.Media._ID}, MediaStore.Audio.Media.DATA + "=?",
                    new String[]{f.getAbsolutePath()}, null);
            long id = -1;
            if (c != null && c.moveToFirst()) {
                id = c.getLong(c.getColumnIndex(MediaStore.Audio.Media._ID));
                newUri = Uri.parse(uri.toString() + "/" + id);
                c.close();
            }
        }

        try {
            if (checkSystemWritePermission()) {
                if (newUri == null) {
                    Toast.makeText(context, "Unable to Set Tone", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (setOption == SET_AS_RINGTONE) {
                    RingtoneManager.setActualDefaultRingtoneUri(context,
                            RingtoneManager.TYPE_RINGTONE, newUri);
                    Settings.System.putString(mCr, Settings.System.RINGTONE, newUri.toString());
                    Toast.makeText(context, "Ringtone Set Successfully.", Toast.LENGTH_SHORT).show();
                } else if (setOption == SET_AS_ALARM) {
                    RingtoneManager.setActualDefaultRingtoneUri(context,
                            RingtoneManager.TYPE_ALARM, newUri);
                    Settings.System.putString(mCr, Settings.System.ALARM_ALERT, newUri.toString());
                    Toast.makeText(context, "Alarm Tone Set Successfully.", Toast.LENGTH_SHORT).show();
                } else if (setOption == SET_AS_MSG) {
                    RingtoneManager.setActualDefaultRingtoneUri(context,
                            RingtoneManager.TYPE_NOTIFICATION, newUri);
                    Settings.System.putString(mCr, Settings.System.NOTIFICATION_SOUND, newUri.toString());
                    Toast.makeText(context, "Notification Sound Set Successfully.", Toast.LENGTH_SHORT).show();
                }
//                InterstitialAd fullScreenAd = App.instance.getFullScreenAd();
//                if(fullScreenAd.isLoaded()){
//                    fullScreenAd.show();
//                } else {
//                    Log.d("MADDY","Interstitial Not Loaded");
//                    App.instance.requestNewInterstitial();
////                    App.instance.countIntAd--;
//                }

            }

        } catch (Throwable t) {
            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            t.printStackTrace();
        }
    }

    static class ItemClickHandler implements AdapterView.OnItemClickListener {
        Context context;
        ArrayList<String> data;
        ArrayList<Integer> ringToneIds;

        public ItemClickHandler(ArrayList<Integer> ringToneIds, ArrayList<String> data, Context context) {
            this.data = data;
            this.context = context;
            this.ringToneIds = ringToneIds;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.setting_dialog);
            ImageView ringImage = (ImageView) dialog.findViewById(R.id.dialog_set_ring);
            ImageView notiImage = (ImageView) dialog.findViewById(R.id.dialog_set_noti);
            ImageView alarmImage = (ImageView) dialog.findViewById(R.id.dialog_set_alarm);
            ringImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SystemUtils.instance.setContext(context).setRingTone(data.get(position), ringToneIds.get(position), SET_AS_RINGTONE);
                    dialog.hide();
                }
            });
            notiImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SystemUtils.instance.setContext(context).setRingTone(data.get(position), ringToneIds.get(position), SET_AS_MSG);
                    dialog.hide();
                }
            });
            alarmImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SystemUtils.instance.setContext(context).setRingTone(data.get(position), ringToneIds.get(position), SET_AS_ALARM);
                    dialog.hide();
                }
            });
            dialog.show();
        }
    }

}
