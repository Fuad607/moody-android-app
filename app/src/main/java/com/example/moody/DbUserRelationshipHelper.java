package com.example.moody;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DbUserRelationshipHelper extends SQLiteOpenHelper {

    public DbUserRelationshipHelper(@Nullable Context context) {
        super(context, "UserRelationship.db", null, 1);
     }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table Users( id INTEGER primary key autoincrement,user_id TEXT,  nickname TEXT, contacted_user_id TEXT, nickname TEXT,type TEXT   )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists Users");

    }

    public Boolean insertUserData(  String user_id,  String nickname, String email, String password) {
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
        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("Select * from Users where user_id=?", new String[]{user_id});
        return cursor;
    }
}
