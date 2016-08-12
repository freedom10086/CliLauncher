package com.yang.mylauncher.data;

import android.content.Intent;
import android.graphics.drawable.Drawable;



public class AppData {
    public AppData(CharSequence name, String pkg, Intent intent, Drawable icon) {
        this.name = name;
        this.pkg = pkg;
        this.intent = intent;
        this.icon = icon;
        this.searchKey = name.toString();
    }

    public CharSequence name;
    public String pkg;
    public Intent intent;
    public Drawable icon;
    public String searchKey;
}
