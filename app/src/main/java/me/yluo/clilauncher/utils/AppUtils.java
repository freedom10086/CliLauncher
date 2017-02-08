package me.yluo.clilauncher.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;


public class AppUtils {

    public static void install(Context context){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        File file = new File(Environment.getExternalStorageDirectory(),"HtmlUI1.apk");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void uninstall(Context context,String pkgname){

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:"+pkgname));
        context.startActivity(intent);
    }
}
