package com.sharcodes.ortho.fragments;

import static com.sharcodes.ortho.helper.JSONHelper.mapToJSON;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.sharcodes.ortho.R;
import com.sharcodes.ortho.ViewPatientActivity;
import com.sharcodes.ortho.activities.AddPatientActivity;
import com.sharcodes.ortho.activities.MainActivity;
import com.sharcodes.ortho.adapters.CustomAdapter;
import com.sharcodes.ortho.models.FormClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PatientListFragmentOnline#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientListFragmentOnline extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final List<HashMap<String, LinkedHashMap<String, FormClass>>> userList = new ArrayList<>();
    private final List<String> docId = new ArrayList<>();
    private final boolean offline = false;
    BottomNavigationView bottomNavigationView;
    FirebaseFirestore db;
    SearchView searchView;
    ProgressDialog progressDialog;
    String unit;
    private CustomAdapter listAdapter;
    private RecyclerView recycler;
    private FirebaseAuth mAuth;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PatientListFragmentOnline() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewActivityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PatientListFragmentOnline newInstance(String param1, String param2) {
        PatientListFragmentOnline fragment = new PatientListFragmentOnline();
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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());


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
                item.remove("docId");
                intent.putExtra("online", true);
                intent.putExtra("unit",unit);

                SharedPreferences.Editor sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                sharedPref.putBoolean("online", true).apply();

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

        getOnlineData();


//        listAdapter.notifyDataSetChanged();

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
        intent.putExtra("online", true);
        intent.putExtra("unit", unit);
        startActivity(intent);
    }


    public void getOnlineData() {
        progressDialog.show();

        mAuth = FirebaseAuth.getInstance();

        String user = mAuth.getCurrentUser().getUid();


        db.collection(user + "-" + unit).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    userList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> data = document.getData();

                        JSONObject obj;
                        try {

                            obj = mapToJSON(document.getData());
                            Object patientDetails = obj.get("Biography");
                            //LinkedHashMap<String,FormClass> patientDetailsMap = new Gson().fromJson( patientDetails.toString(), new TypeToken<LinkedHashMap<String,FormClass>>(){}.getType());
                            HashMap<String, LinkedHashMap<String, FormClass>> p = new Gson().fromJson(obj.toString(), new TypeToken<HashMap<String, LinkedHashMap<String, FormClass>>>() {
                            }.getType());
                            //String patientName = patientDetailsMap.get("name").content;
                            LinkedHashMap<String, FormClass> lk = new LinkedHashMap<String, FormClass>();
                            lk.put("docId", new FormClass(document.getId()));
                            p.put("docId", lk);
                            userList.add(p);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                    listAdapter.notifyDataSetChanged();
                    Log.d("Down", list.toString());
                } else {
                    Log.d("Down", "Error getting documents: ", task.getException());
                }

                String queryHint = "Search for patients (Total: " + userList.size() + ")";
                searchView.setQueryHint(queryHint);

                progressDialog.hide();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        userList.clear();

        getOnlineData();


    }
}