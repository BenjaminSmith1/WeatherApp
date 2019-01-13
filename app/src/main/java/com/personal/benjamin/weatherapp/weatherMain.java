package com.personal.benjamin.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.location.Location;
import android.widget.*;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import android.Manifest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class weatherMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FusedLocationProviderClient mFusedLocationClient;
    private double lati = 0;
    private double longi = 0;
    private JSONObject weatherData;
    public static weatherDataObject[] wDataArr = new weatherDataObject[7];
    private weatherStorage wStore;
    public static Context mContext;
    private ConnectivityManager cm;
    public NetworkInfo mnetwork;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this is just instatiating stuff and some default stuff from the app
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
        cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        mnetwork = cm.getActiveNetworkInfo();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainFunction(view, 0);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        addLocation();
        wStore = new weatherStorage();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.weather_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.saveloc) {
            saveLoc();
            return true;
        }
        else if (id==R.id.loadloc){
            loadLoc();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody") //code adapted from the navigation activity that came with the app
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        View view = new View(this);
        if (id == R.id.today) {
            mainFunction(view,0);
        } else if (id == R.id.tommorow) {
            mainFunction(view,1);
        } else if (id == R.id.day3) {
            mainFunction(view,2);
        } else if (id == R.id.day4) {
            mainFunction(view,3);
        } else if (id == R.id.day5) {
            mainFunction(view,4);
        } else if (id == R.id.day6) {
            mainFunction(view, 5);
        }else if (id == R.id.sp) {
            wStore.storeFB();
            wStore.loadFB();
            displaySP();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void saveLoc(){ //saving the location to shared preferences
        wStore.storeSP(mContext, Double.toString(lati), "lati");
        wStore.storeSP(mContext, Double.toString(longi), "longi");
        Toast.makeText(mContext, "Location Saved", Toast.LENGTH_SHORT).show();

    }
    public void loadLoc(){ //loading the location from shareed preferences
        longi= Double.valueOf(wStore.loadSP(mContext, "longi"));
        lati = Double.valueOf(wStore.loadSP(mContext, "lati"));
        Toast.makeText(mContext,"Location Loaded", Toast.LENGTH_SHORT).show();
    }
    public void displaySP(){ //function to open a new activity with the location and summary in
        //this wouldnt be necessary in the finished app but it demostrates data parsing between activities
        View view = new View(this);
        if (lati==0 || longi==0){
            getDeviceCurrentLocation(view, 0);
        }
        Intent intent = new Intent(this, SP_FB_Data.class);
        intent.putExtra("lati", Double.toString(lati));
        intent.putExtra("longi", Double.toString(longi));
        intent.putExtra("summary", wDataArr[0].getSummary());
        mContext.startActivity(intent);
    }

    public void checkLocation(){ //checking the devices location by getting it again
        //this was needed as sometimes the API call would call zero as the location hadnt be returned due to asynchronous stuff
        //adapted from the tutorial on using google play services api on the android developers website
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some situations this can be null.
                        if (location != null) {
                            lati = location.getLatitude();
                            longi = location.getLongitude();
                        }
                    }
                });
    }

    public void getDeviceCurrentLocation(final View view, final Integer dayindex){ //getting the devices location
        //adapted from the tutorial on using google play services api on the android developers website
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some situations this can be null.
                        if (location != null) {
                            makeWeatherRequest(view,dayindex);
                        }
                    }
                });
    }

    public void addLocation() {
        //getting permissions, this is adapted from the tutorial on using the google play services api
        ActivityCompat.requestPermissions(weatherMain.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        //code adapted from the tutorial on using the google play services api on the android developers website
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(weatherMain.this, "Permission granted to access location!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(weatherMain.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    addLocation(); //loops until the user gives permission
                }
            }
        }
    }
    public void makeWeatherRequest(View view, final Integer dayIndex) {
        //code adapted from the Volley tutorial on Android developer website
        final TextView locationout = view.findViewById(R.id.locationbox);
// Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.darksky.net/forecast/02d8514b5b0bb5c187b5ab1a2bf4ad19/"+ lati + ","+longi+"?exclude=currently,minutely,hourly,alerts,flags&units=uk2";

// Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                weatherData = response;
                storeData(weatherData);
                displayData(dayIndex);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                locationout.setText("unlucky");
            }
        });
// Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
    }

    public void storeData(JSONObject weatherData){ //storing the data to the array of objects
        try {
            JSONObject wDaily = weatherData.getJSONObject("daily");
            JSONArray wData = wDaily.getJSONArray("data");
            for(int i =0; i<7; i++) {
                JSONObject c = wData.getJSONObject(i);
                wDataArr[i] = new weatherDataObject();
                wDataArr[i].setSummary(c.getString("summary"));
                wDataArr[i].setTempL(c.getString("temperatureLow"));
                wDataArr[i].setTempH(c.getString("temperatureHigh"));
                wDataArr[i].setwBearing(c.getInt("windBearing"));
                wDataArr[i].setwSpeed(c.getString("windSpeed"));
                wDataArr[i].setSunrise(c.getInt("sunriseTime"));
                wDataArr[i].setSunset(c.getInt("sunsetTime"));
                wDataArr[i].setTime(c.getInt("time"));
            }
        } catch (final JSONException e){ //catch code adapted from http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
            Log.e("ERROR", "Json parsing error" + e.getMessage());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Json error: " + e.getMessage(),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
        wStore.storeDB();
    }
    public void displayData(Integer dayIndex){ //outputs the data to the screen
        TextView summaryV = findViewById(R.id.Summary);
        TextView tempminV = findViewById(R.id.tempL);
        TextView tempmaxv = findViewById(R.id.tempH);
        TextView bearingV = findViewById(R.id.winddirection);
        TextView wspeedV = findViewById(R.id.windspeed);
        TextView sunrV = findViewById(R.id.sunrise);
        TextView sunsV = findViewById(R.id.sunset);
        TextView lativs = findViewById(R.id.latit);
        TextView longiw = findViewById(R.id.longiw);
        summaryV.setText(wDataArr[dayIndex].getSummary());
        tempminV.setText(wDataArr[dayIndex].getTempL());
        tempmaxv.setText(wDataArr[dayIndex].getTempH());
        bearingV.setText(windConvert(wDataArr[dayIndex].getwBearing()));
        wspeedV.setText(wDataArr[dayIndex].getwSpeed());
        sunrV.setText(timeConvert(wDataArr[dayIndex].getSunrise()));
        sunsV.setText(timeConvert(wDataArr[dayIndex].getSunset()));
        lativs.setText(Double.toString(lati));
        longiw.setText(Double.toString(longi));
    }
    public String windConvert(Integer bearing){ //function to convert the bearing into a direction
        String direction = "";
        double val = (bearing/22.5)+0.5;
        String[] arr={"N","NNE","NE","ENE","E","ESE","SE","SSE","S","SSW","SW","WSW","W", "WNW","NW", "NNW"};
        //int value  = mod(val);
        int value = (int)(val%16);
        direction = arr[value];
        return direction;
    }
    public String timeConvert(Integer unixDate){ //function to convert Unix time into readable time
        // Calendar calendar = Calendar.getInstance();
        Long unixDateLong = unixDate*1000L;
        // calendarDate = String.valueOf(calendar.get((Calendar.DAY_OF_MONTH)))+ String.valueOf(calendar.get((Calendar.MONTH ))+1) + String.valueOf(calendar.get(Calendar.YEAR));
        java.text.SimpleDateFormat calendarDate = new java.text.SimpleDateFormat("yyyy - MM - dd HH:mm:ss z");
        calendarDate.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
        String calendartime = calendarDate.format(unixDateLong);
        return calendartime;
    }
    public void mainFunction(View view, int dayIndex){
        mnetwork = cm.getActiveNetworkInfo(); //updates the network info
        checkLocation();
         if(mnetwork==null){ //checks if the network is null
             if (wStore.checkDB()==true){ //checks if there is data in the database, if there is then load it
                Toast.makeText(mContext, "No network to connect to. Using last saved data", Toast.LENGTH_SHORT).show();
                wStore.loadDB();
                displayData(dayIndex);
             } else { //if both fail then tell the user to connect to a network.
                 Toast.makeText(mContext, "No Connection or data. Please connect to a network.", Toast.LENGTH_SHORT).show();
             }
        }else if (mnetwork.isConnected()&& mnetwork!=null){// if there is a network
            if (wStore.checkDB() == false){ //and if there isnt data in the database do an api call
            getDeviceCurrentLocation(view,dayIndex);
            //makeWeatherRequest(view, dayIndex);
            }else {
                wStore.loadDB();
                displayData(dayIndex);
            } //if there is data in the database, load it. Saves API calls
        }
    }
}
