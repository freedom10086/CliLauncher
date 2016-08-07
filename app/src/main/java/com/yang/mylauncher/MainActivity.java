package com.yang.mylauncher;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FragmentManager manager;
    private Fragment f1,f2,current;
    private TextView info_text;
    private List<AppData> apps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = getFragmentManager();
        info_text = (TextView) findViewById(R.id.info_text);
        f1 = new MainFragment();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(f1,MainFragment.TAG).commit();
        current = f1;

        findViewById(R.id.btn_show_apps).setVisibility(View.GONE);

        String model = DeviceUtils.getModel();
        String netWork = NetworkUtils.getCurNetworkType(this);
        int memAva = DeviceUtils.getAvailMemory(this);
        int menTotal = DeviceUtils.getTotalMemory(this);

        String cpuinfo = "\ncpu info :"+DeviceUtils.getCpuInfo();
        int cpucount = DeviceUtils.getNumCores2();
        String meminfo = DeviceUtils.getMenInfo();

        String[] s =  DeviceUtils.getCpuFreq(0);
        Log.e("cpu", TextUtils.join("\n",s));

        long[] rom = DeviceUtils.getRomSize();
        Log.e("rom", rom[0]+"/"+rom[1]);

        Log.e("version",DeviceUtils.getSysInfo());

        DeviceUtils.getBootTime();
        info_text.setText(model+netWork+memAva+"/"+menTotal+meminfo);

        getApps();

    }



    @Override
    public void onBackPressed() {
        if(current instanceof AppListFragment){
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.hide(f2).show(f1).commit();
            current = f1;
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
                if(f2==null){
                    f2  = new AppListFragment();
                }
                FragmentTransaction transaction = manager.beginTransaction();
                if(!f2.isAdded()){
                    transaction.hide(f1).add(R.id.main_frame, f2,AppListFragment.TAG).commit();
                }else{
                    transaction.hide(f1).show(f2).commit();
                }
                current = f2;
        }
    }
}
