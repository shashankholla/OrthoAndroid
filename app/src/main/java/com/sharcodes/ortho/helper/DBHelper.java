package com.sharcodes.ortho.helper;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";
    public static final String[] MY_TABLES = new String[]{"unit1", "unit2", "unit3", "unit4", "unit5"};




    public static final String IMAGE_TABLE = "IMAGES";

    private static final String SQL_CREATE_ENTRIES_DATA =
            "CREATE TABLE TABLE_NAME ( UUID TEXT PRIMARY KEY, DATA TEXT )";

    private static final String SQL_CREATE_ENTRIES_NOTES =
            "CREATE TABLE NOTES_TABLE_NAME ( UUID TEXT PRIMARY KEY, TITLE TEXT, CONTENT TEXT, DATE TEXT )";

    private static final String SQL_CREATE_ENTRIES_PG =
            "CREATE TABLE PG_TABLE_NAME ( UUID TEXT PRIMARY KEY, TITLE TEXT, CONTENT TEXT, DATE TEXT )";


    private static final String SQL_DELETE_ENTRIES_DATA =
            "DROP TABLE IF EXISTS TABLE_NAME";

    private static final String SQL_DELETE_ENTRIES_NOTES =
            "DROP TABLE IF EXISTS NOTES_TABLE_NAME";

    private static final String SQL_DELETE_ENTRIES_PG =
            "DROP TABLE IF EXISTS PG_TABLE_NAME";

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


        for (String tableName : MY_TABLES) {
            sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES_DATA.replace("TABLE_NAME", tableName));
            sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES_NOTES.replace("TABLE_NAME", tableName));
            sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES_PG.replace("TABLE_NAME", tableName));
        }



    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES_IMAGE);
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES_NOTES);

        for (String tableName : MY_TABLES) {
            sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES_DATA.replace("TABLE_NAME", tableName));
            sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES_NOTES.replace("TABLE_NAME", tableName));
            sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES_PG.replace("TABLE_NAME", tableName));
        }



        onCreate(sqLiteDatabase);

    }
}
