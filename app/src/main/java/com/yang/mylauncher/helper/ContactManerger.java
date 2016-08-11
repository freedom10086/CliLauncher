package com.yang.mylauncher.helper;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;

import com.yang.mylauncher.data.Contact;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 联系人操作辅助类
 */
public class ContactManerger {

    public static Map<String, Contact> getContacts(Context context) {
        Map<String, Contact> peoples = new LinkedHashMap<>();
        ContentResolver cr = context.getContentResolver();
        Cursor phone = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED},
                null, null, ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED + " DESC");

        while (phone != null && phone.moveToNext()) {
            String number = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String name = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String time = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TIMES_CONTACTED));

            Contact ad = peoples.get(name);
            if (ad == null) {
                ArrayList<String> s = new ArrayList<>();
                s.add(number);
                ad = new Contact(name, time, s);
                peoples.put(name, ad);
            } else
                ad.addPhone(number);
        }

        if (phone != null)
            phone.close();

        return peoples;
    }


    //以下是根据一个已知的电话号码，从通讯录中获取相对应的联系人姓名的代码
    public static String getNameFromNumBer(Context context, String phoneNum) {
        String contactName = "";
        ContentResolver cr = context.getContentResolver();
        Cursor pCur = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                new String[]{phoneNum}, null);
        if (pCur != null && pCur.moveToFirst()) {
            contactName = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            pCur.close();
        }
        return contactName;
    }


    public static void callPeopleByNumber(Context context,String num) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + "135xxxxxxxx");
        intent.setData(data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(context.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                context.startActivity(intent);
            }
        }else {
            context.startActivity(intent);
        }
    }

    public static void callPeopleByName(Context context,String name){
        //// TODO: 16-8-9
    }


}

