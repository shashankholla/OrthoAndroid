package com.sharcodes.ortho;

import static com.sharcodes.ortho.helper.JSONHelper.mapToJSON;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
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
import com.sharcodes.ortho.helper.DBHelper;
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

    private boolean offline = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ProgressDialog progressDialog;
    DBHelper dbHelper;
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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        String text = sharedPref.getBoolean("offline",false) ? "True" : "False";
//        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
        offline = sharedPref.getBoolean("offline",false);

        dbHelper = new DBHelper(getContext());

        SQLiteDatabase db = dbHelper.getWritableDatabase();

//        ContentValues values = new ContentValues();
//        values.put("UUID", "123");
//        values.put("DATA", "{\"Post Op\":{\"complaints\":{\"imagePath\":{},\"id\":\"complaints\",\"title\":\"Complaints\",\"group\":\"Post Op\"},\"postoporder\":{\"imagePath\":{},\"id\":\"postoporder\",\"title\":\"Postoporder\",\"group\":\"Post Op\"},\"postoprounds\":{\"imagePath\":{},\"id\":\"postoprounds\",\"title\":\"Postoprounds\",\"group\":\"Post Op\"}},\"PreOp\":{\"preopWorkout\":{\"imagePath\":{},\"id\":\"preopWorkout\",\"title\":\"Preop Workout\",\"content\":\"\",\"group\":\"PreOp\"},\"postOpPrep\":{\"imagePath\":{},\"id\":\"postOpPrep\",\"title\":\"Postop Preparation\",\"content\":\"\",\"group\":\"PreOp\"},\"xrayCTMRI\":{\"imagePath\":{},\"id\":\"xrayCTMRI\",\"title\":\"Preop Xra CT MRI\",\"group\":\"PreOp\"},\"good\":{\"imagePath\":{},\"id\":\"good\",\"title\":\"Good\",\"group\":\"PreOp\"},\"implants\":{\"imagePath\":{},\"id\":\"implants\",\"title\":\"Implants\",\"content\":\"\",\"group\":\"PreOp\"},\"preopCT\":{\"imagePath\":{},\"id\":\"preopCT\",\"title\":\"Pre OP CT\",\"group\":\"PreOp\"},\"preopMRI\":{\"imagePath\":{},\"id\":\"preopMRI\",\"title\":\"Pre OP MRI\",\"group\":\"PreOp\"},\"find\":{\"imagePath\":{},\"id\":\"find\",\"title\":\"Find\",\"group\":\"PreOp\"},\"fitness\":{\"imagePath\":{\"Click-IMG-20220101-WA0011.jpg\":\"https:\\/\\/firebasestorage.googleapis.com\\/v0\\/b\\/ortho-cb424.appspot.com\\/o\\/images%2FPreOp%2Ffitness%2F72616989-f227-4855-93b4-63b3399854cb?alt=media&token=2bb83c61-9fc1-4698-978f-8ed70a04a7cb\"},\"id\":\"fitness\",\"title\":\"Fitness\",\"content\":\"test fitness\",\"group\":\"PreOp\"},\"afitness\":{\"imagePath\":{},\"id\":\"afitness\",\"title\":\"Anaesthesia Fitness\",\"content\":\"\",\"group\":\"PreOp\"},\"bills\":{\"imagePath\":{},\"id\":\"bills\",\"title\":\"Bills\",\"content\":\"\",\"group\":\"PreOp\"},\"details\":{\"imagePath\":{},\"id\":\"details\",\"title\":\"Details\",\"content\":\"\",\"group\":\"PreOp\"},\"preopClinic\":{\"imagePath\":{},\"id\":\"preopClinic\",\"title\":\"Pre OP Clinical Status\",\"group\":\"PreOp\"},\"poOthers\":{\"imagePath\":{\"Click-IMG_20220102_134104473.jpg\":\"https:\\/\\/firebasestorage.googleapis.com\\/v0\\/b\\/ortho-cb424.appspot.com\\/o\\/images%2FPreOp%2FpoOthers%2F54fe7f65-c611-4d37-a81a-a674ee24969e?alt=media&token=59384349-0517-4ac5-8003-5d63f72d1d68\"},\"id\":\"poOthers\",\"title\":\"Others\",\"content\":\"\",\"group\":\"PreOp\"},\"recent\":{\"imagePath\":{},\"id\":\"recent\",\"title\":\"Recent\",\"content\":\"\",\"group\":\"PreOp\"},\"guide\":{\"imagePath\":{},\"id\":\"guide\",\"title\":\"Guide\",\"content\":\"\",\"group\":\"PreOp\"},\"preopXray\":{\"imagePath\":{},\"id\":\"preopXray\",\"title\":\"Pre OP Xray\",\"group\":\"PreOp\"},\"bloodArrang\":{\"imagePath\":{},\"id\":\"bloodArrang\",\"title\":\"Blood Arrangement\",\"group\":\"PreOp\"}},\"Biography\":{\"phnumber\":{\"imagePath\":{\"Click-IMG_20220102_134304316.jpg\":\"https:\\/\\/firebasestorage.googleapis.com\\/v0\\/b\\/ortho-cb424.appspot.com\\/o\\/images%2FBiography%2Fphnumber%2F6d7f0fb1-9ac1-4548-88bc-3328e6f19952?alt=media&token=c1fab515-4127-49a3-9edc-2a21a1a31f8b\"},\"id\":\"phnumber\",\"title\":\"Number\",\"content\":\"test\",\"group\":\"Biography\"},\"name\":{\"imagePath\":{\"Click-IMG_20220102_133658524.jpg\":\"https:\\/\\/firebasestorage.googleapis.com\\/v0\\/b\\/ortho-cb424.appspot.com\\/o\\/images%2FBiography%2Fname%2F02c2470d-a6bd-4226-aec4-e34d4cda59f3?alt=media&token=b46cd31f-4c0a-43dd-bc2d-4de9553bd77b\",\"Click-IMG-20220101-WA0024.jpeg\":\"https:\\/\\/firebasestorage.googleapis.com\\/v0\\/b\\/ortho-cb424.appspot.com\\/o\\/images%2FBiography%2Fname%2Fe3713cd5-75e5-4875-8360-d426bad1d946?alt=media&token=a70ede32-ddff-4af3-8426-679b62ee1ba2\"},\"id\":\"name\",\"title\":\"Name\",\"content\":\"new guy\",\"group\":\"Biography\"},\"diagnosis\":{\"imagePath\":{\"Click-IMG_20220102_134045631.jpg\":\"https:\\/\\/firebasestorage.googleapis.com\\/v0\\/b\\/ortho-cb424.appspot.com\\/o\\/images%2FBiography%2Fdiagnosis%2F22b1d9c8-9ab0-4643-8d98-007c9b230bae?alt=media&token=6594b904-3954-4216-b0ba-3492f11de8cc\",\"IMG_20220102_142547264.jpg\":\"https:\\/\\/firebasestorage.googleapis.com\\/v0\\/b\\/ortho-cb424.appspot.com\\/o\\/images%2FBiography%2Fdiagnosis%2Fa140c4a3-6e6c-46fb-b0f1-776ce1f94147?alt=media&token=6595fd9d-7f4b-4e4a-932b-864f08874731\"},\"id\":\"diagnosis\",\"title\":\"Diagnosis\",\"content\":\"\",\"group\":\"Biography\"},\"history\":{\"imagePath\":{\"Click-IMG-20220101-WA0024.jpeg\":\"https:\\/\\/firebasestorage.googleapis.com\\/v0\\/b\\/ortho-cb424.appspot.com\\/o\\/images%2FBiography%2Fhistory%2Fbb2a840b-36f6-4e07-a6b4-d782d5451f17?alt=media&token=34e63fc3-af4b-4514-85fe-df5d4fe05dcc\"},\"id\":\"history\",\"title\":\"History\",\"content\":\"history\",\"group\":\"Biography\"},\"rounds\":{\"imagePath\":{\"Click-IMG_20220102_134052205.jpg\":\"https:\\/\\/firebasestorage.googleapis.com\\/v0\\/b\\/ortho-cb424.appspot.com\\/o\\/images%2FBiography%2Frounds%2F6caf128b-62ff-4731-a886-9841ce913f5d?alt=media&token=4d5f4999-8707-4203-a857-815988d5d0c1\"},\"id\":\"rounds\",\"title\":\"Rounds\",\"content\":\"\",\"group\":\"Biography\"}},\"patientDetails\":{\"pxray\":{\"imagePath\":{},\"id\":\"pxray\",\"title\":\"Pxray\",\"content\":\"\",\"group\":\"patientDetails\"},\"treatment\":{\"imagePath\":{},\"id\":\"treatment\",\"title\":\"Treatment\",\"group\":\"patientDetails\"},\"rehab\":{\"imagePath\":{},\"id\":\"rehab\",\"title\":\"Rehab\",\"content\":\"\",\"group\":\"patientDetails\"},\"newthings\":{\"imagePath\":{},\"id\":\"newthings\",\"title\":\"Newthings\",\"content\":\"\",\"group\":\"patientDetails\"},\"findings\":{\"imagePath\":{},\"id\":\"findings\",\"title\":\"Findings\",\"content\":\"\",\"group\":\"patientDetails\"},\"iimplants\":{\"imagePath\":{},\"id\":\"iimplants\",\"title\":\"Iimplants\",\"content\":\"\",\"group\":\"patientDetails\"},\"movements\":{\"imagePath\":{},\"id\":\"movements\",\"title\":\"Movements\",\"content\":\"\",\"group\":\"patientDetails\"},\"approach\":{\"imagePath\":{},\"id\":\"approach\",\"title\":\"Approach\",\"content\":\"\",\"group\":\"patientDetails\"},\"fxray\":{\"imagePath\":{},\"id\":\"fxray\",\"title\":\"Fxray\",\"group\":\"patientDetails\"},\"cpreop\":{\"imagePath\":{},\"id\":\"cpreop\",\"title\":\"Cpreop\",\"group\":\"patientDetails\"},\"skills\":{\"imagePath\":{},\"id\":\"skills\",\"title\":\"Skills\",\"content\":\"\",\"group\":\"patientDetails\"},\"duration\":{\"imagePath\":{},\"id\":\"duration\",\"title\":\"Duration\",\"content\":\"\",\"group\":\"patientDetails\"},\"cdiagnosis\":{\"imagePath\":{},\"id\":\"cdiagnosis\",\"title\":\"Cdiagnosis\",\"group\":\"patientDetails\"},\"fphysio\":{\"imagePath\":{},\"id\":\"fphysio\",\"title\":\"Fphysio\",\"group\":\"patientDetails\"},\"dressing\":{\"imagePath\":{},\"id\":\"dressing\",\"title\":\"Dressing\",\"content\":\"\",\"group\":\"patientDetails\"},\"discharge\":{\"imagePath\":{},\"id\":\"discharge\",\"title\":\"Discharge\",\"content\":\"\",\"group\":\"patientDetails\"},\"class\":{\"imagePath\":{},\"id\":\"class\",\"title\":\"Class\",\"group\":\"patientDetails\"},\"rest\":{\"imagePath\":{},\"id\":\"rest\",\"title\":\"Rest\",\"content\":\"\",\"group\":\"patientDetails\"},\"reminder\":{\"imagePath\":{},\"id\":\"reminder\",\"title\":\"Reminder\",\"content\":\"\",\"group\":\"patientDetails\"},\"cpost\":{\"imagePath\":{},\"id\":\"cpost\",\"title\":\"Cpost\",\"group\":\"patientDetails\"},\"cconc\":{\"imagePath\":{},\"id\":\"cconc\",\"title\":\"Cconc\",\"group\":\"patientDetails\"},\"physio\":{\"imagePath\":{},\"id\":\"physio\",\"title\":\"Physio\",\"content\":\"\",\"group\":\"patientDetails\"},\"weight\":{\"imagePath\":{},\"id\":\"weight\",\"title\":\"Weight\",\"content\":\"\",\"group\":\"patientDetails\"},\"cprognosis\":{\"imagePath\":{},\"id\":\"cprognosis\",\"title\":\"Cprognosis\",\"group\":\"patientDetails\"},\"pdressing\":{\"imagePath\":{},\"id\":\"pdressing\",\"title\":\"Pdressing\",\"content\":\"\",\"group\":\"patientDetails\"},\"procedure\":{\"imagePath\":{},\"id\":\"procedure\",\"title\":\"Procedure\",\"content\":\"\",\"group\":\"patientDetails\"},\"prognosis\":{\"imagePath\":{},\"id\":\"prognosis\",\"title\":\"Prognosis\",\"group\":\"patientDetails\"},\"inv\":{\"imagePath\":{},\"id\":\"inv\",\"title\":\"Inv\",\"content\":\"\",\"group\":\"patientDetails\"},\"followup\":{\"imagePath\":{},\"id\":\"followup\",\"title\":\"Followup\",\"content\":\"\",\"group\":\"patientDetails\"},\"consultant\":{\"imagePath\":{},\"id\":\"consultant\",\"title\":\"Consultant\",\"content\":\"\",\"group\":\"patientDetails\"},\"inventions\":{\"imagePath\":{\"Click-IMG_20220102_133952092.jpg\":\"https:\\/\\/firebasestorage.googleapis.com\\/v0\\/b\\/ortho-cb424.appspot.com\\/o\\/images%2FpatientDetails%2Finventions%2F1ac94116-60c3-4644-a204-d1383b82581d?alt=media&token=3efc8522-710c-45a7-be71-efa0e8b9a771\"},\"id\":\"inventions\",\"title\":\"Inventions\",\"content\":\"\",\"group\":\"patientDetails\"},\"scrubbing\":{\"imagePath\":{},\"id\":\"scrubbing\",\"title\":\"Scrubbing\",\"content\":\"\",\"group\":\"patientDetails\"},\"diagnosispost\":{\"imagePath\":{},\"id\":\"diagnosispost\",\"title\":\"Diagnosispost\",\"content\":\"\",\"group\":\"patientDetails\"},\"status\":{\"imagePath\":{},\"id\":\"status\",\"title\":\"Status\",\"content\":\"\",\"group\":\"patientDetails\"}},\"patientDetails2\":{\"exam\":{\"imagePath\":{},\"id\":\"exam\",\"title\":\"Exam\",\"group\":\"patientDetails2\"}}}");
//
//        db.insert("PATIENTS", null, values);


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
        if(offline) {
            getOfflineData();
        }else {
            getOnlineData();
        }

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

    public void getOfflineData() {
        progressDialog.show();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("PATIENTS", null, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String data = cursor.getString(cursor.getColumnIndex("DATA"));
                String uuid = cursor.getString(cursor.getColumnIndex("UUID"));
                HashMap<String,LinkedHashMap<String,FormClass>> p = new Gson().fromJson( data, new TypeToken<HashMap<String, LinkedHashMap<String,FormClass>>>(){}.getType());
                //String patientName = patientDetailsMap.get("name").content;
                LinkedHashMap<String, FormClass> lk = new LinkedHashMap<String, FormClass>();
                lk.put("docId",new FormClass(uuid));
                p.put("docId",lk);
                userList.add(p);
                cursor.moveToNext();
            }
        }

        listAdapter.notifyDataSetChanged();

        progressDialog.hide();
    }


    public void getOnlineData(){
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
        if(offline) {
            getOfflineData();
        } else {
            getOnlineData();
        }

    }
}