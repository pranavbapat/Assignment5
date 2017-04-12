package com.example.pranavbapat.assignment5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by pranav.bapat on 4/2/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hometownsdsu";
    private static final int DATABASE_VERSION = 1;


    private static final String COUNTRY_TABLE = "country";
    private static final String COUNTRY_KEY = "country_name";

    private static final String STATE_TABLE = "state";
    private static final String STATE_KEY = "state_name";
    private static final String STATE_COUNTRY_ID = "country_id";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCountryTable = "CREATE TABLE IF NOT EXISTS " +  COUNTRY_TABLE + "(country_id INTEGER PRIMARY KEY, country_name TEXT);";
        db.execSQL(createCountryTable);

        String createStateTable = "CREATE TABLE IF NOT EXISTS " + STATE_TABLE + " (state_id INTEGER PRIMARY KEY, country_id INTEGER, state_name TEXT);";
        System.out.println(createStateTable);
        db.execSQL(createStateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addCountries(ArrayList<String> countryList){
        SQLiteDatabase db = this.getWritableDatabase();
        String clearDBQuery = "DELETE FROM "+ COUNTRY_TABLE;
        db.execSQL(clearDBQuery);

        ContentValues values;
        String country;
        Iterator countryIterator = countryList.iterator();
        while(countryIterator.hasNext()){
            values = new ContentValues();
            country = (String) countryIterator.next();
            values.put(COUNTRY_KEY, country);
            db.insert(COUNTRY_TABLE, null, values);
        }
        db.close();
    }

    public ArrayList<String> getCountries(){
        ArrayList<String> countryList = new ArrayList<String>();
        String SELECT_COUNTRIES_SQL = "SELECT * FROM " + COUNTRY_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(SELECT_COUNTRIES_SQL, null);

        if(cursor.moveToFirst()){
            do{
                countryList.add(cursor.getString(1));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return countryList;
    }

    public int getCountryCount(){
        String SELECT_COUNTRIES_SQL = "SELECT * FROM " + COUNTRY_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(SELECT_COUNTRIES_SQL, null);
        int countryCount = cursor.getCount();
        cursor.close();
        return countryCount;
    }

    public int checkStateInDB(String countryName){
        SQLiteDatabase db = this.getReadableDatabase();

        String SQ = "SELECT c.country_id FROM country c JOIN state s ON c.country_id = s.country_id where c.country_name='" + countryName + "';";
        Cursor cursor = db.rawQuery(SQ, null);
        System.out.println("STATES : " + cursor.getCount());
        if(cursor.moveToNext()){
            return cursor.getInt(0);
        }
        return -1;
    }

    public ArrayList<String> getStatesForCountry(String countryName){
        ArrayList<String> stateList = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();

        String SQ = "SELECT s.state_name FROM state s JOIN country c ON s.country_id = c.country_id where c.country_name='" + countryName + "';";
        Cursor cursor = db.rawQuery(SQ, null);

        if(cursor.moveToFirst()){
            do{
                stateList.add(cursor.getString(0));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return stateList;
    }

    public void addStatesForCountry(ArrayList<String> stateList, String countryName){
        SQLiteDatabase db = this.getWritableDatabase();
        String GET_COUNTRY_ID_SQL = "SELECT country_id FROM " + COUNTRY_TABLE + " where " + COUNTRY_KEY + " ='" + countryName + "';";
        Cursor cursor = db.rawQuery(GET_COUNTRY_ID_SQL,null);

        if(cursor.moveToNext()) {
            int countryID = cursor.getInt(0);

            ContentValues values;
            String state;
            for (String aState : stateList) {
                values = new ContentValues();
                values.put(STATE_KEY, aState);
                values.put(STATE_COUNTRY_ID, countryID);
                db.insert(STATE_TABLE, null, values);
            }
            Log.d("Add", "added states for country " + countryName);
            db.close();
        }
    }
}
