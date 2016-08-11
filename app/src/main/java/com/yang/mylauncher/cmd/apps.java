package com.yang.mylauncher.cmd;

import android.database.Cursor;

import com.yang.mylauncher.SuggestProvider;
import com.yang.mylauncher.data.ArgType;


public class apps extends base{


    @Override
    protected String execCommand() throws Exception{
        StringBuilder db  =new StringBuilder();
        String[] colums = new String[]{SuggestProvider.DISPLAY_NAME};
        String selections = SuggestProvider.TYPE+" = ?";
        String[] seleArgs = new String[]{String.valueOf(ArgType.APP)};

        Cursor cur = EXECCONTEXT.resolver.query(SuggestProvider.URI,colums,selections,seleArgs,null);
        if(cur!=null){
            while (cur.moveToNext()){
                db.append(cur.getString(cur.getColumnIndex(SuggestProvider.DISPLAY_NAME))).append("\n");
            }
            cur.close();
        }

        return db.toString();
    }


    @Override
    public int argType(int i) {
        return ArgType.UNDEFINIED;
    }

    @Override
    public int[] getArgsNum() {
        return new int[2];
    }

    @Override
    public String getUsageInfo() {
        return null;
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
