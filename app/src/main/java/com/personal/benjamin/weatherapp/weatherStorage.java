package com.personal.benjamin.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.personal.benjamin.weatherapp.weatherDay;
import com.scottyab.aescrypt.AESCrypt;

import java.io.File;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.personal.benjamin.weatherapp.weatherMain.wDataArr;


public class weatherStorage {
    //class for interacting witht the various storage methods
    private String recentID;
    public void storeSP(Context context, String saveString,String Savename){ //storing sharedpreferences
        try {
            String password = "weather";
            SharedPreferences fileSP = context.getSharedPreferences("weatherapp", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = fileSP.edit();
            String encrypt = AESCrypt.encrypt(password, saveString);
            edit.putString(Savename,encrypt);
            edit.apply();
        }catch(Exception ex)
        {
            Toast.makeText(context, "saving failed", Toast.LENGTH_SHORT).show();
        }
    }
    public void storeDB(){
        new saveDB().execute("");
    } //storing the data in an asynchronous task

    public Boolean checkDB(){ //checking if the database has data in it by trying to extract some data
        //if it returns null then it has nothing
        long i = 1;
        if(weatherDay.findById(weatherDay.class,i) == null){
            return false;
        } else
            return true;
    }
    public void loadDB(){ //function for loading the data from the database
        int dayIndex = 0;
        for (long id = 1; id<7; id++){
            weatherDay wDLoad = weatherDay.findById(weatherDay.class, id);
            wDataArr[dayIndex] = new weatherDataObject();
            wDataArr[dayIndex].setSummary(wDLoad.getSummary());
            wDataArr[dayIndex].setSunrise(wDLoad.getSunrise());
            wDataArr[dayIndex].setSunset(wDLoad.getSunset());
            wDataArr[dayIndex].setTempH(wDLoad.getTempH());
            wDataArr[dayIndex].setTempL(wDLoad.getTempL());
            wDataArr[dayIndex].setwBearing(wDLoad.getwBearing());
            wDataArr[dayIndex].setwSpeed(wDLoad.getwSpeed());
            wDataArr[dayIndex].setTime(wDLoad.getTime());
            dayIndex++;
        }
    }
    public void deleteDB(){ //clearing the table and deleting the table to reset the IDs to always be 1 to 7
        weatherDay.deleteAll(weatherDay.class);
        weatherDay.executeQuery("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'WEATHER_DAY'");
    }
    private void saveWeather(Integer dayIndex){ //saving the data to the database using SugarORM
        weatherDay wDay = new weatherDay(wDataArr[dayIndex].getSummary(),wDataArr[dayIndex].getTempL(),
                wDataArr[dayIndex].getTempH(), wDataArr[dayIndex].getwBearing(),wDataArr[dayIndex].getwSpeed(),
                wDataArr[dayIndex].getSunrise(),wDataArr[dayIndex].getSunset(), wDataArr[dayIndex].getTime());
                wDay.save();
    }
    public void storeFB(){ //saving the summary to firebase
        FirebaseFirestore weatherfb = FirebaseFirestore.getInstance();
        Map<String, Object> summ = new HashMap<>();
        summ.put("summary", wDataArr[0].getSummary());
        CollectionReference summarycoll = weatherfb.collection("summary");
        summarycoll.document("latestsumm").set(summ);

    }
    public void loadFB(){ //loading the summary from firebase
        FirebaseFirestore weatherfb = FirebaseFirestore.getInstance();
        DocumentReference docsumm = weatherfb.collection("summary").document("latestsumm");
        docsumm.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    public String loadSP(Context context, String savename){ //loading the shared preferences
        String weathersp ="";
        String password = "weather";
        SharedPreferences sharedPref = context.getSharedPreferences("weatherapp", Context.MODE_PRIVATE);
        try{
        weathersp = AESCrypt.decrypt(password, sharedPref.getString(savename, ""));}
        catch (GeneralSecurityException e){

        }
        return weathersp;
    }
    private class saveDB extends AsyncTask<String, String, String>{ //Async task to save the data
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings) {
                try{
                    deleteDB(); //deletes the previous data before saving
                    long id = 1;
                    for (Integer i = 0;i <7; i++){
                        saveWeather(i);
                    }
                }catch(Exception e){
                    for (Integer i = 0;i <7; i++){
                        saveWeather(i);
                    }
                }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

    }
}

