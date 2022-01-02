package com.sharcodes.ortho;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PgActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pg);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.pg);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.pg:
                        Intent c = new Intent(PgActivity.this,PgActivity.class);
                        startActivity(c);
                        break;
                    case R.id.patient:
                        Intent a = new Intent(PgActivity.this,NewActivity.class);
                        startActivity(a);
                        break;
                    case R.id.notes:
                        Intent b = new Intent(PgActivity.this,NotesActivity.class);
                        startActivity(b);
                        break;

                }
                return false;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabpg);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddPage();
            }
        });

    }
    public void goToAddPage(){
        Intent intent = new Intent(PgActivity.this, PgaddActivity.class);
        startActivity(intent);
    }
    }
