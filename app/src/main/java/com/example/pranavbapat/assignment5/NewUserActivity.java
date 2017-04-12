package com.example.pranavbapat.assignment5;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NewUserActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    GoogleMap map;
    LatLng userLatLng;
    EditText userNickname, userPassword, userCity;
    TextView userLatLongTextView;
    Spinner spinnerCountry, spinnerState;
    Boolean firstCallSpinnerCountry=true, firstCallSpinnerState=true;
    NumberPicker userYear;
    private static final String SELECT_ONE = "Select";
    User newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        newUser = new User();
        userNickname = (EditText) findViewById(R.id.user_nickname);
        userPassword = (EditText) findViewById(R.id.user_password);
        userCity = (EditText) findViewById(R.id.user_city);
        spinnerCountry = (Spinner) findViewById(R.id.spinner_country);
        spinnerState = (Spinner) findViewById(R.id.spinner_state);
        userYear = (NumberPicker) findViewById(R.id.numberPickerYear);
        userLatLongTextView = (TextView) findViewById(R.id.textView_lat_long);
        initYear();
        initCountry();

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            ArrayList<String> stateList;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    String selectedCountry = spinnerCountry.getSelectedItem().toString();
                    populateStatesSpinner(new VolleyResponseCallBack() {
                        @Override
                        public void onSuccess(ArrayList<String> stateListFromCountry) {
                            stateList = stateListFromCountry;
                            stateList.add(0,SELECT_ONE);
                            setStateSpinnerData(stateList);
                        }
                    }, selectedCountry);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedState = spinnerState.getSelectedItem().toString();
                String selectedCountry = spinnerCountry.getSelectedItem().toString();
                if(!selectedState.equals(SELECT_ONE) && !selectedCountry.equals(SELECT_ONE)) {
                    moveCameraToStateCountry(selectedState, selectedCountry);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    public void moveCameraToStateCountry(String state, String country){
        Geocoder locator = new Geocoder(this);
        List<Address> addressList;
        String moveCameraTo = state + " " + country;
        try {
            addressList = locator.getFromLocationName(moveCameraTo,5);
            Address oneAddress = addressList.get(0);
            double latitude = oneAddress.getLatitude();
            double longitude = oneAddress.getLongitude();
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
            Log.d("move", "moving camera to " + state + " " + country);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStateSpinnerData(ArrayList<String> stateList){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stateList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerState.setAdapter(adapter);
    }
    public void initCountry(){
        ArrayList<String> countryList;
        DBHelper dbHelper = new DBHelper(this);
        countryList = dbHelper.getCountries();
        countryList.add(0,SELECT_ONE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(adapter);
    }

    public void initYear(){
        userYear.setMinValue(1970);
        userYear.setMaxValue(2017);
        userYear.setWrapSelectorWheel(false);

        userYear.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                newUser.setYear(newVal);
            }
        });
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

    public void addNewUser(View v){
        final String URL_POST_USER = "http://bismarck.sdsu.edu/hometown/adduser";
        User newUser = new User();
        newUser.setNickname(userNickname.getText().toString());
        newUser.setPassword(userPassword.getText().toString());
        newUser.setCountry(spinnerCountry.getSelectedItem().toString());
        newUser.setState(spinnerState.getSelectedItem().toString());
        newUser.setYear(userYear.getValue());
        if(userLatLng == null)
            newUser.setHometownLatLng(new LatLng(0,0));
        else{
            newUser.setCity(userCity.getText().toString());
            newUser.setHometownLatLng(new LatLng(userLatLng.latitude, userLatLng.longitude));
        }

        if(newUser.isValidUserInfo()){
            //Post to Server
            JSONObject jsonUser = new JSONObject();
                try {
                    jsonUser.put("nickname", newUser.getNickname());
                    jsonUser.put("password", newUser.getPassword());
                    jsonUser.put("country", newUser.getCountry());
                    jsonUser.put("state", newUser.getState());
                    jsonUser.put("city", newUser.getCity());
                    jsonUser.put("year", newUser.getYear());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("User added!");
                    }
                };

                Response.ErrorListener failure = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {}
                };

                JsonObjectRequest postRequest = new JsonObjectRequest(URL_POST_USER, jsonUser, success, failure);
                VolleyRequestQueue.getInstance(this).addToRequestQueue(postRequest);
            }
    }

    public void setUserLocation(View v){
        newUser.setHometownLatLng(userLatLng);
        double latitude = newUser.getHometownLatLng().latitude;
        double longitude = newUser.getHometownLatLng().longitude;
        String userLatLong = String.format("(Latitude, Longitude): (%.2f, %.2f)",latitude, longitude);
        userLatLongTextView.setText(userLatLong);

        //getting locality from (lat,long)
        Geocoder locator = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = locator.getFromLocation(latitude, longitude, 1);
            String city = addressList.get(0).getAddressLine(0).toString();
            String[] geocodeAddress = city.split(",");
            city = geocodeAddress[0];
            newUser.setCity(city);
            userCity.setText(city);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng location) {
        map.clear();
        userLatLng = location;
        map.addMarker(new MarkerOptions().position(location).title("Hometown"));
        map.moveCamera(CameraUpdateFactory.newLatLng(location));
    }
}
