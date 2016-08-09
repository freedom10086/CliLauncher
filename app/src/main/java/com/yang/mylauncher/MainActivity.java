package com.yang.mylauncher;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.yang.mylauncher.command.raw.base;
import com.yang.mylauncher.suggest.ContactManerger;
import com.yang.mylauncher.suggest.SuggestProvider;
import com.yang.mylauncher.utils.DeviceUtils;
import com.yang.mylauncher.utils.HttpUtils;
import com.yang.mylauncher.utils.NetworkUtils;
import com.yang.mylauncher.utils.ShellUtils;
import com.yang.mylauncher.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends Activity implements View.OnClickListener{

    private FragmentManager manager;
    private Fragment current;
    private TextView info_text1,info_text2;
    private List<AppData> apps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTransparentStatusBar();
        manager = getFragmentManager();
        info_text1 = (TextView) findViewById(R.id.info_1);
        info_text2 = (TextView) findViewById(R.id.info_2);

        findViewById(R.id.btn_show_apps).setVisibility(View.GONE);

        String model = DeviceUtils.getModel();
        String menufacture = DeviceUtils.getManufacturer();
        String netWork = NetworkUtils.getCurNetworkType(this);
        int memAva = DeviceUtils.getAvailMemory(this);
        int menTotal = DeviceUtils.getTotalMemory(this);
        long[] rom = DeviceUtils.getRomSize();
        DeviceUtils.getBootTime();
        info_text1.setText(menufacture+" "+model+" "+netWork);
        info_text2.setText("RAM:"+memAva+"/"+menTotal+"MB  ROM:"+rom[0]+"/"+rom[1]+"MB");
        getApps();

        current = new MainFragment();
        manager.beginTransaction()
                .replace(R.id.main_frame,current,MainFragment.TAG)
                .commit();

        ContactManerger manerger =new ContactManerger();
        Map<String,ContactManerger.Contact> peoples =  manerger.getContacts(this);
        Set<String> names =  peoples.keySet();
        for(String name:names){
            Log.e("name","name"+name+"  "+peoples.get(name).toString());
        }


        ContentValues values = new ContentValues();
        values.put(SuggestProvider.COLUM_NAME, "name5");
        values.put(SuggestProvider.COLUM_TYPE,0);
        values.put(SuggestProvider.COLUM_USE_COUNT,2);
        values.put(SuggestProvider.COLUM_USE_TIME, Utils.getCurrentTime());
        getContentResolver().insert(SuggestProvider.CONTENT_URI, values);

        String columns[] = new String[] {SuggestProvider.COLUM_NAME,SuggestProvider.COLUM_USE_TIME,SuggestProvider.COLUM_USE_COUNT};
        Cursor cur = getContentResolver().query(SuggestProvider.CONTENT_URI,
                columns,null,null,SuggestProvider.COLUM_USE_TIME+" DESC, "+SuggestProvider.COLUM_USE_COUNT+" DESC");
        if(cur!=null){
            while (cur.moveToNext()){
                String name = cur.getString(cur.getColumnIndex(SuggestProvider.COLUM_NAME));
                String time = cur.getString(cur.getColumnIndex(SuggestProvider.COLUM_USE_TIME));
                int count = cur.getInt(cur.getColumnIndex(SuggestProvider.COLUM_USE_COUNT));
                Log.e("db",name+time+"  "+count);
            }
            cur.close();
        }

        getContentResolver().update(SuggestProvider.CONTENT_URI,null,null,new String[]{"name5"});
    }



    @Override
    public void onBackPressed() {
        if(current instanceof AppListFragment){
            Fragment f = manager.findFragmentByTag(MainFragment.TAG);
            switchContent(f,MainFragment.TAG);
        }
    }


    private class getAppTask extends AsyncTask<Void, Void, List<AppData>>{
        @Override
        protected List<AppData> doInBackground(Void... voids) {
            final PackageManager pm = getPackageManager();
            //get a list of installed apps.
            List<AppData> apps = new ArrayList<>();
            Log.e("time","start :"+System.currentTimeMillis());
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
            Log.e("time","end :"+System.currentTimeMillis());
            return apps;
        }

        @Override
        protected void onPostExecute(List<AppData> appDatas) {
            super.onPostExecute(appDatas);
            apps.clear();
            apps.addAll(appDatas);
            findViewById(R.id.btn_show_apps).setOnClickListener(MainActivity.this);
            findViewById(R.id.btn_show_apps).setVisibility(View.VISIBLE);

        }
    }

    public List<AppData> getApps() {
        if(apps==null){
            apps = new ArrayList<>();
            new getAppTask().execute();
        }
        return apps;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_show_apps:
                FragmentManager fm = getFragmentManager();
                Fragment f =  fm.findFragmentByTag(AppListFragment.TAG);
                if(f==null){
                    f  = new AppListFragment();
                }
                switchContent(f,AppListFragment.TAG);
        }
    }


    private void switchContent(Fragment to, String Tag) {
        if (current != to) {
            FragmentTransaction transaction = manager.beginTransaction();
            //.setCustomAnimations(android.R.anim.fade_in, R.anim.slide_out);
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(current).add(R.id.main_frame, to,Tag).commit();
                // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(current).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
            current = to;
        }
    }


    /**
     * 设置透明状态栏(api >= 19方可使用)
     * <p>android:clipToPadding="true"
     * <p>android:fitsSystemWindows="true"
     */
    private   void setTransparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

}
