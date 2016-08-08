package com.yang.mylauncher.command;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ContactManerger {


    public Map<String, Contact> getContacts(Context context) {
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
    public String getNameFromNumBer(Context context, String phoneNum) {
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


    public void callPeopleByNumber(Context context,String num) {
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

    public void callPeopleByName(Context context,String name){
        //// TODO: 16-8-9
    }


    public   class Contact{
        private String name;
        private int callTimes;
        private List<String> phones;

        public Contact(String name, String callTimes, List<String> phones) {
            this.name = name;
            this.callTimes = Integer.parseInt(callTimes);
            this.phones = phones;
        }

        public void addPhone(String phone){
            if(phones!=null){
                phones.add(phone);
            }
        }

        @Override
        public String toString() {
            return "name:"+name+" phone:"+phones.toString()+" calltimes:"+callTimes;
        }
    }

}

