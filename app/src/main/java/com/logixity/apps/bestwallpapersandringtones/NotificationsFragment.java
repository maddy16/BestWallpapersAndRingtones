package com.logixity.apps.bestwallpapersandringtones;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class NotificationsFragment extends Fragment implements IFrag,View.OnClickListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
//    private AdView mAdView;
    RecyclerView mediaList;
    ArrayList<String> mediaListItems;
    ArrayList<Object> ringToneIDs;
    ArrayList<ImageView> allViewsList;
    boolean played = false;
    ImageView currentView;
    int selectedImage;
    MediaRecyclerAdapter adapter;
    boolean iphoneRings=false;
    Button loadMoreBtn;
    FirebaseStorage mStorage;
    ProgressBar mProgressBar;
    int tonesLoaded=0;
    String ringtonePrefix = "Ringtones/Notification/";
    private static final String ARG_SECTION_NUMBER = "section_number";

    public NotificationsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NotificationsFragment newInstance(int sectionNumber) {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notifications_tab, container, false);

//        mAdView = (AdView) rootView.findViewById(R.id.notiBannerAd);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        mediaList = (RecyclerView) rootView.findViewById(R.id.NotiListView);
        mediaList.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
//        onlineMediaList = (RecyclerView) rootView.findViewById(R.id.RemoteNotiListView);
//        onlineMediaList.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        loadMoreBtn = (Button)rootView.findViewById(R.id.loadMoreNotiBtn);
        loadMoreBtn.setOnClickListener(this);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.loadingMoreNotiProgress);

        final Field[] fields = R.raw.class.getFields();
        mediaListItems = new ArrayList<>();
        ringToneIDs = new ArrayList<>();
        allViewsList = new ArrayList<>();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName.startsWith("noti_")) {
                fieldName = fieldName.substring(5, 6).toUpperCase() + fieldName.substring(6);
                fieldName = fieldName.replaceAll("_", " ");
                mediaListItems.add(fieldName);
                ringToneIDs.add(getResources().getIdentifier(field.getName(), "raw", getActivity().getPackageName()));
            }
        }
        mediaList.setNestedScrollingEnabled(false);
//        onlineMediaList.setNestedScrollingEnabled(false);
//        ViewCompat.setNestedScrollingEnabled(mediaList, true);

        adapter = new MediaRecyclerAdapter(getContext(), mediaListItems,this,ringToneIDs,"Notification Sound");
        mediaList.setAdapter(adapter);
//        downloadedFilesList = new ArrayList<>();
//        onlineAdapter = new MediaRecyclerAdapter(getContext(), this,downloadedFilesList,"Notification Sound");
//        onlineMediaList.setAdapter(onlineAdapter);
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
    public void setSelectedImage(int i) {
        selectedImage = i;
    }

    @Override
    public int getSelectedImage() {
        return selectedImage;
    }

    @Override
    public ArrayList<ImageView> getAllViews() {
        return allViewsList;
    }

    @Override
    public void onClick(View view) {
        fetchRingtones(view);
    }
    private void fetchRingtones(final View view){
        if(isNetworkAvailable()){
            try {
                mProgressBar.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);
                if(mStorage==null)
                    mStorage = FirebaseStorage.getInstance();
                StorageReference reference = mStorage.getReference().child(ringtonePrefix+(tonesLoaded+1)+".mp3");

                final File localFile = File.createTempFile("noti_"+tonesLoaded+1, "mp3");
                reference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d("MADDY","Success");
                        tonesLoaded++;
                        ringToneIDs.add(localFile);
//                        downloadedFilesList.add(localFile);
                        mediaList.getAdapter().notifyDataSetChanged();
                        if(tonesLoaded%6!=0){
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
                        if(exception.getHttpResultCode()==404){
                            Log.d("MADDY","Exception for " + ringtonePrefix+(tonesLoaded+1));
                            mProgressBar.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(getContext(), "Data Fetch Failed", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                            view.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                        }

                    }
                });

            } catch (Exception ex){
                ex.printStackTrace();
                Toast.makeText(getContext(), "Unable to Load More Ringtones. Try Again Later", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), "No Internet Connection. Try Again Later", Toast.LENGTH_LONG).show();
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
