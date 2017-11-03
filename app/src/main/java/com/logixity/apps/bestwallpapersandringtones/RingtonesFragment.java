package com.logixity.apps.bestwallpapersandringtones;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class RingtonesFragment extends Fragment implements IFrag, View.OnClickListener {
    private static final String ARG_SECTION_NUMBER = "section_number";
    static int soundId;
    static File selectedFile;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    RecyclerView mediaList;
    ArrayList<String> mediaListItems;
    ArrayList<Object> ringToneIDs;
    ArrayList<ImageView> allViewsList;
    boolean played = false;
    ImageView currentView;
    int selectedImage;
    MediaRecyclerAdapter adapter;
    boolean iphoneRings = false;
    Button loadMoreBtn;
    FirebaseStorage mStorage;
    ProgressBar mProgressBar;
    int tonesLoaded = 0;
    String ringtonePrefix = "Ringtones/";

    public RingtonesFragment() {

    }

    public static RingtonesFragment newInstance(int sectionNumber) {
        RingtonesFragment fragment = new RingtonesFragment();
        if (sectionNumber == 1)
            fragment.iphoneRings = true;
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public RingtonesFragment setData() {
        final Field[] fields = R.raw.class.getFields();
        ringToneIDs = new ArrayList<>();
        mediaListItems = new ArrayList<>();
        allViewsList = new ArrayList<>();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (iphoneRings) {
                //ringtonePrefix+="Branded/";
                if (fieldName.startsWith("iphone_")) {
                    fieldName = fieldName.substring(7, 8).toUpperCase() + fieldName.substring(8);
                    fieldName = fieldName.replaceAll("_", " ");
                    mediaListItems.add(fieldName);
                    ringToneIDs.add(getResources().getIdentifier(field.getName(), "raw", getActivity().getPackageName()));
                }
            } else {
                if (fieldName.startsWith("ring_")) {
                    fieldName = fieldName.substring(5, 6).toUpperCase() + fieldName.substring(6);
                    fieldName = fieldName.replaceAll("_", " ");
                    mediaListItems.add(fieldName);
                    ringToneIDs.add(getResources().getIdentifier(field.getName(), "raw", getActivity().getPackageName()));
                }
            }

        }
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ringtones_tab, container, false);
        if (iphoneRings) {
            ringtonePrefix += "Branded/";
        } else {
            ringtonePrefix += "Custom/";
        }
//        NestedScrollView nestedScrollView = (NestedScrollView) rootView.findViewById(R.id.nested);
//        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                Log.d("SCROLL","Old Scroll: "+oldScrollY+", new: "+scrollY);
//                if(scrollY>oldScrollY){
//                    if(MainActivity.instance.getSupportActionBar().isShowing())
//                        MainActivity.instance.getSupportActionBar().hide();
//                } else if(scrollY<oldScrollY){
//                    if(!MainActivity.instance.getSupportActionBar().isShowing())
//                        MainActivity.instance.getSupportActionBar().show();
//                }
//            }
//        });
        mediaList = (RecyclerView) rootView.findViewById(R.id.MediaListView);
        mediaList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//        onlineMediaList = (RecyclerView) rootView.findViewById(R.id.RemoteMediaListView);
//        onlineMediaList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        loadMoreBtn = (Button) rootView.findViewById(R.id.loadMoreBtn);
        loadMoreBtn.setOnClickListener(this);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.loadingMoreProgress);

        setData();
        mediaList.setNestedScrollingEnabled(false);
//        onlineMediaList.setNestedScrollingEnabled(false);
//        ViewCompat.setNestedScrollingEnabled(mediaList, true);
        String tonePrefix = "";
        if (iphoneRings) {
            tonePrefix = "Branded Ringtone";
        } else {
            tonePrefix = "Custom Ringtone";
        }
        adapter = new MediaRecyclerAdapter(getContext(), mediaListItems, this, ringToneIDs,tonePrefix);
        mediaList.setAdapter(adapter);
//        downloadedFilesList = new ArrayList<>();
//        onlineAdapter = new MediaRecyclerAdapter(getContext(), this, downloadedFilesList, tonePrefix);
//        onlineMediaList.setAdapter(onlineAdapter);

//        mediaList.setOnClickListener(new SystemUtils.ItemClickHandler(ringToneIDs,mediaListItems,getContext()));
        return rootView;
    }

    @Override
    public ImageView getCurrentView() {
        return currentView;
    }

    @Override
    public void setCurrentView(ImageView view) {
        this.currentView = view;
    }

    @Override
    public int getSelectedImage() {
        return selectedImage;
    }

    @Override
    public void setSelectedImage(int i) {
        selectedImage = i;
    }

    @Override
    public ArrayList<ImageView> getAllViews() {
        return allViewsList;
    }

    @Override
    public void onClick(View view) {
        InterstitialAd fullScreenAd = App.instance.getFullScreenAd();
        if(App.instance.shouldShowAd()) {
            if(fullScreenAd!=null && fullScreenAd.isLoaded()) {
                fullScreenAd.show();
            } else {
                App.instance.requestNewInterstitial();
            }
        }
        fetchRingtones(view);
    }

    private void fetchRingtones(final View view) {
        try {
            if (isNetworkAvailable()) {
                try {
                    mProgressBar.setVisibility(View.VISIBLE);
                    view.setVisibility(View.GONE);
                    if (mStorage == null)
                        mStorage = FirebaseStorage.getInstance();
                    StorageReference reference = mStorage.getReference().child(ringtonePrefix + (tonesLoaded + 1) + ".mp3");

                    final File localFile = File.createTempFile("ring" + tonesLoaded + 1, "mp3");
                    reference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d("MADDY", "Success");
                            tonesLoaded++;
                            ringToneIDs.add(localFile);
//                        downloadedFilesList.add(localFile);
                            mediaList.getAdapter().notifyItemInserted(ringToneIDs.size()-1);
                            mediaList.getLayoutManager().scrollToPosition(ringToneIDs.size()-1);
                            if (tonesLoaded % 6 != 0) {
                                fetchRingtones(view);
                            } else {
                                view.setVisibility(View.VISIBLE);
                                mProgressBar.setVisibility(View.GONE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            StorageException exception = (StorageException) e;
                            if (exception.getHttpResultCode() == 404) {
                                Log.d("MADDY", "Exception for " + ringtonePrefix + (tonesLoaded + 1));
                                mProgressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getContext(), "Data Fetch Failed", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                                view.setVisibility(View.VISIBLE);
                                mProgressBar.setVisibility(View.GONE);
                            }

                        }
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.makeText(getContext(), "Unable to Load More Ringtones. Try Again Later", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), "No Internet Connection. Try Again Later", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {}

    }

    private boolean isNetworkAvailable() throws Exception {
        if (getContext() == null) {
            throw new Exception("");
        }
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();


    }
}
