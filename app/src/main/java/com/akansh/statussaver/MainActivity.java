package com.akansh.statussaver;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import androidx.fragment.app.FragmentPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private String[] PERMISSIONS={
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    NavigationView navigationView;
    private FragmentImage fragmentImage;
    private FragmentVideo fragmentVideo;
    private DrawerLayout drawerLayout;
    private boolean isAppInitialised = false;
    private TextView title_label;
    private int currentMode;
    private ImageButton drawer_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.container);
        title_label = findViewById(R.id.title_label);
        drawer_btn = findViewById(R.id.drawer_btn);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_regular:
                        title_label.setText("WARegular");
                        initializeApp(Constants.R_STATUSES);
                        break;
                    case R.id.item_business:
                        title_label.setText("WABusiness");
                        initializeApp(Constants.B_STATUSES);
                        break;
                    case R.id.item_about:
                        BottomDialog bottomDialog = BottomDialog.newInstance();
                        bottomDialog.show(getSupportFragmentManager(),"");
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        drawer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        requestPerm();
    }

    public void requestPerm() {
        try {
            int flg = 0;
            int[] permissions = {
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE),
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            };
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i] != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        flg = 1;
                        requestPermissions(PERMISSIONS, 1);
                    }
                    break;
                }
            }
            if (flg == 0) {
                initializeApp(Constants.R_STATUSES);
            }
        }catch (Exception e) {
            //Do Nothing
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1) {
            initializeApp(Constants.R_STATUSES);
        }else{
            requestPerm();
        }
    }

    private void initializeApp(int mode) {
        if(!isAppInitialised) {
            currentMode = Constants.R_STATUSES;
            tabLayout = findViewById(R.id.tab_layout);
            viewPager = findViewById(R.id.view_pager);
            viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            fragmentImage = new FragmentImage(this, this);
            fragmentVideo = new FragmentVideo(this, this);

            viewPagerAdapter.addFragment(fragmentImage, "");
            viewPagerAdapter.addFragment(fragmentVideo, "");

            viewPager.setAdapter(viewPagerAdapter);

            tabLayout.setupWithViewPager(viewPager);

            tabLayout.getTabAt(0).setIcon(R.drawable.ic_simg_s);
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_vimg);

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    int pos = tab.getPosition();
                    if (pos == 0) {
                        tabLayout.getTabAt(0).setIcon(R.drawable.ic_simg_s);
                        tabLayout.getTabAt(1).setIcon(R.drawable.ic_vimg);
                    } else {
                        tabLayout.getTabAt(0).setIcon(R.drawable.ic_simg);
                        tabLayout.getTabAt(1).setIcon(R.drawable.ic_vimg_s);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }else{
            if(mode != currentMode) {
                fragmentVideo.clear();
                fragmentImage.clear();
                fragmentVideo.reload(mode);
                fragmentImage.reload(mode);
                currentMode = mode;
            }
        }
        isAppInitialised = true;
    }

    @Override
    public void onBackPressed() {
        try {
            finishAffinity();
        }catch (Exception e) {
            //Do Nothing
        }
        super.onBackPressed();
    }
}
