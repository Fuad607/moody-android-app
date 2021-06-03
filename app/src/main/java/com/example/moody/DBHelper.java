package com.example.moody;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(@Nullable Context context) {
        super(context, "Userdata.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table Users( id INTEGER primary key autoincrement,user_id TEXT,  nickname TEXT, email TEXT, password TEXT,user_unique_code TEXT   )");
        DB.execSQL("create Table UserRelationship( id INTEGER primary key autoincrement,user_id TEXT,  nickname TEXT, contacted_user_id TEXT,  type TEXT)");
        DB.execSQL("create Table Survey( id INTEGER primary key autoincrement,user_id TEXT,  mood_level INTEGER, relaxed_level INTEGER,  timestamp INTEGER,  deleted INTEGER,  sync INTEGER)");
        DB.execSQL("create Table UserMeeting( id INTEGER primary key autoincrement,survey_id TEXT,  contacted_user_id INTEGER, meeting_type TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists Users");
        DB.execSQL("drop Table if exists UserRelationship");
        DB.execSQL("drop Table if exists Survey");
        DB.execSQL("drop Table if exists UserMeeting");
        onCreate(DB);
    }

    public Boolean insertUserData(String user_id, String nickname, String email, String password) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("user_id", user_id);
        contentValues.put("nickname", nickname);
        contentValues.put("email", email);
        contentValues.put("password", password);
        contentValues.put("user_unique_code", "");
        long result = DB.insert("Users", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean updateUserData(String user_id, String nickname, String email, String user_unique_code) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nickname", nickname);
        contentValues.put("email", email);
        contentValues.put("user_unique_code", user_unique_code);
        Cursor cursor = DB.rawQuery("Select * from Users where user_id=?", new String[]{user_id});
        if (cursor.getCount() > 0) {
            long result = DB.update("Users", contentValues, "id=?", new String[]{user_id});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public Boolean deleteData(String user_id) {
        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("Select * from Users where user_id=?", new String[]{user_id});
        if (cursor.getCount() > 0) {
            long result = DB.delete("Users", "user_id=?", new String[]{user_id});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;

        }
    }

    public Cursor getData(String user_id) {
        SQLiteDatabase DB = this.getReadableDatabase();

        Cursor cursor = DB.rawQuery("Select * from Users where user_id=?", new String[]{user_id});
        return cursor;
    }

    //UserRelationship data
    public Long insertUserRelationshipData(String user_id, String nickname, String contacted_user_id, String type) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("user_id", user_id);
        contentValues.put("nickname", nickname);
        contentValues.put("contacted_user_id", contacted_user_id);
        contentValues.put("type", type);
        long result = DB.insert("UserRelationship", null, contentValues);

        return result;
    }

    public Cursor getUserRelationshipData(String user_id) {
        SQLiteDatabase DB = this.getReadableDatabase();

        Cursor cursor = DB.rawQuery("Select * from UserRelationship where user_id=" + user_id, null);

        return cursor;
    }

    public void deleteAlUserRelationship() {
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.execSQL("delete from  UserRelationship");
    }

    //Survey data
    public Long inserSurvey(String user_id, Integer mood_level, Integer relaxed_level) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        contentValues.put("user_id", user_id);
        contentValues.put("mood_level", mood_level);
        contentValues.put("relaxed_level", relaxed_level);
        contentValues.put("timestamp", ts);
        contentValues.put("deleted", 0);
        contentValues.put("sync", 0);
        long result = DB.insert("Survey", null, contentValues);

        return result;
    }

    public Cursor geSurvey(String user_id) {
        SQLiteDatabase DB = this.getReadableDatabase();

        Cursor cursor = DB.rawQuery("Select * from Survey where user_id=" + user_id, null);

        return cursor;
    }

    public void deleteAlSurvey() {
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.execSQL("delete from  Survey");
    }
}
