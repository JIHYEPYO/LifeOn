package com.example.ncs.lifeon.ECT;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.ncs.lifeon.ECT.Const.TABLE_NAME_STEP;

/**
 * Created by PYOJIHYE on 2017-06-06.
 */

public class DatabaseStepController extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "project.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseStepController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_STEP + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, curDate TEXT, step TEXT, m TEXT, cal TEXT); ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_STEP);
        onCreate(db);
    }
}