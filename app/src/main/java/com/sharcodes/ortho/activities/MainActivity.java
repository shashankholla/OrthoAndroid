package com.sharcodes.ortho.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sharcodes.ortho.fragments.NotesFragment;
import com.sharcodes.ortho.fragments.PatientListFragmentOffline;
import com.sharcodes.ortho.fragments.PatientListFragmentOnline;
import com.sharcodes.ortho.R;

public class MainActivity extends AppCompatActivity {
    String unit;

    public String getUnit() {
        return unit;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Fragment fragment1 = new PatientListFragmentOffline();
        final Fragment fragment2 = new PatientListFragmentOnline();
        final Fragment fragment3 = new NotesFragment();
        final FragmentManager fm = getSupportFragmentManager();
        final Fragment[] active = {fragment1};

        unit = getIntent().getStringExtra("unit");

        BottomNavigationView navigation = findViewById(R.id.navigation);

        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.main_container, fragment1, "1").commit();

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.patientOffline:
                        fm.beginTransaction().hide(active[0]).show(fragment1).commit();
                        active[0] = fragment1;
                        return true;
                    case R.id.patientOnline:
                        fm.beginTransaction().hide(active[0]).show(fragment2).commit();
                        active[0] = fragment2;
                        return true;
                    case R.id.notes:
                        fm.beginTransaction().hide(active[0]).show(fragment3).commit();
                        active[0] = fragment3;
                        return true;
                }
                return false;
            }
        });


    }
}