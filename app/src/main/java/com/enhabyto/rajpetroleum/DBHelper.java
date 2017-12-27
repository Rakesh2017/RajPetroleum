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

    String pump_name, petrol_filled, address, money_paid, state, token_number, gps_location , date, time, place_name
            ,description, amount_paid, amount_paid_for, filling_name, quantity, oil_loaded, next_stoppage, location
            , state_name, oil_unloaded, oil_left, failure_name, resource_name1, resource_price1, resource_quantity1, resource_name2, resource_price2, resource_quantity2
            , resource_name3, resource_price3, resource_quantity3, resource_name4, resource_price4, resource_quantity4, resource_name5, resource_price5, resource_quantity5
            , bill_paid1, bill_paid2, bill_paid3, pump_fuel_rate, pump_fuel_rate_date;


    SharedPreferences sharedPreferences;

        public DBHelper(Context context) {
            super(context, DATABASE_NAME , null, DATABASE_VERSION);
            sharedPreferences = context.getSharedPreferences("excel_data", Context.MODE_PRIVATE);
        }




        @Override
        public void onCreate(SQLiteDatabase db) {
          /*  db.execSQL(
                 //   "CREATE TABLE IF NOT EXISTS " + table_name +
                       //     "(id integer primary key, user_name text, phone_number text)"
            ); */
        }

        public void insertPetrolData(){
            pump_name = sharedPreferences.getString("pump_name", "");
            petrol_filled = sharedPreferences.getString("petrol_filled", "");
            address = sharedPreferences.getString("address", "");
            date = sharedPreferences.getString("date", "");
            time = sharedPreferences.getString("time", "");
            gps_location = sharedPreferences.getString("gps_location", "");
            money_paid = sharedPreferences.getString("money_paid", "");
            state = sharedPreferences.getString("state", "");
            token_number = sharedPreferences.getString("token_number", "");
            pump_fuel_rate = sharedPreferences.getString("pump_fuel_rate", "");
            pump_fuel_rate_date = sharedPreferences.getString("pump_fuel_rate_date", "");


            SQLiteDatabase db1 = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("pump_name", pump_name);
            contentValues.put("petrol_filled", petrol_filled);
            contentValues.put("address", address);
            contentValues.put("date", date);
            contentValues.put("time", time);
            contentValues.put("gps_location", gps_location);
            contentValues.put("token_number", token_number);
            contentValues.put("state", state);
            contentValues.put("money_paid", money_paid);
            contentValues.put("pump_fuel_rate", pump_fuel_rate);
            contentValues.put("pump_fuel_rate_date", pump_fuel_rate_date);

            db1.insert(table_name, null, contentValues);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         //   SQLiteDatabase dbs = this.getReadableDatabase();
         //   dbs.execSQL("DROP TABLE IF EXISTS " + table_name);

        }

    public void dropPetrolTable() {
        SQLiteDatabase dbs = this.getReadableDatabase();
        dbs.execSQL("DROP TABLE IF EXISTS " + table_name);
        onCreatePetrolTable();

    }

    public void onCreatePetrolTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + table_name +
                        "(id integer primary key, pump_name text, petrol_filled text, address text, date text, time text" +
                        ", gps_location text, token_number text, money_paid text, state text, pump_fuel_rate text" +
                        ", pump_fuel_rate_date text)"
        );
    }




//    Stoppage

    public void insertStopData(){
        place_name = sharedPreferences.getString("place_name", "");
        description = sharedPreferences.getString("description", "");
        amount_paid = sharedPreferences.getString("money_paid", "");
        amount_paid_for = sharedPreferences.getString("money_paid_for", "");
        date = sharedPreferences.getString("date", "");
        time = sharedPreferences.getString("time", "");
        gps_location = sharedPreferences.getString("gps_location", "");


        SQLiteDatabase db1 = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("place_name", place_name);
        contentValues.put("description", description);
        contentValues.put("amount_paid", amount_paid);
        contentValues.put("amount_paid_for", amount_paid_for);
        contentValues.put("date", date);
        contentValues.put("time", time);
        contentValues.put("gps_location", gps_location);


        db1.insert(table_name, null, contentValues);
    }


    public void dropStopTable() {
        SQLiteDatabase dbs = this.getReadableDatabase();
        dbs.execSQL("DROP TABLE IF EXISTS " + table_name);
        onCreateStopTable();

    }

    public void onCreateStopTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + table_name +
                        "(id integer primary key, place_name text, description text, amount_paid text, amount_paid_for text" +
                        ", date text" +
                        ", time text, gps_location text)"
        );
    }




//    Other filling


    public void insertOtherData(){
        filling_name = sharedPreferences.getString("filling_name", "");
        quantity = sharedPreferences.getString("quantity", "");
        amount_paid = sharedPreferences.getString("money_paid", "");
        description = sharedPreferences.getString("description", "");
        date = sharedPreferences.getString("date", "");
        time = sharedPreferences.getString("time", "");
        gps_location = sharedPreferences.getString("gps_location", "");


        SQLiteDatabase db1 = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("filling_name", filling_name);
        contentValues.put("quantity", quantity);
        contentValues.put("money_paid", amount_paid);
        contentValues.put("description", description);
        contentValues.put("date", date);
        contentValues.put("time", time);
        contentValues.put("gps_location", gps_location);


        db1.insert(table_name, null, contentValues);
    }


    public void dropOtherTable() {
        SQLiteDatabase dbs = this.getReadableDatabase();
        dbs.execSQL("DROP TABLE IF EXISTS " + table_name);
        onCreateOtherTable();

    }

    public void onCreateOtherTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + table_name +
                        "(id integer primary key, filling_name text, quantity text, money_paid text, description text" +
                        ", date text" +
                        ", time text, gps_location text)"
        );
    }






//    load



    public void insertLoadData(){
        oil_loaded = sharedPreferences.getString("oil_loaded", "");
        location = sharedPreferences.getString("location", "");
        state_name = sharedPreferences.getString("state_name", "");
        next_stoppage = sharedPreferences.getString("next_stoppage", "");
        date = sharedPreferences.getString("date", "");
        time = sharedPreferences.getString("time", "");
        gps_location = sharedPreferences.getString("gps_location", "");


        SQLiteDatabase db1 = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("oil_loaded", oil_loaded);
        contentValues.put("location", location);
        contentValues.put("state_name", state_name);
        contentValues.put("next_stoppage", next_stoppage);
        contentValues.put("date", date);
        contentValues.put("time", time);
        contentValues.put("gps_location", gps_location);


        db1.insert(table_name, null, contentValues);
    }


    public void dropLoadTable() {
        SQLiteDatabase dbs = this.getReadableDatabase();
        dbs.execSQL("DROP TABLE IF EXISTS " + table_name);
        onCreateLoadTable();

    }

    public void onCreateLoadTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + table_name +
                        "(id integer primary key, oil_loaded text, location text, state_name text, next_stoppage text" +
                        ", date text" +
                        ", time text, gps_location text)"
        );
    }




    //    uload



    public void insertunLoadData(){
        oil_unloaded = sharedPreferences.getString("oil_unloaded", "");
        pump_name = sharedPreferences.getString("pump_name", "");
        location = sharedPreferences.getString("location", "");
        state_name = sharedPreferences.getString("state_name", "");
        oil_left = sharedPreferences.getString("oil_left", "");
        next_stoppage = sharedPreferences.getString("next_stoppage", "");
        date = sharedPreferences.getString("date", "");
        time = sharedPreferences.getString("time", "");
        gps_location = sharedPreferences.getString("gps_location", "");


        SQLiteDatabase db1 = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("oil_unloaded", oil_unloaded);
        contentValues.put("pump_name", pump_name);
        contentValues.put("location", location);
        contentValues.put("state_name", state_name);
        contentValues.put("next_stoppage", next_stoppage);
        contentValues.put("oil_left", oil_left);
        contentValues.put("date", date);
        contentValues.put("time", time);
        contentValues.put("gps_location", gps_location);


        db1.insert(table_name, null, contentValues);
    }


    public void dropunLoadTable() {
        SQLiteDatabase dbs = this.getReadableDatabase();
        dbs.execSQL("DROP TABLE IF EXISTS " + table_name);
        onCreateunLoadTable();

    }

    public void onCreateunLoadTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + table_name +
                        "(id integer primary key, oil_unloaded text, pump_name text, location text, state_name text, next_stoppage text, oil_left text" +
                        ", date text" +
                        ", time text, gps_location text)"
        );
    }




    //   break



    public void insertBreakData(){
        failure_name = sharedPreferences.getString("failure_name", "");
        address = sharedPreferences.getString("address", "");
        state_name = sharedPreferences.getString("state_name", "");

        resource_name1 = sharedPreferences.getString("resource_name1", "");
        resource_price1 = sharedPreferences.getString("resource_price1", "");
        resource_quantity1 = sharedPreferences.getString("resource_quantity1", "");

        resource_name2 = sharedPreferences.getString("resource_name2", "");
        resource_price2 = sharedPreferences.getString("resource_price2", "");
        resource_quantity2 = sharedPreferences.getString("resource_quantity2", "");

        resource_name3 = sharedPreferences.getString("resource_name3", "");
        resource_price3 = sharedPreferences.getString("resource_price3", "");
        resource_quantity3 = sharedPreferences.getString("resource_quantity3", "");

        resource_name4 = sharedPreferences.getString("resource_name4", "");
        resource_price4 = sharedPreferences.getString("resource_price4", "");
        resource_quantity4 = sharedPreferences.getString("resource_quantity4", "");

        resource_name5 = sharedPreferences.getString("resource_name5", "");
        resource_price5 = sharedPreferences.getString("resource_price5", "");
        resource_quantity5 = sharedPreferences.getString("resource_quantity5", "");

        bill_paid1 = sharedPreferences.getString("bill_paid1", "");
        bill_paid2 = sharedPreferences.getString("bill_paid2", "");
        bill_paid3 = sharedPreferences.getString("bill_paid3", "");

        date = sharedPreferences.getString("date", "");
        time = sharedPreferences.getString("time", "");
        gps_location = sharedPreferences.getString("gps_location", "");


        SQLiteDatabase db1 = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("failure_name", failure_name);
        contentValues.put("address", address);
        contentValues.put("state_name", state_name);

        contentValues.put("resource_name1", resource_name1);
        contentValues.put("resource_price1", resource_price1);
        contentValues.put("resource_quantity1", resource_quantity1);

        contentValues.put("resource_name2", resource_name2);
        contentValues.put("resource_price2", resource_price2);
        contentValues.put("resource_quantity2", resource_quantity2);

        contentValues.put("resource_name3", resource_name3);
        contentValues.put("resource_price3", resource_price3);
        contentValues.put("resource_quantity3", resource_quantity3);

        contentValues.put("resource_name4", resource_name4);
        contentValues.put("resource_price4", resource_price4);
        contentValues.put("resource_quantity4", resource_quantity4);

        contentValues.put("resource_name5", resource_name5);
        contentValues.put("resource_price5", resource_price5);
        contentValues.put("resource_quantity5", resource_quantity5);

        contentValues.put("bill_paid1", bill_paid1);
        contentValues.put("bill_paid2", bill_paid2);
        contentValues.put("bill_paid3", bill_paid3);


        contentValues.put("date", date);
        contentValues.put("time", time);
        contentValues.put("gps_location", gps_location);


        db1.insert(table_name, null, contentValues);
    }


    public void dropBreakTable() {
        SQLiteDatabase dbs = this.getReadableDatabase();
        dbs.execSQL("DROP TABLE IF EXISTS " + table_name);
        onCreateBreakTable();

    }

    public void onCreateBreakTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + table_name +
                        "(id integer primary key, failure_name text, address text, state_name text, resource_name1 text, resource_price1 text, resource_quantity1 text" +
                        ", resource_name2 text, resource_price2 text, resource_quantity2 text, resource_name3 text, resource_price3 text, resource_quantity3 text" +
                        ", resource_name4 text, resource_price4 text, resource_quantity4 text, resource_name5 text, resource_price5 text, resource_quantity5 text" +
                        ", bill_paid1 text, bill_paid2 text, bill_paid3 text, date text" +
                        ", time text, gps_location text)"
        );
    }



    public Cursor getuser() {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from " + table_name + " ",
                    null);
            return res;
        }
    }