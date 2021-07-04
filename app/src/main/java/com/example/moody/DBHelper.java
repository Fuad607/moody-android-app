package com.example.moody;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(@Nullable Context context) {
        super(context, "Userdata.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table Users( id INTEGER primary key autoincrement,user_id TEXT,  nickname TEXT, email TEXT, password TEXT,user_unique_code TEXT   )");
        DB.execSQL("create Table UserRelationship( id INTEGER primary key autoincrement,user_id TEXT,  nickname TEXT, contacted_user_id TEXT,  type TEXT)");
        DB.execSQL("create Table Survey( id INTEGER primary key autoincrement,user_id TEXT,  mood_level INTEGER, relaxed_level INTEGER,  timestamp INTEGER,  deleted INTEGER,  sync INTEGER)");
        DB.execSQL("create Table UserMeeting( id INTEGER primary key autoincrement,survey_id TEXT,  contacted_user_id TEXT, meeting_type TEXT)");
        DB.execSQL("create Table UserSpecialSituation( id INTEGER primary key autoincrement,survey_id TEXT,  special_situation TEXT, special_situation_type INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists Users");
        DB.execSQL("drop Table if exists UserRelationship");
        DB.execSQL("drop Table if exists Survey");
        DB.execSQL("drop Table if exists UserMeeting");
        DB.execSQL("drop Table if exists UserSpecialSituation");
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
    public Long insertSurvey(String user_id, Integer mood_level, Integer relaxed_level, Integer sync) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();

        contentValues.put("user_id", user_id);
        contentValues.put("mood_level", mood_level);
        contentValues.put("relaxed_level", relaxed_level);
        contentValues.put("timestamp", ts);
        contentValues.put("deleted", 0);
        contentValues.put("sync", sync);
        long result = DB.insert("Survey", null, contentValues);

        return result;
    }

    public Cursor getSurvey() {
        SQLiteDatabase DB = this.getReadableDatabase();

        Cursor cursor = DB.rawQuery("Select * from Survey where sync='0'", null);

        return cursor;
    }

    public void deleteAllSurvey() {
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.execSQL("delete from  Survey");
    }

    public Boolean setSyncSurvey(String survey_id) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("sync", 1);
        Cursor cursor = DB.rawQuery("Select * from Survey where id=?", new String[]{survey_id});
        if (cursor.getCount() > 0) {
            long result = DB.update("Survey", contentValues, "id=?", new String[]{survey_id});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    //UserMeeting data
    public Long insertUserMeeting(String survey_id, String contacted_user_id, String meeting_type) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("survey_id", survey_id);
        contentValues.put("contacted_user_id", contacted_user_id);
        contentValues.put("meeting_type", meeting_type);
        long result = DB.insert("UserMeeting", null, contentValues);

        return result;
    }

    public Cursor getUserMeeting(String survey_id) {
        SQLiteDatabase DB = this.getReadableDatabase();

        Cursor cursor = DB.rawQuery("Select * from UserMeeting where survey_id=" + survey_id, null);

        return cursor;
    }

    public void deleteAllUserMeeting() {
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.execSQL("delete from  UserMeeting");
    }

    //UserMeeting data
    public Long insertUserSpecialSituation(String survey_id, String special_situation, String special_situation_type) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("survey_id", survey_id);
        contentValues.put("special_situation", special_situation);
        contentValues.put("special_situation_type", special_situation_type);
        long result = DB.insert("UserSpecialSituation", null, contentValues);

        return result;
    }

    public Cursor getUserSpecialSituation(String survey_id) {
        SQLiteDatabase DB = this.getReadableDatabase();

        Cursor cursor = DB.rawQuery("Select * from UserSpecialSituation where survey_id=" + survey_id, null);

        return cursor;
    }

    public void deleteAllUserSpecialSituation() {
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.execSQL("delete from  UserSpecialSituation");
    }
}
