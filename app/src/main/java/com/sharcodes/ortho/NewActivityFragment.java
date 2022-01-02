package com.sharcodes.ortho;

import static com.sharcodes.ortho.helper.JSONHelper.mapToJSON;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.sharcodes.ortho.helper.JSONHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewActivityFragment extends Fragment {
    BottomNavigationView bottomNavigationView;
    private CustomAdapter listAdapter;
    private List<HashMap<String,LinkedHashMap<String,FormClass>>> userList = new ArrayList<>();
    private List<String> docId = new ArrayList<>();
    private RecyclerView recycler;
    FirebaseFirestore db;
    SearchView searchView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ProgressDialog progressDialog;

    public NewActivityFragment() {
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
    public static NewActivityFragment newInstance(String param1, String param2) {
        NewActivityFragment fragment = new NewActivityFragment();
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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_activity, container, false);
        recycler = view.findViewById(R.id.recycler_view);
        searchView = view.findViewById(R.id.searchView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        recycler.setLayoutManager(layoutManager);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading...");
        listAdapter = new CustomAdapter(userList, getContext(), new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HashMap<String,LinkedHashMap<String,FormClass>> item) {
                Toast.makeText(getContext(), item.get("Biography").get("name").content, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), ViewPatientActivity.class);

                intent.putExtra("docId",item.get("docId").get("docId").id);
                item.remove("docId");

                String json = new Gson().toJson(item);
                intent.putExtra("key",json);

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
        getData();


//        listAdapter.notifyDataSetChanged();

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddPage();
            }
        });



        return view;
    }

    public void goToAddPage(){
        Intent intent = new Intent(getContext(), AddPatientActivity.class);

        startActivity(intent);
    }

    public void getData(){
        progressDialog.show();
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                            HashMap<String,LinkedHashMap<String,FormClass>> p = new Gson().fromJson( obj.toString(), new TypeToken<HashMap<String, LinkedHashMap<String,FormClass>>>(){}.getType());
                            //String patientName = patientDetailsMap.get("name").content;
                            LinkedHashMap<String, FormClass> lk = new LinkedHashMap<String, FormClass>();
                            lk.put("docId",new FormClass(document.getId()));
                            p.put("docId",lk);
                            userList.add(p);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }





//                          String patientName = data.get("patientDetails").get("name").get("content");

//                        String diagnosis = data.get("diagnosis").toString();
//                        String preop = data.get("preop").toString();
//                        String medicine = data.get("medicine").toString();
//                        String anaesthesia = data.get("anaesthesia").toString();
//                        String implants = data.get("implants").toString();
//                        String intraOp = data.get("intraOp").toString();
//                        String postOp = data.get("postOp").toString();
//                        String generalNotes = data.get("generalNotes").toString();
//                        String imageUrl = data.get("imageUrl").toString();

                    }
                    listAdapter.notifyDataSetChanged();
                    Log.d("Down", list.toString());
                } else {
                    Log.d("Down", "Error getting documents: ", task.getException());
                }
                progressDialog.hide();
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
        userList.clear();
        getData();
    }
}