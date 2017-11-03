package com.logixity.apps.bestwallpapersandringtones;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import static com.logixity.apps.bestwallpapersandringtones.MainActivity.listsMap;
import static com.logixity.apps.bestwallpapersandringtones.MainActivity.wallpaperNames;

/**
 * Created by ahmed on 12/10/2017.
 */

public class WallpapersFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    int position = -1;
    //    RecyclerView onlineRecycler;
//    ArrayList<File> onlineFiles;
    ProgressBar mProgressBar;
    Button loadMoreBtn;
    FirebaseStorage mStorage;
    RelativeLayout progressLayout;
    int wallpapersLoaded;
    String key;
    String pathPrefix = "Wallpapers/";
    RecyclerView recyclerView;
    GridLayoutManager mLayoutManager;

    public static WallpapersFragment newInstance(int sectionNumber) {
        WallpapersFragment fragment = new WallpapersFragment();
        Bundle args = new Bundle();
        fragment.position = sectionNumber;
        if (fragment.position >= 0)
            fragment.wallpapersLoaded = listsMap.get(wallpaperNames.get(fragment.position)).size() - 4;
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wallp_tab, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerview);
//        onlineRecycler = rootView.findViewById(R.id.onlineWallpRecycler);
        loadMoreBtn = rootView.findViewById(R.id.loadMoreWBtn);
        loadMoreBtn.setOnClickListener(this);
        mLayoutManager = new GridLayoutManager(getContext(), 2);
        mProgressBar = rootView.findViewById(R.id.loadingMoreWProgress);
        progressLayout = rootView.findViewById(R.id.loading_layout);
        recyclerView.setLayoutManager(mLayoutManager);
//        onlineRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));

        if (position >= 0) {
            key = wallpaperNames.get(position);
            setAdapterByKey();
        }

        return rootView;
    }

    void setAdapterByKey() {
//        currentCategory = key;
//        getSupportActionBar().setTitle(key.substring(0, 1).toUpperCase() + key.substring(1) + " Wallpapers");
//        if(expandedImageView.getVisibility()==View.VISIBLE) {
//            expandedImageView.performClick();
//        }
//        if (slider.getVisibility() == View.VISIBLE) {
//            slider.performClick();
//        }

        pathPrefix += (key.charAt(0) + "").toUpperCase() + key.substring(1) + "/";
        MyAdapter adapter = new MyAdapter(getContext(), listsMap.get(key));
        recyclerView.setAdapter(adapter);
//        onlineFiles = new ArrayList<>();
//        onlineMap.put(key,onlineFiles);
//        MyAdapter onlineAdapter = new MyAdapter(getContext(), onlineFiles,true);
//        onlineRecycler.setAdapter(onlineAdapter);
        recyclerView.setNestedScrollingEnabled(false);
//        onlineRecycler.setNestedScrollingEnabled(false);

    }

    private void fetchWallpapers(final View view) {
        wallpapersLoaded = listsMap.get(key).size() - 4;
        try {
            if (isNetworkAvailable()) {
                try {
                    mProgressBar.setVisibility(View.VISIBLE);
                    progressLayout.setVisibility(View.VISIBLE);
                    view.setVisibility(View.GONE);
                    if (mStorage == null)
                        mStorage = FirebaseStorage.getInstance();
                    final StorageReference reference = mStorage.getReference().child(pathPrefix + (wallpapersLoaded + 1) + ".jpg");
                    final File localFile = File.createTempFile("wallp" + wallpapersLoaded + 1, "jpg");
                    reference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d("MADDY", "Success");
                            wallpapersLoaded++;

                            listsMap.get(key).add(0, localFile);
                            recyclerView.getAdapter().notifyDataSetChanged();


//                            onlineFiles.add(localFile);
//                        downloadedFilesList.add(localFile);

                            if (wallpapersLoaded % 6 != 0) {
                                fetchWallpapers(view);
                            } else {
                                view.setVisibility(View.VISIBLE);
                                mProgressBar.setVisibility(View.GONE);
                                progressLayout.setVisibility(View.GONE);
                                mLayoutManager.scrollToPosition(0);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            StorageException exception = (StorageException) e;
                            if (exception.getHttpResultCode() == 404) {
                                Log.d("MADDY", "Exception for " + pathPrefix + (wallpapersLoaded + 1));
                                mProgressBar.setVisibility(View.GONE);
                                progressLayout.setVisibility(View.GONE);
                                mLayoutManager.scrollToPosition(0);
                            } else {
                                Toast.makeText(getContext(), "Data Fetch Failed", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                                view.setVisibility(View.VISIBLE);
                                mProgressBar.setVisibility(View.GONE);
                                progressLayout.setVisibility(View.GONE);
                                mLayoutManager.scrollToPosition(0);
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
        } catch (Exception e) {
        }
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
        fetchWallpapers(view);
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
