package com.yang.mylauncher;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.yang.mylauncher.data.AppData;
import com.yang.mylauncher.helper.LoadDbUtil;
import com.yang.mylauncher.utils.DeviceUtils;
import com.yang.mylauncher.utils.ImeUtil;
import com.yang.mylauncher.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener{

    private FragmentManager manager;
    private Fragment current;
    private TextView info_text1,info_text2;
    public List<AppData> apps =new ArrayList<>();
    private AppChangeReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        current = new MainFragment();
        manager.beginTransaction()
                .replace(R.id.main_frame,current,MainFragment.TAG)
                .commit();

        new UpdateDbTask().execute();

        receiver = new AppChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(receiver,intentFilter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver!=null){
            unregisterReceiver(receiver);
        }
    }

    @Override
    public void onBackPressed() {
        if(current instanceof AppListFragment){
            Fragment f = manager.findFragmentByTag(MainFragment.TAG);
            switchContent(f,MainFragment.TAG);
        }
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
                ImeUtil.hideSoftInput(this);
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


    private class UpdateDbTask extends AsyncTask<Void,Void,List<AppData>>{

        @Override
        protected List<AppData> doInBackground(Void... voids) {

            //load commadn to db
            LoadDbUtil.updateDabase(MainActivity.this);

            //load app to db
            return LoadDbUtil.updateApp(MainActivity.this);
        }

        @Override
        protected void onPostExecute(List<AppData> appsD) {
            apps.clear();
            apps.addAll(appsD);
            findViewById(R.id.btn_show_apps).setOnClickListener(MainActivity.this);
            findViewById(R.id.btn_show_apps).setVisibility(View.VISIBLE);
        }
    }


    public class AppChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            AppListFragment f = (AppListFragment) manager.findFragmentByTag(AppListFragment.TAG);
            String packageName = intent.getDataString().split(":")[1];
            Log.e("name",packageName);
            if(f!=null){
                switch (intent.getAction()){
                    case Intent.ACTION_PACKAGE_ADDED:
                        f.onAppDataChange(1,packageName);
                        break;
                    case Intent.ACTION_PACKAGE_REMOVED:
                        f.onAppDataChange(-1,packageName);
                        break;
                }
            }
        }

    }

}
