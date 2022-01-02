package com.sharcodes.ortho;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;



import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddPatientActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    LinkedHashMap<String,LinkedHashMap<String,FormClass>> expandableListDetail;

    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;

    ProgressDialog progressDialog;
    String mainTitle;
    String mainListId;

    Integer maxUploads = 0;
    Integer doneCount = 0;

    ListModel listModel;

    StorageTask uploadTask;
    List<Task> myTasks = new ArrayList<>();
    String docId;
    String data;
    LinkedHashMap<String,LinkedHashMap<String,FormClass>> dummyDetail;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        listModel = new ListModel();
        dummyDetail = listModel.getData();


        expandableListTitle = new ArrayList<String>(dummyDetail.keySet());
        Bundle e = getIntent().getExtras();
        if(e != null && e.containsKey("key")) {
            data = getIntent().getStringExtra("key");
            expandableListDetail = new Gson().fromJson(data, new TypeToken<LinkedHashMap<String,LinkedHashMap<String,FormClass>>>() {
            }.getType());
        } else {
            expandableListDetail = listModel.getData();
        }

        for(String topKey : dummyDetail.keySet()) {
            for(String key : dummyDetail.get(topKey).keySet()) {
                dummyDetail.get(topKey).replace(key,expandableListDetail.get(topKey).get(key));
            }
        }

        if(e != null && e.containsKey("docId")) {
            docId = getIntent().getStringExtra("docId");
        }



       // expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Do firebase data push
                firebasepush();
            }
        });
    }



    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        YourObject obj = (YourObject) savedInstanceState.getSerializable(KEY);
        mainTitle =  savedInstanceState.getString("Group");
        mainListId =  savedInstanceState.getString("List");
        String data = savedInstanceState.getString("Data");
        expandableListDetail = new Gson().fromJson(data, new TypeToken<LinkedHashMap<String,LinkedHashMap<String,FormClass>>>() {
        }.getType());
//        if(obj != null)
//            mObject = obj;
    }

    void firebasepush()  {
        progressDialog.show();
        HashMap<String, String> uidName = new HashMap<>();
        MutableLiveData<Integer> uploadCount = new MutableLiveData<>();
        uploadCount.setValue(0);
        for(String key : expandableListDetail.keySet()) {
            for(String subKey :  expandableListDetail.get(key).keySet()) {
                for (String imageKeys : expandableListDetail.get(key).get(subKey).imagePath.keySet()) {
                    {
//                        uploadCount.setValue(uploadCount.getValue() + 1);
                    }
                }
            }
        }

        for(String key : expandableListDetail.keySet()) {
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

                                        doneCount +=1;
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
                if(integer == 0) {
                    Toast.makeText(AddPatientActivity.this,"Done", Toast.LENGTH_SHORT).show();

                    if (docId != null) {

                        db.collection("users").document(docId).set(expandableListDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("DB", "DocumentSnapshot added with ID");
                                progressDialog.hide();
                                Toast.makeText(AddPatientActivity.this, "Added", Toast.LENGTH_SHORT).show();
                                AddPatientActivity.this.finish();
                            }
                        });
                    }
                    else {
                        CollectionReference tref;
                        tref = db.collection("users");
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
//        Task finish = Tasks.whenAll((Collection) myTasks);
//        finish.addOnSuccessListener(new OnSuccessListener() {
//            @Override
//            public void onSuccess(Object o) {
//
//            }
//        });


    }
    void changeData(String id, String group, String content, HashMap<String,String> images) {
        expandableListDetail.get(group).get(id).content = content;
//        expandableListDetail.get(group).get(id).imagePath = images;

    }

    Uri outputFileUri;
    String imagePath;

    void addImage2(String title, String listId) {
        mainTitle = title;
        mainListId = listId;

        String directorypath = Environment.getExternalStorageDirectory()
                + "/Ortho";
        File mediaStorageDir = getApplicationContext().getExternalFilesDir(null);
        File directory = new File(directorypath);
        if (! directory.exists()){
            Boolean retVal = directory.mkdir();

            // If you require it to make the entire directory path including parents,
            // use directory.mkdirs(); here instead.
        }


        ImagePicker.with(this)
                .saveDir(directory)
                .start();
    }


    void addImage(String title, String listId) throws IOException {
        mainTitle = title;
        mainListId = listId;

        Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT, null);
        galleryintent.setType("image/*");
        galleryintent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);


        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyyMMdd_HHmmss");
        String dt = sdf.format(new Date());

        File imageFile = null;
        String directorypath = Environment.getExternalStorageDirectory()
                + "/Ortho";
        File mediaStorageDir = getApplicationContext().getExternalFilesDir(null);
        File directory = new File(directorypath);
        if (! directory.exists()){
            Boolean retVal = directory.mkdir();

            // If you require it to make the entire directory path including parents,
            // use directory.mkdirs(); here instead.
        }

        imagePath =  directorypath + "/Camera_" + dt + ".png";
            imageFile = new File(imagePath);
        Log.e("New Camera Image Path:-",
                Environment.getExternalStorageDirectory()
                        + "/FrameFace/" + "Camera_" + dt + ".png");

        if (!imageFile.exists())
            imageFile.createNewFile();



        outputFileUri = Uri.fromFile(imageFile);



        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        cameraIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INTENT, galleryintent);
        chooser.putExtra(Intent.EXTRA_TITLE, "Select from:");
        Intent[] intentArray = { cameraIntent };
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        startActivityForResult(chooser, 1);
    }
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 1) {
//            if(null != data) { // checking empty selection
//                if(null != data.getClipData()) { // checking multiple selection or not
//                    for(int i = 0; i < data.getClipData().getItemCount(); i++) {
//                        Uri uri = data.getClipData().getItemAt(i).getUri();
//                        File f = new File(uri.getPath());
//                        String path = f.getPath();
//                        LinkedHashMap<String, FormClass> k = expandableListDetail.get(mainTitle);
//
//                        k.get(mainListId).imagePath.put(getFileName(uri), uri.toString());
//                    }
//
//
//
//                } else {
//                    if(data.getData() != null) {
//                        Uri uri = data.getData();
//                        File f = new File(uri.getPath());
//                        String path = f.getPath();
//                        LinkedHashMap<String, FormClass> k = expandableListDetail.get(mainTitle);
//                        k.get(mainListId).imagePath.put(getFileName(uri), uri.toString());
//                    } else {
//                        Uri uri = outputFileUri;//getImageUri(getApplicationContext(), (Bitmap) data.getExtras().get("data"));
//
////                        Uri uri = bitmapToUriConverter((Bitmap) data.getExtras().get("data"));
//                        Log.i("TITLE", mainTitle);
//                        LinkedHashMap<String, FormClass> k = expandableListDetail.get(mainTitle);
//                        k.get(mainListId).imagePath.put("Click-"+getFileName(uri), uri.toString());
//                    }
//
//
//
//
////                    if(data.getExtras().get("data") != null) {
////
////                        Uri uri = outputFileUri;//getImageUri(getApplicationContext(), (Bitmap) data.getExtras().get("data"));
////
//////                        Uri uri = bitmapToUriConverter((Bitmap) data.getExtras().get("data"));
////                        LinkedHashMap<String, FormClass> k = expandableListDetail.get(mainTitle);
////                        k.get(mainListId).imagePath.put("Click-"+getFileName(uri), uri.toString());
////                    } else {
////                        Uri uri = data.getData();
////                        File f = new File(uri.getPath());
////                        String path = f.getPath();
////                        LinkedHashMap<String, FormClass> k = expandableListDetail.get(mainTitle);
////                        k.get(mainListId).imagePath.put(getFileName(uri), uri.toString());
////                    }
//                }
//
//
//            }
//            expandableListView.invalidateViews();
//
//        }
//    }

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
        if(resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();

            LinkedHashMap<String, FormClass> k = expandableListDetail.get(mainTitle);
            k.get(mainListId).imagePath.put(getFileName(uri), uri.toString());
            expandableListView.invalidateViews();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000,true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "Title", null);
        return Uri.parse(path);
    }


    public Uri bitmapToUriConverter(Bitmap mBitmap) {
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Calculate inSampleSize
            //options.inSampleSize = 1;//calculateInSampleSize(options, 100, 100);

            // Decode bitmap with inSampleSize set
            //options.inJustDecodeBounds = false;
            Bitmap newBitmap = mBitmap;// Bitmap.createScaledBitmap(mBitmap, 200, 200,true);
            File file = new File(this.getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpeg");
            FileOutputStream out = this.openFileOutput(file.getName(),
                    Context.MODE_PRIVATE);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
            out.flush();
            out.close();
            //get absolute path
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Your Error Message", e.getMessage());
        }
        return uri;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
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

        Set<String> keys =  expandableListDetail.get(group).get(id).imagePath.keySet();

        HashMap<String, String> images = new HashMap<>();
        HashMap<String, String> original = expandableListDetail.get(group).get(id).imagePath;
        for(String c : original.keySet()) {
            images.put(c, original.get(c));
        }


        expandableListDetail.get(group).get(id).imagePath.clear();
        for(String c: images.keySet()) {
            if (lista.contains(c)) {
                expandableListDetail.get(group).get(id).imagePath.put(c, images.get(c));
            }
        }

            }
}
