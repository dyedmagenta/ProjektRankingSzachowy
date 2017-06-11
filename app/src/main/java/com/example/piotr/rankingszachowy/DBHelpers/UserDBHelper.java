package com.example.piotr.rankingszachowy.DBHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Marcin Omelan on 29.05.2017.
 */

public class UserDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Chess.db";
    private static final String TABLE_NAME = "users_table";
    private static final String ID = "ID";
    private static final String USERNAME = "USERNAME";
    private static final String RANK = "RANK";
    private static final String LAST_PLAYED = "LAST_PLAYED";
    private static final String PLAYING_SINCE = "PLAYING_SINCE";
    private static final String AGE = "AGE";
    private static final String PICURI = "PICURI";


    public UserDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 3);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_String = "CREATE TABLE " + TABLE_NAME + "(" + ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + USERNAME + " TEXT, " +
                RANK + " TEXT, " + LAST_PLAYED +" TEXT, "+ PLAYING_SINCE +" TEXT, "+
                AGE +" INTEGER, " + PICURI + " INTEGER);";

        db.execSQL(SQL_String);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST "+TABLE_NAME);
        onCreate(db);
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public Cursor getSpecificData(String s) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + USERNAME +" LIKE '"+ s+"%'", null);
    }

    public boolean insertData(String username, String rank, String lastPlayed, String playingSince,
                              String age, String picuri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USERNAME, username);
        contentValues.put(RANK, rank);
        contentValues.put(LAST_PLAYED, lastPlayed);
        contentValues.put(PLAYING_SINCE, playingSince);
        contentValues.put(AGE, age);
        contentValues.put(PICURI, picuri);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }
}
