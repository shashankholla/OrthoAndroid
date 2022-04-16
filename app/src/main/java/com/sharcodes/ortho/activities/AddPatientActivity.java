package com.sharcodes.ortho.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.sharcodes.ortho.models.FormClass;
import com.sharcodes.ortho.models.ListModel;
import com.sharcodes.ortho.R;
import com.sharcodes.ortho.adapters.EditExpandableListAdapter;
import com.sharcodes.ortho.helper.DBHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class AddPatientActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    LinkedHashMap<String, LinkedHashMap<String, FormClass>> expandableListDetail;

    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;

    ProgressDialog progressDialog;
    String mainTitle;
    String mainListId;

    Integer maxUploads = 0;
    Integer doneCount = 0;

    ListModel listModel;

    Uri outputFileUri;
    String imagePath;

    String directorypath = Environment.getExternalStorageDirectory() + "/Ortho";


    List<Task> myTasks = new ArrayList<>();
    String docId;
    String data;
    LinkedHashMap<String, LinkedHashMap<String, FormClass>> dummyDetail;
    String unit;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        expandableListView = findViewById(R.id.expandableListView);
        listModel = new ListModel();
        dummyDetail = listModel.getData();
        unit = getIntent().getStringExtra("unit");

        expandableListTitle = new ArrayList<String>(dummyDetail.keySet());
        Bundle e = getIntent().getExtras();
        if (e != null && e.containsKey("key")) {
            data = getIntent().getStringExtra("key");
            expandableListDetail = new Gson().fromJson(data, new TypeToken<LinkedHashMap<String, LinkedHashMap<String, FormClass>>>() {
            }.getType());
        } else {
            expandableListDetail = listModel.getData();
        }



        for (String topKey : dummyDetail.keySet()) {
            for (String key : dummyDetail.get(topKey).keySet()) {
                dummyDetail.get(topKey).replace(key, expandableListDetail.get(topKey).get(key));
            }
        }

        if (e != null && e.containsKey("docId")) {
            docId = getIntent().getStringExtra("docId");
        }


        expandableListAdapter = new EditExpandableListAdapter(this, expandableListTitle, dummyDetail);
        expandableListView.setAdapter(expandableListAdapter);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
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

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Do firebase data push
                Bundle e = getIntent().getExtras();


                if (e != null && e.containsKey("online") && e.getBoolean("online")) {
                    firebasepush();
                } else {
                    try {
                        addToDatabase();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mainTitle = savedInstanceState.getString("Group");
        mainListId = savedInstanceState.getString("List");
        String data = savedInstanceState.getString("Data");
        expandableListDetail = new Gson().fromJson(data, new TypeToken<LinkedHashMap<String, LinkedHashMap<String, FormClass>>>() {
        }.getType());
    }

    void addToDatabase() throws IOException {
        progressDialog.show();
        DBHelper dbHelper = new DBHelper(this);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (String key : expandableListDetail.keySet()) {
            for (String subKey : expandableListDetail.get(key).keySet()) {
                for (String imageKeys : expandableListDetail.get(key).get(subKey).imagePath.keySet()) {
                    String uriString = expandableListDetail.get(key).get(subKey).imagePath.get(imageKeys);

                    Pattern p = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

                    if (!p.matcher(uriString).matches()) {

                        ContentValues values = new ContentValues();
                        String uuidImage = UUID.randomUUID().toString();
                        values.put("UUID", uuidImage);
                        values.put("DATA", uriString);

                        db.insert("IMAGES", null, values);

                        expandableListDetail.get(key).get(subKey).imagePath.put(imageKeys, uuidImage);
                    }
                }
            }
        }


        ContentValues values = new ContentValues();


        values.put("DATA", new Gson().toJson(expandableListDetail));


        if (docId != null) {
            db.update(unit, values, "UUID=?", new String[]{docId});
        } else {
            values.put("UUID", UUID.randomUUID().toString());
            db.insert(unit, null, values);

        }

        progressDialog.hide();
        Toast.makeText(AddPatientActivity.this, "Added", Toast.LENGTH_SHORT).show();
        AddPatientActivity.this.finish();


    }

    void firebasepush() {
        progressDialog.show();
        HashMap<String, String> uidName = new HashMap<>();
        MutableLiveData<Integer> uploadCount = new MutableLiveData<>();
        uploadCount.setValue(0);

        for (String key : expandableListDetail.keySet()) {
            for (String subKey : expandableListDetail.get(key).keySet()) {
                for (String imageKeys : expandableListDetail.get(key).get(subKey).imagePath.keySet()) {
                    String uriString = expandableListDetail.get(key).get(subKey).imagePath.get(imageKeys);

                    if (uriString.contains("https://") == false) {
                        String id = UUID.randomUUID().toString();
                        String path = "images/" + key + "/" + subKey + "/"
                                + id;
                        StorageReference ref
                                = storageReference
                                .child(path);


                        uidName.put(path, imageKeys);
                        uploadCount.setValue(uploadCount.getValue() + 1);
                        maxUploads = uploadCount.getValue();
                        UploadTask t = ref.putFile(Uri.parse(uriString));
                        progressDialog.setMessage(doneCount + "/" + maxUploads);
                        t.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                String fullPath = taskSnapshot.getMetadata().getPath();

                                String mKey = fullPath.split("/")[1];
                                String mSubKey = fullPath.split("/")[2];
                                String mPath = fullPath;

                                Task<Uri> t = ref.getDownloadUrl();
                                myTasks.add(t);
                                t.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        String image = uidName.get(mPath);
                                        expandableListDetail.get(mKey).get(mSubKey).imagePath.put(image, uri.toString());
                                        uploadCount.setValue(uploadCount.getValue() - 1);

                                        doneCount += 1;
                                        progressDialog.setMessage(doneCount + "/" + maxUploads);
                                    }
                                });
                            }
                        });

                        myTasks.add(t);


                    }
                }

            }
        }
        uploadCount.observe(AddPatientActivity.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == 0) {
                    Toast.makeText(AddPatientActivity.this, "Done", Toast.LENGTH_SHORT).show();

                    if (docId != null) {
                        FirebaseAuth mAuth;

                        mAuth = FirebaseAuth.getInstance();

                        String user = mAuth.getCurrentUser().getUid();

                        db.collection(user + "-" + unit).document(docId).set(expandableListDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("DB", "DocumentSnapshot added with ID");
                                progressDialog.hide();
                                Toast.makeText(AddPatientActivity.this, "Added", Toast.LENGTH_SHORT).show();
                                AddPatientActivity.this.finish();
                            }
                        });
                    } else {
                        CollectionReference tref;
                        FirebaseAuth mAuth;

                        mAuth = FirebaseAuth.getInstance();

                        String user = mAuth.getCurrentUser().getUid();


                        tref = db.collection(user + "-" + unit);

                        tref.add(expandableListDetail).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("DB", "DocumentSnapshot added with ID: " + documentReference.getId());
                                progressDialog.hide();
                                Toast.makeText(AddPatientActivity.this, "Added", Toast.LENGTH_SHORT).show();
                                AddPatientActivity.this.finish();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.hide();
                                        Toast.makeText(AddPatientActivity.this, "Could not add. Retry", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }


                }
            }
        });
    }

    public void changeData(String id, String group, String content, HashMap<String, String> images) {
        expandableListDetail.get(group).get(id).content = content;
    }

    public void addLink(String title, String listId, String link) {
        mainTitle = title;
        mainListId = listId;
        LinkedHashMap<String, FormClass> k = expandableListDetail.get(mainTitle);
        k.get(mainListId).links.put(link, "link");
        expandableListView.invalidateViews();
    }

    public void addImage2(String title, String listId) {
        mainTitle = title;
        mainListId = listId;

        String directorypath = Environment.getExternalStorageDirectory()
                + "/Ortho";
        File mediaStorageDir = getApplicationContext().getExternalFilesDir(null);
        File directory = new File(directorypath);
        if (!directory.exists()) {
            Boolean retVal = directory.mkdir();

            // If you require it to make the entire directory path including parents,
            // use directory.mkdirs(); here instead.
        }




        ImagePicker.Companion.with(this)
                .allowMultiple(true)
                .saveDir(directory)
                .start();
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("Group", mainTitle);
        savedInstanceState.putString("List", mainListId);
        savedInstanceState.putString("Data", expandableListDetail.toString());
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            List<File> ll = ImagePicker.Companion.getFiles(data);

            LinkedHashMap<String, FormClass> k = expandableListDetail.get(mainTitle);
            for(File f : ll) {
                Uri urix = Uri.fromFile(f);
                k.get(mainListId).imagePath.put(getFileName(urix), urix.toString());
            }
            if(uri != null) {
                k.get(mainListId).imagePath.put(getFileName(uri), uri.toString());
            }



//            k.get(mainListId).imagePath.put(getFileName(uri), uri.toString());
            expandableListView.invalidateViews();
        }
    }


    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void changeImages(String id, String group, List<String> lista) {


        HashMap<String, String> images = new HashMap<>();
        HashMap<String, String> original = expandableListDetail.get(group).get(id).imagePath;
        for (String c : original.keySet()) {
            images.put(c, original.get(c));
        }


        expandableListDetail.get(group).get(id).imagePath.clear();
        for (String c : images.keySet()) {
            if (lista.contains(c)) {
                expandableListDetail.get(group).get(id).imagePath.put(c, images.get(c));
            }
        }

    }


    public void changeLinks(String id, String group, List<String> lista) {


        HashMap<String, String> images = new HashMap<>();
        HashMap<String, String> original = expandableListDetail.get(group).get(id).links;
        for (String c : original.keySet()) {
            images.put(c, original.get(c));
        }


        expandableListDetail.get(group).get(id).links.clear();
        for (String c : images.keySet()) {
            if (lista.contains(c)) {
                expandableListDetail.get(group).get(id).links.put(c, images.get(c));
            }
        }

    }
}
