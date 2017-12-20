package com.enhabyto.rajpetroleum;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

/**
 * Created by this on 19-Dec-17.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "DriverTrip";
    private static final int DATABASE_VERSION = 1;
    private HashMap hp;
    public String table_name = "driver";
    SharedPreferences shared;

    String pump_name, petrol_filled, address, money_paid, state, token_number, gps_location;

        public DBHelper(Context context) {
            super(context, DATABASE_NAME , null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS " + table_name +
                            "(id integer primary key, user_name text, phone_number text)"
            );
        }

        public void insertData(){
            SQLiteDatabase db1 = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("user_name", "Abhay");
            contentValues.put("phone_number", "9971634265");
            db1.insert(table_name, null, contentValues);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + table_name);

        }

        public Cursor getuser() {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from " + table_name + " ",
                    null);
            return res;
        }
    }