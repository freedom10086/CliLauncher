package me.yluo.clilauncher.helper;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.yluo.clilauncher.R;
import me.yluo.clilauncher.SuggestProvider;
import me.yluo.clilauncher.data.AppData;
import me.yluo.clilauncher.data.ArgType;
import me.yluo.clilauncher.data.Contact;
import me.yluo.clilauncher.utils.PinyinUtil;

public class LoadDbUtil {

    public static final String KEY_DB_CMD = "db_cmd";
    public static final String KEY_DB_PEOPLE = "db_people";
    private static final String KEY_DB_APP = "db_apps";

    private static final int hour = 1000*60*60;
    private static final int day = hour*24;

    public static void updateDabase(Context context){

        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = shp.edit();
        ContentResolver resolver = context.getContentResolver();

        long time = System.currentTimeMillis();
        String where = SuggestProvider.TYPE+ " = ?";

        long time_cmd =  shp.getLong(KEY_DB_CMD,0);
        if(time_cmd==0){
            String[] whereArgs = new String[]{String.valueOf(ArgType.COMMAND)};
            resolver.delete(SuggestProvider.URI,where,whereArgs);
            updateCmd(resolver,context);
            editor.putLong(KEY_DB_CMD,time);
        }

        long time_people = shp.getLong(KEY_DB_PEOPLE,0);
        if(time-time_people>day){
            String[] whereArgs = new String[]{String.valueOf(ArgType.PEOPLE)};
            resolver.delete(SuggestProvider.URI,where,whereArgs);
            uodatePeoples(resolver,context);
            editor.putLong(KEY_DB_PEOPLE,time);
        }


        editor.apply();
    }

    private static void updateCmd(ContentResolver resolver, Context context){

        //// TODO: 16-8-11  lodamore
        String[] cmds = context.getResources().getStringArray(R.array.commands);
        for (String s : cmds) {
            String name = null;
            if (s.contains(",")) {
                name = s.split(",")[0];
            } else {
                name  = s;
            }
            ContentValues values = new ContentValues();
            values.put(SuggestProvider.DISPLAY_NAME, name);
            values.put(SuggestProvider.CMD_CLASS_NAME, name);
            values.put(SuggestProvider.SEARCH_NAME, s);
            values.put(SuggestProvider.TYPE, ArgType.COMMAND);
            resolver.insert(SuggestProvider.URI,values);
        }

        String[] shells = context.getResources().getStringArray(R.array.shellcommand);
        for (String s : shells) {
            String name = null;
            if (s.contains(",")) {
                name = s.split(",")[0];
            } else {
                name  = s;
            }
            ContentValues values = new ContentValues();
            values.put(SuggestProvider.DISPLAY_NAME, name);
            values.put(SuggestProvider.CMD_CLASS_NAME,"shell");
            values.put(SuggestProvider.SEARCH_NAME, s);
            values.put(SuggestProvider.TYPE, ArgType.COMMAND);
            resolver.insert(SuggestProvider.URI,values);
        }
    }



    private static void uodatePeoples(ContentResolver resolver, Context context){
        Map<String,Contact> peoples =  ContactManerger.getContacts(context);
        Set<String> names =  peoples.keySet();
        for(String name:names){
            Contact contact = peoples.get(name);
            String searchtxt = name;
            for(String phone:contact.phones){
                searchtxt = searchtxt+","+phone;
            }
            ContentValues values = new ContentValues();
            values.put(SuggestProvider.DISPLAY_NAME, name);
            values.put(SuggestProvider.CMD_CLASS_NAME,"people");
            values.put(SuggestProvider.SEARCH_NAME,searchtxt);
            values.put(SuggestProvider.TYPE, ArgType.PEOPLE);
            resolver.insert(SuggestProvider.URI,values);
        }
    }

    public static List<AppData> updateApp(Context context){
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(context);
        long time_apps = shp.getLong(KEY_DB_APP,0);
        long time = System.currentTimeMillis();
        final PackageManager pm = context.getPackageManager();
        //get a list of installed apps.
        List<AppData> apps = new ArrayList<>();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            Intent intet = pm.getLaunchIntentForPackage(packageInfo.packageName);
            if(intet!=null){
                //String name, String pkg, Intent intent, Drawable icon
                AppData data = new AppData(packageInfo.loadLabel(pm),
                        packageInfo.packageName,
                        intet,
                        packageInfo.loadIcon(pm));
                apps.add(data);
            }
        }

        if(time-time_apps>hour){
            SharedPreferences.Editor editor = shp.edit();
            ContentResolver resolver =  context.getContentResolver();
            String where = SuggestProvider.TYPE+ " = ?";
            String[] whereArgs = new String[]{String.valueOf(ArgType.APP)};
            resolver.delete(SuggestProvider.URI,where,whereArgs);

            for(AppData app:apps){
                String sn = app.name.toString().toLowerCase();
                String firstpy = PinyinUtil.getFirstPy(sn);
                String fullpy = PinyinUtil.getFullPy(sn);
                sn +=","+firstpy+","+fullpy;
                ContentValues values = new ContentValues();
                values.put(SuggestProvider.DISPLAY_NAME,app.name.toString());
                values.put(SuggestProvider.CMD_CLASS_NAME,"apps");
                values.put(SuggestProvider.SEARCH_NAME,sn);
                values.put(SuggestProvider.TYPE, ArgType.APP);
                resolver.insert(SuggestProvider.URI,values);
            }
            editor.putLong(KEY_DB_APP,time);
            editor.apply();
        }

        return apps;
    }
}
