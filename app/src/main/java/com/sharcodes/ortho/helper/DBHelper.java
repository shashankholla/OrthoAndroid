package com.sharcodes.ortho.helper;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper  extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";
    public static final String DATA_TABLE = "PATIENTS";
    public static final String IMAGE_TABLE = "IMAGES";

    private static final String SQL_CREATE_ENTRIES_DATA =
            "CREATE TABLE " + DATA_TABLE + " ( UUID TEXT PRIMARY KEY, DATA TEXT )";

    private static final String SQL_DELETE_ENTRIES_DATA =
            "DROP TABLE IF EXISTS " + DATA_TABLE;

    private static final String SQL_CREATE_ENTRIES_IMAGE =
            "CREATE TABLE " + IMAGE_TABLE + " ( UUID TEXT PRIMARY KEY, DATA BLOB )";

    private static final String SQL_DELETE_ENTRIES_IMAGE =
            "DROP TABLE IF EXISTS " + IMAGE_TABLE;


    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES_IMAGE);
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES_DATA);
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES_IMAGE);
        onCreate(sqLiteDatabase);

    }
}
