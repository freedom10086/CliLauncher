package com.yang.mylauncher.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AppChangeReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent){
        //接收安装广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            //TODO
        }
        //接收卸载广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            //TODO
        }
    }
}
