package com.logixity.apps.bestwallpapersandringtones;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import static com.logixity.apps.bestwallpapersandringtones.App.MODE_RINGTONES;
import static com.logixity.apps.bestwallpapersandringtones.App.MODE_WALLPAPERS;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static MediaPlayer player;
    static ArrayList<Integer> colors;
    static HashMap<String, ArrayList<Object>> listsMap;
    //    static HashMap<String, ArrayList<File>> onlineMap;
    static MainActivity instance;
    //    File selectedFile=null;
    static ArrayList<String> wallpaperNames;
    static int selectedMode = MODE_RINGTONES;
    ArrayList<Object> tempData;
    TabLayout tbl_pages;
    boolean isZoomed;
    String currentCategory;
    ViewPager slider;
    Object selectedObject;
    AdView mAdView;
    //    int selectedImage = -1;
//    int zoomedImage = -1;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private ViewPager mViewPager;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_rings:
                    if (selectedMode != MODE_RINGTONES) {
                        selectedMode = MODE_RINGTONES;

                        setTabs();

                    }
                    break;
                case R.id.navigation_wallpapers:
                    if (selectedMode != MODE_WALLPAPERS) {
                        selectedMode = MODE_WALLPAPERS;
                        setTabs();
                    }
                    break;

            }
            return true;
        }

    };

    static void makeColors() {
        colors = new ArrayList<>();
        colors.add(R.color.material_1);
        colors.add(R.color.material_2);
        colors.add(R.color.material_3);
        colors.add(R.color.material_4);
        colors.add(R.color.material_5);
        colors.add(R.color.material_6);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAdView = (AdView) findViewById(R.id.bannerAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        instance = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_content);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        makeColors();

        tbl_pages = (TabLayout) findViewById(R.id.tbl_pages);
        prepareWallpapersData();
        initTabs();
        slider = (ViewPager) findViewById(R.id.image_slider);
        slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                if(selectedFile==null)
                if (tempData != null)
                    selectedObject = tempData.get(position);
                InterstitialAd fullScreenAd = App.instance.getFullScreenAd();
                if(App.instance.shouldShowAd()) {
                    if(fullScreenAd!=null && fullScreenAd.isLoaded()) {
                        fullScreenAd.show();
                    } else {
                        App.instance.requestNewInterstitial();
                    }
                }
//                else {
//                    selectedFile = onlineMap.get(currentCategory).get(position);
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);


    }

    void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaRecyclerAdapter.stopPlayback();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MediaRecyclerAdapter.stopPlayback();
    }

    void showMoreApps() {
        Uri uri = Uri.parse("market://search?q=pub:Logixity Studios");
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.fb_like_menu:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/logixityInc/"));
                startActivity(intent);
                break;
            case R.id.rate_app_menu:
                rateApp();
                break;
            case R.id.show_more_menu:
                showMoreApps();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_content);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void prepareWallpapersData() {
        listsMap = new HashMap<>();
        wallpaperNames = new ArrayList<>();
        PopupMenu p = new PopupMenu(this, null);
        Menu menu = p.getMenu();
        getMenuInflater().inflate(R.menu.wallpapers_menu, menu);
        for (int i = 0; i < menu.size(); i++) {
            String title = menu.getItem(i).getTitle().toString();
            if (title.contains(" ")) {
                title = title.split(" ")[0];
            }
            Log.d("MADDY", title.toLowerCase().trim() + ">");
            listsMap.put(title.toLowerCase(), new ArrayList<Object>());
            wallpaperNames.add(title.toLowerCase());
        }
        currentCategory = wallpaperNames.get(0);
        Field[] fields = R.drawable.class.getFields();
        for (Field field : fields) {
            String name = field.getName();
            if (name.startsWith("wallp")) {
                String[] words = name.split("_");
                String key = words[1].toLowerCase();
                if (key.contains(" ")) {
                    key = key.split(" ")[0];
                }
                listsMap.get(key.trim()).add(getResources().getIdentifier(field.getName(), "drawable", getPackageName()));
            }
        }
    }

    void initTabs() {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(6);
        tbl_pages.setupWithViewPager(mViewPager);
//        tbl_pages.setTabMode(TabLayout.MODE_SCROLLABLE);
//        tbl_pages.setTabGravity(TabLayout.GRAVITY_CENTER);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MediaRecyclerAdapter.stopPlayback();
                InterstitialAd fullScreenAd = App.instance.getFullScreenAd();
                if(App.instance.shouldShowAd()) {
                    if(fullScreenAd!=null && fullScreenAd.isLoaded()) {
                        fullScreenAd.show();
                    } else {
                        App.instance.requestNewInterstitial();
                    }
                }
                if (selectedMode == MODE_WALLPAPERS)
                    currentCategory = wallpaperNames.get(position);
//                try {
//                    InterstitialAd fullScreenAd = App.instance.getFullScreenAd();
//                    if (fullScreenAd!=null && fullScreenAd.isLoaded()) {
//                        fullScreenAd.show();
//                    } else {
//                        Log.d("MADDY", "Interstitial Not Loaded");
//                        App.instance.requestNewInterstitial();
//                    }
//                } catch (Exception ex) {}
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_content);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (slider.getVisibility() == View.VISIBLE) {
                slider.performClick();
            } else {
                super.onBackPressed();
            }
        }
    }

    void setWallpaper() {
        InterstitialAd fullScreenAd = App.instance.getFullScreenAd();

        if (fullScreenAd != null && fullScreenAd.isLoaded()) {
            fullScreenAd.show();
        } else {
            App.instance.requestNewInterstitial();
        }

        final ProgressBar pb = (ProgressBar) findViewById(R.id.wallpaperSpinner);
        final Button btn = (Button) findViewById(R.id.setWBtn);
        btn.setVisibility(View.VISIBLE);


        AsyncTask task = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                pb.setVisibility(View.VISIBLE);
                btn.setVisibility(View.GONE);
            }

            @Override
            protected Object doInBackground(Object[] params) {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(MainActivity.this);
                if (selectedObject != null) {
                    Bitmap bitmap = null;
                    if (selectedObject instanceof Integer) {
                        Integer resourceId = (Integer) selectedObject;
                        Drawable drawable = getResources().getDrawable(resourceId);
                        bitmap = ((BitmapDrawable) drawable).getBitmap();

                    } else if (selectedObject instanceof File) {

                        bitmap = BitmapFactory.decodeFile(((File) selectedObject).getPath());
                    }
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int height = displayMetrics.heightPixels;
                    int width = displayMetrics.widthPixels;
                    bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
                    try {

                        wallpaperManager.setBitmap(bitmap);
                        return true;

                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                }
                return false;
            }

            @Override
            protected void onPostExecute(Object o) {
                pb.setVisibility(View.INVISIBLE);
                btn.setVisibility(View.VISIBLE);
                Boolean flag = (Boolean) o;
                if (flag) {
                    Toast.makeText(MainActivity.this, "Wallpaper Set Successfully.", Toast.LENGTH_SHORT).show();
//                    InterstitialAd fullScreenAd = App.instance.getFullScreenAd();
//                    if (fullScreenAd.isLoaded()) {
//                        fullScreenAd.show();
//                    } else {
//                        Log.d("MADDY", "Interstitial Not Loaded");
//                        App.instance.requestNewInterstitial();
////                    App.instance.countIntAd--;
//                    }
                } else
                    Toast.makeText(MainActivity.this, "Failed to Set Wallpaper.", Toast.LENGTH_SHORT).show();
            }
        };
        task.execute();
    }

    void setTabs() {
        InterstitialAd fullScreenAd = App.instance.getFullScreenAd();
        if(App.instance.shouldShowAd()) {
            if(fullScreenAd!=null && fullScreenAd.isLoaded()) {
                fullScreenAd.show();
            } else {
                App.instance.requestNewInterstitial();
            }
        }
        MediaRecyclerAdapter.stopPlayback();
        if (selectedMode == MODE_RINGTONES) {
            tbl_pages.setTabMode(TabLayout.MODE_FIXED);
        } else if (selectedMode == MODE_WALLPAPERS) {
            tbl_pages.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        mSectionsPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(0);

    }

    void zoomImageFromThumb(final View thumbView, int currentItem) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        ImageAdapter adapter = null;

        tempData = new ArrayList<>();
        tempData.addAll(listsMap.get(currentCategory));
        adapter = new ImageAdapter(this, tempData);
        slider.setAdapter(adapter);
        slider.setCurrentItem(currentItem);
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        final View contents = findViewById(R.id.allContent);
        contents.setVisibility(View.INVISIBLE);

        // Load the high-resolution "zoomed-in" image.
//
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        isZoomed = true;
//        invalidateOptionsMenu();

//        if(selectedFile==null) {
//            adapter = new ImageAdapter(this, listsMap.get(currentCategory));
//            slider.setAdapter(adapter);
//            slider.setCurrentItem(currentItem);
//        }
//        else {
//            adapter = new ImageAdapter(this, listsMap.get(currentCategory),onlineMap.get(currentCategory));
//            slider.setAdapter(adapter);
//            slider.setCurrentItem(currentItem+4);
//        }

//        expandedImageView.setImageResource(imageResId);
        Button btn = (Button) findViewById(R.id.setWBtn);
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instance.setWallpaper();
            }
        });
        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.main_content)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        findViewById(R.id.main_content).setBackgroundColor(Color.BLACK);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
//        expandedImageView.setVisibility(View.VISIBLE);
        slider.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
//        expandedImageView.setPivotX(0f);
//        expandedImageView.setPivotY(0f);
        slider.setPivotX(0f);
        slider.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
//        AnimatorSet set = new AnimatorSet();
//        set
//                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
//                        startBounds.left, finalBounds.left))
//                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
//                        startBounds.top, finalBounds.top))
//                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
//                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
//                View.SCALE_Y, startScale, 1f));
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(slider, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(slider, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(slider, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(slider,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        slider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    getWindow().getAttributes().flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                }
                contents.setVisibility(View.VISIBLE);
                findViewById(R.id.setWBtn).setVisibility(View.INVISIBLE);
                findViewById(R.id.main_content).setBackgroundColor(Color.WHITE);
                isZoomed = false;
                selectedObject = null;
                tempData = null;

//                invalidateOptionsMenu();

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
//                AnimatorSet set = new AnimatorSet();
//                set.play(ObjectAnimator
//                        .ofFloat(expandedImageView, View.X, startBounds.left))
//                        .with(ObjectAnimator
//                                .ofFloat(expandedImageView,
//                                        View.Y, startBounds.top))
//                        .with(ObjectAnimator
//                                .ofFloat(expandedImageView,
//                                        View.SCALE_X, startScaleFinal))
//                        .with(ObjectAnimator
//                                .ofFloat(expandedImageView,
//                                        View.SCALE_Y, startScaleFinal));
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(slider, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(slider,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(slider,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(slider,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
//                        expandedImageView.setVisibility(View.GONE);
                        slider.setVisibility(View.GONE);
                        mCurrentAnimator = null;
//                        showFullScreenAd();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
//                        expandedImageView.setVisibility(View.GONE);
                        slider.setVisibility(View.GONE);
                        mCurrentAnimator = null;
//                        showFullScreenAd();
                    }
                });
                set.start();
                mCurrentAnimator = set;

            }
        });

    }

    public static class ImageZoomer implements View.OnClickListener {

        int imageId;
        int currentItem;
        //        File imageFile;
        Object image;

        public ImageZoomer(Object image, int currentItem) {
            this.image = image;
            this.currentItem = currentItem;
        }

        @Override
        public void onClick(View v) {
            instance.selectedObject = image;
            InterstitialAd fullScreenAd = App.instance.getFullScreenAd();
            if(App.instance.shouldShowAd()) {
                if(fullScreenAd!=null && fullScreenAd.isLoaded()) {
                    fullScreenAd.show();
                } else {
                    App.instance.requestNewInterstitial();
                }
            }
            instance.zoomImageFromThumb(v, currentItem);
        }
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a RingtonesFragment (defined as a static inner class below).
            if (selectedMode == MODE_RINGTONES) {
                switch (position) {
                    case 0:
                        return RingtonesFragment.newInstance(position + 1);
                    case 1:
                        return NotificationsFragment.newInstance(position + 1);
                    case 2:
                        return RingtonesFragment.newInstance(position + 1);
                }
            } else if (selectedMode == MODE_WALLPAPERS) {
                return WallpapersFragment.newInstance(position);
            }

            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            if (selectedMode == MODE_RINGTONES) {
                return 3;
            } else if (selectedMode == MODE_WALLPAPERS) {
                return wallpaperNames.size();
            }
            return 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (selectedMode == MODE_RINGTONES) {
                switch (position) {
                    case 0:
                        return "Branded";
                    case 1:
                        return "Alerts";
                    case 2:
                        return "Custom";
                }
            } else if (selectedMode == MODE_WALLPAPERS) {
                return wallpaperNames.get(position);
            }
            return null;
        }
    }


}