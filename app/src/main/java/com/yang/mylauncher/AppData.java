package com.yang.mylauncher;

import android.content.Intent;
import android.graphics.drawable.Drawable;



public class AppData {
    public AppData(CharSequence name, String pkg, Intent intent, Drawable icon) {
        this.name = name;
        this.pkg = pkg;
        this.intent = intent;
        this.icon = icon;
    }

    CharSequence name;
    String pkg;
    Intent intent;
    Drawable icon;
}
