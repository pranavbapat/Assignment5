package com.example.pranavbapat.assignment5;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pranav.bapat on 4/3/17.
 */

public class UserDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hometownsdsu";
    private static final int DATABASE_VERSION = 1;

    private static final String USER_TABLE = "users";
    private static final int DEFAULT_PAGE_SIZE = 25; //25 users at once

    private boolean DB_EMPTY;

    public UserDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCountryTable = "CREATE TABLE IF NOT EXISTS " +  USER_TABLE + "(u_id INTEGER PRIMARY KEY, nickname TEXT, city TEXT, state TEXT, country TEXT, timestamp TEXT, lat double, lng double);";
        db.execSQL(createCountryTable);
        DB_EMPTY = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<User> getUsersFromDB(String sqlQuery){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        String nickname, city, state, country;
        double latitude, longitude;
        ArrayList<User> userList=null;

        if(cursor.moveToFirst()){
            userList = new ArrayList<User>();
            User oneUser;
            do{
                oneUser = new User();
                nickname = cursor.getString(1);
                city = cursor.getString(2);
                state = cursor.getString(3);
                country = cursor.getString(4);
                latitude = cursor.getDouble(6);
                longitude = cursor.getDouble(7);
                oneUser.setNickname(nickname);
                oneUser.setCity(city);
                oneUser.setState(state);
                oneUser.setCountry(country);
                oneUser.setHometownLatLng(new LatLng(latitude, longitude));
                userList.add(oneUser);
                Log.d("fetched user from db",nickname);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return userList;
    }
    //fetch users returns 25 users
    //Query the db first and add all users from db
    //Call API for remaining users
    //Add to DB
    //Fetch from DB

    //Used onScrollView
   /* public ArrayList<User> fetchUsers(int lastID){
        String fetchUsersSQL = "SELECT * FROM USERS WHERE u_id > " + lastID + " ORDER BY u_id DESC LIMIT 25";
        ArrayList<User> userList = fetchUsersDB(fetchUsersSQL);

        //if(userList == null || userList.size() == 0)
            return fetchUsersDB(fetchUsersSQL);

    }*/

    /*public ArrayList<User> fetchUsers(String country, String state, String city, int year){
        String fetchUsersSQL = "SELECT * FROM USERS WHERE ";
        //Check in DB
        //If not call API
        //Store in db
    }*/

    //Fetch from DB
    /*private ArrayList<User> fetchUsersDB(String fetchUsersSQL){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(fetchUsersSQL, null);
        String nickname, city, state, country;
        double latitude, longitude;
        if(cursor.moveToFirst()){
            ArrayList<User> userList = new ArrayList<User>();
            User oneUser;
            do{
                oneUser = new User();
                nickname = cursor.getString(1);
                city = cursor.getString(2);
                state = cursor.getString(3);
                country = cursor.getString(4);
                latitude = cursor.getDouble(6);
                longitude = cursor.getDouble(7);
                oneUser.setNickname(nickname);
                oneUser.setCity(city);
                oneUser.setState(state);
                oneUser.setCountry(country);
                oneUser.setHometownLatLng(new LatLng(latitude, longitude));
                userList.add(oneUser);
            }while(cursor.moveToNext());
        }else{
            cursor.close();
            return fetchUsersAPI();
        }
    }
*/
    //Fetch from API
//    private ArrayList<User> fetchUsersAPI(){}

    //Adding data to DB
    private void addDataToDB(ArrayList<User> userList){}
}
