package com.sharcodes.ortho;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Fragment fragment1 = new NewActivityFragment();
        final Fragment fragment2 = new NewActivityFragment();
        final Fragment fragment3 = new NewActivityFragment();
        final FragmentManager fm = getSupportFragmentManager();
        final Fragment[] active = {fragment1};

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.main_container,fragment1, "1").commit();

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.patient:
                        fm.beginTransaction().hide(active[0]).show(fragment1).commit();
                        active[0] = fragment1;
                        return true;
                    case R.id.notes:
                        fm.beginTransaction().hide(active[0]).show(fragment2).commit();
                        active[0] = fragment2;
                        return true;
                    case R.id.pg:
                        fm.beginTransaction().hide(active[0]).show(fragment3).commit();
                        active[0] = fragment3;
                        return true;
                }
                return false;
            }
        });



    }
}