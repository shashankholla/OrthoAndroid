package com.sharcodes.ortho.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sharcodes.ortho.R;
import com.sharcodes.ortho.helper.DBHelper;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class NoteEditor extends AppCompatActivity {

    final Calendar myCalendar= Calendar.getInstance();
    EditText dateET;

    EditText titleET;
    EditText contentET;

    String unit;
    FloatingActionButton save;
    FloatingActionButton delete;
    String uuid;
    SQLiteDatabase db;
    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notesadd);

        unit = getIntent().getStringExtra("unit");

        dateET = findViewById(R.id.date);
        titleET = findViewById(R.id.title);
        contentET = findViewById(R.id.content);


        DBHelper dbHelper = new DBHelper(this);

        db = dbHelper.getWritableDatabase();


        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        String date = getIntent().getStringExtra("date");
        if(date != null && !date.isEmpty()) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("E M d H:m:s z y");
                Date dateF = null;
                dateF = format.parse(date);
                myCalendar.setTime(dateF);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        uuid = getIntent().getStringExtra("uuid");


        if(title != null && !title.isEmpty()) {
            titleET.setText(title);
        }

        if(content != null && !content.isEmpty()) {
            contentET.setText(content);
        }


        DatePickerDialog.OnDateSetListener datePicker =new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };
        dateET.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(MotionEvent.ACTION_UP == motionEvent.getAction())
                new DatePickerDialog(NoteEditor.this,datePicker,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                return true;
            }
        });
        updateLabel();

        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             save();
            }
        });

        delete = findViewById(R.id.delete);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new AlertDialog.Builder(NoteEditor.this)
                        .setTitle("Delete?")
                        .setMessage("Do you want to delete your note?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(uuid != null && !uuid.isEmpty()) {
                                    String selection = "UUID" + " = ?";
                                    String[] selectionArgs = {uuid};
                                    db.delete("NOTES_"+unit, selection, selectionArgs);
                                }
                                finish();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                                finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();




            }
        });



    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateLabel(){
        String myFormat="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        dateET.setText(dateFormat.format(myCalendar.getTime()));
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        new AlertDialog.Builder(this)
                .setTitle("Save?")
                .setMessage("Do you want to save your changes?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        save();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    public void save() {
        ContentValues values = new ContentValues();
        values.put("TITLE", titleET.getText().toString());
        values.put("CONTENT", contentET.getText().toString());
        values.put("DATE", myCalendar.getTime().toString());
        if(uuid == null || uuid.isEmpty()) {
            uuid = UUID.randomUUID().toString();
            values.put("UUID", uuid);
            db.insert("NOTES_"+unit, null, values);
        } else {
            values.put("UUID", uuid);
            db.update("NOTES_"+unit, values, "UUID=?", new String[]{uuid});

        }
        finish();
    }
}