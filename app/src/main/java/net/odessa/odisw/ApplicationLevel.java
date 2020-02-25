package net.odessa.odisw;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.odessa.odisw.model.Request;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ApplicationLevel extends Application {

    static Context context;
    public static List<Request> requests = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    static public void savePhone(String value) {
        SharedPreferences.Editor pref = context.getSharedPreferences("PHONE", Context.MODE_PRIVATE).edit();
        pref.putString("my_phone", value);
        pref.apply();
    }

    static public String loadPhone()  {
        SharedPreferences prefs = context.getSharedPreferences("PHONE", Context.MODE_PRIVATE);
        return prefs.getString("my_phone","");
    }

    static public void saveServerUri(String value) {
        SharedPreferences.Editor pref = context.getSharedPreferences("URI", Context.MODE_PRIVATE).edit();
        pref.putString("server_uri", value);
        pref.apply();
    }

    static public String loadServerUri()  {
        SharedPreferences prefs = context.getSharedPreferences("URI", Context.MODE_PRIVATE);
        return prefs.getString("server_uri","http://aster.ow.od.ua:8080");
    }

    static public void savePreambula(String value) {
        SharedPreferences.Editor pref = context.getSharedPreferences("PREAMBULA", Context.MODE_PRIVATE).edit();
        pref.putString("preambula_text", value);
        pref.apply();
    }

    static public String loadPreambula()  {
        SharedPreferences prefs = context.getSharedPreferences("PREAMBULA", Context.MODE_PRIVATE);
        return prefs.getString("preambula_text","M20A");
    }

    static public List<Request> getDataFromSharedPreferences(){
        Gson gson = new Gson();
        List<Request> RequestFromShared = new ArrayList<>();
        SharedPreferences sharedPref = context.getSharedPreferences("REQUESTS", Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString("REQUEST_LIST", "");

        Type type = new TypeToken<List<Request>>() {}.getType();
        RequestFromShared = gson.fromJson(jsonPreferences, type);

        return RequestFromShared;
    }

    static public void setDataFromSharedPreferences(List<Request> curRequest){
        Gson gson = new Gson();

        if(curRequest.size() > 1000){
            do {
                curRequest.remove(0);
            } while (curRequest.size() > 1000);
        }

        String jsonCurRequest = gson.toJson(curRequest);

        SharedPreferences sharedPref = context.getSharedPreferences("REQUESTS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("REQUEST_LIST", jsonCurRequest);
        editor.apply();
    }
    
}
