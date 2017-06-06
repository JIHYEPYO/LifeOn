package com.example.ncs.lifeon.ECT;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.ncs.lifeon.ECT.Const.TABLE_NAME_PHONE;

/**
 * Created by PYOJIHYE on 2017-06-07.
 */

public class DatabasePhoneController extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "project.phone";
    private static final int DATABASE_VERSION = 2;

    public DatabasePhoneController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PHONE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, personName TEXT, personPhone TEXT); ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PHONE);
        onCreate(db);
    }
}
