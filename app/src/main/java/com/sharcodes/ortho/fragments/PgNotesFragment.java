package com.sharcodes.ortho.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.sharcodes.ortho.R;
import com.sharcodes.ortho.ViewPatientActivity;
import com.sharcodes.ortho.activities.AddPatientActivity;
import com.sharcodes.ortho.activities.MainActivity;
import com.sharcodes.ortho.adapters.CustomAdapter;
import com.sharcodes.ortho.helper.DBHelper;
import com.sharcodes.ortho.models.FormClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PgNotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PgNotesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final List<HashMap<String, LinkedHashMap<String, FormClass>>> userList = new ArrayList<>();
    private final List<String> docId = new ArrayList<>();
    BottomNavigationView bottomNavigationView;
    FirebaseFirestore db;
    SearchView searchView;
    String unit;
    ProgressDialog progressDialog;
    DBHelper dbHelper;
    private CustomAdapter listAdapter;
    private RecyclerView recycler;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PgNotesFragment() {
        // Required empty public constructor
    }


    public static PgNotesFragment newInstance(String param1, String param2) {
        PgNotesFragment fragment = new PgNotesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        dbHelper = new DBHelper(getContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MainActivity mainActivity = (MainActivity) getActivity();
        unit = mainActivity.getUnit();

        View view = inflater.inflate(R.layout.fragment_patient_list, container, false);
        recycler = view.findViewById(R.id.recycler_view);
        searchView = view.findViewById(R.id.searchView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        recycler.setLayoutManager(layoutManager);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading...");
        listAdapter = new CustomAdapter(userList, getContext(), new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HashMap<String, LinkedHashMap<String, FormClass>> item) {
                Toast.makeText(getContext(), item.get("Biography").get("name").content, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), ViewPatientActivity.class);

                intent.putExtra("docId", item.get("docId").get("docId").id);
                intent.putExtra("online", false);
                intent.putExtra("unit", unit);
                item.remove("docId");

                SharedPreferences.Editor sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                sharedPref.putBoolean("online", false).commit();


                String json = new Gson().toJson(item);
                intent.putExtra("key", json);

                startActivity(intent);
            }
        });
        recycler.setAdapter(listAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                listAdapter.getFilter().filter(s);
                return true;
            }
        });

        //Load the date from the network or other resources
        //into the array list asynchronously
        db = FirebaseFirestore.getInstance();

        getOfflineData();


        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddPage();
            }
        });


        return view;
    }

    public void goToAddPage() {
        Intent intent = new Intent(getContext(), AddPatientActivity.class);
        intent.putExtra("unit", unit);
        startActivity(intent);
    }

    public void getOfflineData() {
        progressDialog.show();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("PG_" +  unit, null, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String data = cursor.getString(cursor.getColumnIndex("DATA"));
                String uuid = cursor.getString(cursor.getColumnIndex("UUID"));
                HashMap<String, LinkedHashMap<String, FormClass>> p = new Gson().fromJson(data, new TypeToken<HashMap<String, LinkedHashMap<String, FormClass>>>() {
                }.getType());
                //String patientName = patientDetailsMap.get("name").content;
                LinkedHashMap<String, FormClass> lk = new LinkedHashMap<String, FormClass>();
                lk.put("docId", new FormClass(uuid));
                p.put("docId", lk);
                userList.add(p);
                cursor.moveToNext();
            }
        }
        Collections.reverse(userList);
        String queryHint = "Search for patients (Total: " + userList.size() + ")";
        searchView.setQueryHint(queryHint);

        listAdapter.notifyDataSetChanged();

        progressDialog.hide();
    }


    @Override
    public void onResume() {
        super.onResume();
        userList.clear();

        getOfflineData();

    }
}