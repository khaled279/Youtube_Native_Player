package com.example.youtubenativeplayer;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
public class StorageUtil {
    private final  String Storage = "com.example.Rythemica.STORAGE";
    private SharedPreferences sharedPreferences  ;
    private Context context ;
    public StorageUtil(Context context){
        this.context = context;
    }
    public void storeSong(ArrayList<VideoData> arrayList){
        sharedPreferences = context.getSharedPreferences(Storage, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("VideoArrayList",json);
        editor.apply();
    }
    public ArrayList<VideoData> loadVideo(){
        sharedPreferences = context.getSharedPreferences(Storage , Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("VideoArrayList" , null);
        Type type = new TypeToken<ArrayList<VideoData>>(){}.getType() ;
        return gson.fromJson(json , type) ;
    }
    public void  storeVideoIndex(int index){
        sharedPreferences = context.getSharedPreferences(Storage,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("index" , index) ;
        editor.apply();
    }
    public  int loadVideoIndex(){
        sharedPreferences = context.getSharedPreferences(Storage , Context.MODE_PRIVATE);
        int index = sharedPreferences.getInt("index" , -1) ;

        return  index;}
    public  void  clearCachedVideoList(){
        sharedPreferences = context.getSharedPreferences(Storage , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit() ;
        editor.clear();
        editor.commit();

    }
}