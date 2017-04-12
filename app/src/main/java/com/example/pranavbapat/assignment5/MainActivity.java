package com.example.pranavbapat.assignment5;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerYear, spinnerCountry, spinnerState;
    Boolean firstCallSpinnerCountry=true;
    private int lastID;
    private static final String SELECT_ONE = "Select";
    private static final int DEFAULT_PAGE_SIZE = 25;
    private boolean filterValid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinnerYear = (Spinner) findViewById(R.id.filterYear);
        spinnerCountry = (Spinner) findViewById(R.id.filterCountry);
        spinnerState = (Spinner) findViewById(R.id.filterState);
        filterValid = false;
        populateCountries(new VolleyResponseCallBack() {
            @Override
            public void onSuccess(ArrayList<String> countryList) {
                setCountrySpinnerData(countryList);
            }
        });
        populateYearSpinner();
        lastID = -1;
        populateInitialUsers(lastID);

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            ArrayList<String> stateList;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!firstCallSpinnerCountry) {
                    String selectedCountry = spinnerCountry.getSelectedItem().toString();

                    populateStatesSpinner(new VolleyResponseCallBack() {
                        @Override
                        public void onSuccess(ArrayList<String> stateListFromCountry) {
                            stateList = stateListFromCountry;
                            setStateSpinnerData(stateList);
                        }
                    }, selectedCountry);
                }
                firstCallSpinnerCountry = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /*public void filterUsers(View v){
        StringBuilder filterSQL = new StringBuilder("SELECT * FROM USERS WHERE");
        String field;


        ArrayList<User> userList =
    }*/

    if(countryID == -1){
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                ArrayList<String> stateList = new ArrayList<String>();
                int numObjects = response.length();
                try {
                    for (int i = 0; i < numObjects; i++) {
                        stateList.add(response.get(i).toString());
                        System.out.println(stateList.get(i));
                    }
                    dbHelper.addStatesForCountry(stateList,countryName);
                    callback.onSuccess(stateList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("rew", error.toString());
            }
        };

        final String URL_STATE = "http://bismarck.sdsu.edu/hometown/states?country=" + countryName;
        JsonArrayRequest getRequest = new JsonArrayRequest(URL_STATE, success, failure);
        VolleyRequestQueue.getInstance(getApplicationContext()).addToRequestQueue(getRequest);
    }else{
        ArrayList<String> stateList = dbHelper.getStatesForCountry(countryName);
        callback.onSuccess(stateList);
    }
    //Fetches users from db.
    //Remaining users from api
    public ArrayList<User> getFilteredUsers(String country, String state, String year){
        //Call DBHelper with SQL query which will return ArrayList
        UserDBHelper userDB = new UserDBHelper(this);
        String sqlQuery = buildFilterQuery(country, state, year);
        ArrayList<User> userList = userDB.getUsersFromDB(sqlQuery);
        if(userList == null){
            userList.addAll(0, getUsersFromAPI(country, state, year, DEFAULT_PAGE_SIZE));
        }else if(userList.size() == DEFAULT_PAGE_SIZE){
            return userList;
        }else if(userList.size() < DEFAULT_PAGE_SIZE){
            userList = getUsersFromAPI(country, state, year, DEFAULT_PAGE_SIZE - userList.size());
            userDB.insertAll(userList);
        }
        return userList;
    }

    public ArrayList<User> getUsersFromAPI(String country, String state, String year, int usersToFetch){
        ArrayList<User> apiUserList = new ArrayList<User>();
        String requestURL = buildFilterURL(country, state, year);
        //Make QUERY
        Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                ArrayList<String> stateList = new ArrayList<String>();
                int numObjects = response.length();
                try {
                    for (int i = 0; i < numObjects; i++) {
                        stateList.add(response.get(i).toString());
                        System.out.println(stateList.get(i));
                    }

                    callback.onSuccess(stateList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.d("rew", error.toString());
            }
        };
    }

    //Builds sql query for filter
    public String buildFilterQuery(String country, String state, String year){
        boolean preceedingFieldPresent=false;
        StringBuilder filterSQL = new StringBuilder("SELECT * FROM USERS WHERE");

        if(!spinnerCountry.getSelectedItem().equals(SELECT_ONE)){
            field = spinnerCountry.getSelectedItem().toString();
            filterSQL.append(" country = '");
            filterSQL.append(field);
            filterSQL.append("'");
            preceedingFieldPresent = true;
        }

        if(spinnerState.getSelectedItem() != null && !spinnerState.getSelectedItem().toString().equals(SELECT_ONE)) {
            field = spinnerState.getSelectedItem().toString();
            if (preceedingFieldPresent) {
                filterSQL.append(" & ");
            }

            filterSQL.append(" state = '");
            filterSQL.append(field);
            filterSQL.append("'");
            preceedingFieldPresent = true;
        }

        if(!spinnerYear.getSelectedItem().equals(SELECT_ONE)){
            field = spinnerYear.getSelectedItem().toString();
            if(preceedingFieldPresent){ filterSQL.append(" & ");}

            filterSQL.append(" year = '");
            filterSQL.append(field);
            filterSQL.append("'");
        }

        filterSQL.append(" ORDER BY u_id DESC LIMIT 25;");
        System.out.println(filterSQL.toString());
    }

    public void populateInitialUsers(int lastID){
        UserDBHelper userDB = new UserDBHelper(this);
        Log.d("create user db",userDB.getDatabaseName());
        //Call fetch users with id
    }

    public void setCountrySpinnerData(ArrayList<String> countryList){
        countryList.add(0,SELECT_ONE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(adapter);
    }

    public void setStateSpinnerData(ArrayList<String> stateList){
        stateList.add(0,SELECT_ONE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stateList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(adapter);
    }

    public void populateCountries(final VolleyResponseCallBack callback){
        final String URL_COUNTRY = "http://bismarck.sdsu.edu/hometown/countries";
        final DBHelper dbHelper = new DBHelper(this);

        if(dbHelper.getCountryCount() == 0){
            Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
                public void onResponse(JSONArray response) {
                    ArrayList<String> countryList = new ArrayList<String>();
                    int numObjects = response.length();
                    try {
                        for (int i = 0; i < numObjects; i++) {
                            countryList.add(response.get(i).toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dbHelper.addCountries(countryList);
                    callback.onSuccess(countryList);
                }
            };
            Response.ErrorListener failure = new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    Log.d("rew", error.toString());
                }
            };
            JsonArrayRequest getRequest = new JsonArrayRequest(URL_COUNTRY, success, failure);
            VolleyRequestQueue.getInstance(getApplicationContext()).addToRequestQueue(getRequest);
        }else{
            ArrayList<String> countryList;
            countryList = dbHelper.getCountries();
            callback.onSuccess(countryList);
        }
    }

    public void populateYearSpinner(){
        ArrayList<String> yearList = new ArrayList<String>();
        for(int i=1970;i<=2017;i++)
            yearList.add(String.valueOf(i));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, yearList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(adapter);
    }

    public void populateStatesSpinner(final VolleyResponseCallBack callback, final String countryName){
        final DBHelper dbHelper = new DBHelper(this);
        int countryID = dbHelper.checkStateInDB(countryName);

        if(countryID == -1){
            Response.Listener<JSONArray> success = new Response.Listener<JSONArray>() {
                public void onResponse(JSONArray response) {
                    ArrayList<String> stateList = new ArrayList<String>();
                    int numObjects = response.length();
                    try {
                        for (int i = 0; i < numObjects; i++) {
                            stateList.add(response.get(i).toString());
                            System.out.println(stateList.get(i));
                        }
                        dbHelper.addStatesForCountry(stateList,countryName);
                        callback.onSuccess(stateList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            Response.ErrorListener failure = new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    Log.d("rew", error.toString());
                }
            };

            final String URL_STATE = "http://bismarck.sdsu.edu/hometown/states?country=" + countryName;
            JsonArrayRequest getRequest = new JsonArrayRequest(URL_STATE, success, failure);
            VolleyRequestQueue.getInstance(getApplicationContext()).addToRequestQueue(getRequest);
        }else{
            ArrayList<String> stateList = dbHelper.getStatesForCountry(countryName);
            callback.onSuccess(stateList);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_user:
                Intent addUserIntent = new Intent(this, NewUserActivity.class);
                startActivity(addUserIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);
        return true;
    }
}
