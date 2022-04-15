package com.sharcodes.ortho.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.sharcodes.ortho.fragments.LoginFragment;
import com.sharcodes.ortho.adapters.LoginPagerAdapter;
import com.sharcodes.ortho.R;
import com.sharcodes.ortho.fragments.SignUpFragment;

public class LoginTabActivity extends AppCompatActivity implements SignUpFragment.OnFragmentInteractionListener, LoginFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_tab);

        TabLayout tb = findViewById(R.id.tabLayout);
        tb.addTab(tb.newTab().setText("Login"));
        tb.addTab(tb.newTab().setText("Signup"));

        tb.setTabGravity(TabLayout.GRAVITY_FILL);

        ViewPager vp = findViewById(R.id.viewPager);
        LoginPagerAdapter adapter = new LoginPagerAdapter(getSupportFragmentManager(), tb.getTabCount());
        vp.setAdapter(adapter);

        vp.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tb));

        tb.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

}