package com.sharcodes.ortho.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sharcodes.ortho.activities.NoteEditor;
import com.sharcodes.ortho.R;
import com.sharcodes.ortho.activities.MainActivity;
import com.sharcodes.ortho.adapters.CustomNotesAdapter;
import com.sharcodes.ortho.helper.DBHelper;
import com.sharcodes.ortho.models.NoteClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PatientListFragmentOffline#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final List<NoteClass> noteList = new ArrayList<>();

    SearchView searchView;
    String unit;
    ProgressDialog progressDialog;
    DBHelper dbHelper;
    private CustomNotesAdapter listAdapter;
    private RecyclerView recycler;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotesFragment() {
        // Required empty public constructor
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
        listAdapter = new CustomNotesAdapter(noteList, getContext(), new CustomNotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NoteClass item) {
                Intent intent = new Intent(getContext(), NoteEditor.class);
                intent.putExtra("title", item.title);
                intent.putExtra("unit", unit);
                intent.putExtra("date", item.date);
                intent.putExtra("content", item.content);
                intent.putExtra("uuid", item.uuid);
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
        Intent intent = new Intent(getContext(), NoteEditor.class);
        intent.putExtra("unit", unit);

        startActivity(intent);
    }

    public void getOfflineData() {
        progressDialog.show();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String tableName = "NOTES_" + unit;
        Cursor cursor = db.query(tableName, null, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String content = cursor.getString(cursor.getColumnIndex("CONTENT"));
                String uuid = cursor.getString(cursor.getColumnIndex("UUID"));
                String title = cursor.getString(cursor.getColumnIndex("TITLE"));
                String date = cursor.getString(cursor.getColumnIndex("DATE"));

                NoteClass note = new NoteClass(date, title, content, uuid);

                noteList.add(note);
                cursor.moveToNext();
            }
        }
        Collections.reverse(noteList);
        String queryHint = "Search for notes (Total: " + noteList.size() + ")";
        searchView.setQueryHint(queryHint);

        listAdapter.notifyDataSetChanged();

        progressDialog.hide();
    }


    @Override
    public void onResume() {
        super.onResume();
        noteList.clear();
        getOfflineData();

    }
}