package com.sharcodes.ortho;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NewActivity extends AppCompatActivity  {
    BottomNavigationView bottomNavigationView;
    private CustomAdapter listAdapter;
    private List<User> userList = new ArrayList<>();
    private RecyclerView recycler;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);





        recycler = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        //listAdapter = new CustomAdapter(userList, this);
        //recycler.setAdapter(listAdapter);

        //Load the date from the network or other resources
        //into the array list asynchronously
        db = FirebaseFirestore.getInstance();
        getData();


//        listAdapter.notifyDataSetChanged();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddPage();
            }
        });



    }
    public void goToAddPage(){
        Intent intent = new Intent(NewActivity.this, AddPatientActivity.class);
        startActivity(intent);
    }

    public void getData(){
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
                            Object patientDetails = obj.get("patientDetails");
                            Map<String,FormClass> patientDetailsMap = new Gson().fromJson( patientDetails.toString(), new TypeToken<Map<String,FormClass>>(){}.getType());
                            String patientName = patientDetailsMap.get("name").content;
                            User user = new User(patientName);
                            userList.add(user);

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
            }
        });
    }

    private JSONObject mapToJSON(Map<String, Object> map) throws JSONException {
        JSONObject obj = new JSONObject();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                Map<String, Object> subMap = (Map<String, Object>) value;
                obj.put(key, mapToJSON(subMap));
            } else if (value instanceof List) {
                obj.put(key, listToJSONArray((List) value));
            }
            else {
                obj.put(key, value);
            }
        }
        return obj;
    }

    private JSONArray listToJSONArray(List<Object> list) throws JSONException {
        JSONArray arr = new JSONArray();
        for(Object obj: list) {
            if (obj instanceof Map) {
                arr.put(mapToJSON((Map) obj));
            }
            else if(obj instanceof List) {
                arr.put(listToJSONArray((List) obj));
            }
            else {
                arr.put(obj);
            }
        }
        return arr;
    }
    

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        // Associate searchable configuration with the SearchView
       // SearchManager searchManager =(SearchManager) getSystemService(Context.SEARCH_SERVICE);SearchView searchView =(SearchView) menu.findItem(R.id.menu_search).getActionView();searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }
}

