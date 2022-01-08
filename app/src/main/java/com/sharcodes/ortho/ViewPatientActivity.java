package com.sharcodes.ortho;

import static com.sharcodes.ortho.helper.JSONHelper.mapToJSON;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.sharcodes.ortho.helper.DBHelper;
import com.sharcodes.ortho.helper.JSONHelper;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ViewPatientActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    ViewExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    LinkedHashMap<String,LinkedHashMap<String,FormClass>> expandableListDetail;
    LinkedHashMap<String,LinkedHashMap<String,FormClass>> dummyDetail;

    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;


    ProgressDialog progressDialog;

    ListModel listModel;
    String docId;
    String data;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient);


        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        data = getIntent().getStringExtra("key");
        docId = getIntent().getStringExtra("docId");
        expandableListDetail = new Gson().fromJson(data, new TypeToken<LinkedHashMap<String,LinkedHashMap<String,FormClass>>>(){}.getType());
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        listModel = new ListModel();
        dummyDetail = listModel.getData();
        for(String topKey : dummyDetail.keySet()) {
            for(String key : dummyDetail.get(topKey).keySet()) {
                dummyDetail.get(topKey).replace(key,expandableListDetail.get(topKey).get(key));
            }
        }

        expandableListTitle = new ArrayList<String>(dummyDetail.keySet());
        expandableListAdapter = new ViewExpandableListAdapter(this, expandableListTitle, dummyDetail);
        expandableListView.setAdapter(expandableListAdapter);
        progressDialog = new ProgressDialog(this);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT
                ).show();
                return false;
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewPatientActivity.this, AddPatientActivity.class);
                intent.putExtra("docId",docId);


                String intentData = new Gson().toJson(dummyDetail);
                intent.putExtra("key",intentData);

                startActivity(intent);

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean offline = sharedPref.getBoolean("offline", false);

        if (offline) {

            DBHelper dbHelper = new DBHelper(this);
            String selection = "UUID" + " = ?";
            String[] selectionArgs = {docId};

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query("PATIENTS", null, selection, selectionArgs, null, null, null, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String data = cursor.getString(cursor.getColumnIndex("DATA"));
                    String uuid = cursor.getString(cursor.getColumnIndex("UUID"));
                    expandableListDetail = new Gson().fromJson(data, new TypeToken<LinkedHashMap<String, LinkedHashMap<String, FormClass>>>() {
                    }.getType());

                    for (String topKey : dummyDetail.keySet()) {
                        for (String key : dummyDetail.get(topKey).keySet()) {
                            dummyDetail.get(topKey).replace(key, expandableListDetail.get(topKey).get(key));
                        }
                    }
                    expandableListAdapter.notifyDataSetChanged();

                    cursor.moveToNext();
                }
            }


        } else {
            db.collection("users").document(docId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {


                    try {
                        String dataS = mapToJSON(documentSnapshot.getData()).toString();
                        expandableListDetail = new Gson().fromJson(dataS, new TypeToken<LinkedHashMap<String, LinkedHashMap<String, FormClass>>>() {
                        }.getType());
                        for (String topKey : dummyDetail.keySet()) {
                            for (String key : dummyDetail.get(topKey).keySet()) {
                                dummyDetail.get(topKey).replace(key, expandableListDetail.get(topKey).get(key));
                            }
                        }
                        expandableListAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });

        }
    }
}
