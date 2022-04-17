package com.sharcodes.ortho.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sharcodes.ortho.fragments.NotesFragment;
import com.sharcodes.ortho.fragments.PatientListFragmentOffline;
import com.sharcodes.ortho.fragments.PatientListFragmentOnline;
import com.sharcodes.ortho.R;
import com.sharcodes.ortho.fragments.PgNotesFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String unit;

    public String getUnit() {
        return unit;
    }

    ArrayList<Fragment> order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Fragment fragment1 = new PatientListFragmentOffline();
        final Fragment fragment2 = new PatientListFragmentOnline();
        final Fragment fragment3 = new NotesFragment();
        final Fragment fragment4 = new PgNotesFragment();
        final FragmentManager fm = getSupportFragmentManager();
        final Fragment[] active = {fragment1};
        order = new ArrayList<Fragment>();
        order.add(fragment1);
        order.add(fragment2);
        order.add(fragment3);
        order.add(fragment4);

        unit = getIntent().getStringExtra("unit");

        BottomNavigationView navigation = findViewById(R.id.navigation);

        fm.beginTransaction().add(R.id.main_container, fragment4, "4").hide(fragment4).commit();
        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.main_container, fragment1, "1").commit();

        replaceFragmentWithAnimation(fragment1, "1", active[0]);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.patientOffline:
//                        fm.beginTransaction().hide(active[0]).show(fragment1).commit();
//                        active[0] = fragment1;
                        active[0] = replaceFragmentWithAnimation(fragment1, "1", active[0]);
                        return true;
                    case R.id.patientOnline:
//                        fm.beginTransaction().hide(active[0]).show(fragment2).commit();
//                        active[0] = fragment2;
                        active[0] = replaceFragmentWithAnimation(fragment2, "2", active[0]);
                        return true;
                    case R.id.notes:
//                        fm.beginTransaction().hide(active[0]).show(fragment3).commit();
//                        active[0] = fragment3;
                        active[0] = replaceFragmentWithAnimation(fragment3, "3", active[0]);
                        return true;
                    case R.id.pg:
//                        fm.beginTransaction().hide(active[0]).show(fragment3).commit();
//                        active[0] = fragment3;
                        active[0] = replaceFragmentWithAnimation(fragment4, "4", active[0]);
                        return true;
                }
                return false;
            }
        });


    }

    public Fragment replaceFragmentWithAnimation(Fragment fragment, String tag, Fragment active){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(order.indexOf(fragment) > order.indexOf(active)){
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_left);
        } else {
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        }


        transaction.hide(active);
        transaction.show(fragment);
        transaction.commit();
        return fragment;
    }
}