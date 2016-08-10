package com.yang.mylauncher.cmd;

import android.text.TextUtils;
import android.util.Log;

import com.yang.mylauncher.data.ArgType;
import com.yang.mylauncher.utils.ShellUtils;


public class shell extends base{
    @Override
    protected String execCommand() {
        String commandStr = EXECCONTEXT.commandStr;
        if(EXECCONTEXT.args!=null&&EXECCONTEXT.args.length>0){
            commandStr=commandStr+" "+TextUtils.join(" ",EXECCONTEXT.args);
        }
        try {
            Log.e("shell","commandStr is :"+commandStr);
            return ShellUtils.execShell(commandStr);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @Override
    public ArgType argType(int i) {
        return ArgType.UNDEFINIED;
    }

    @Override
    public int[] getArgsNum() {
        int[] a = new  int[2];
        a[0] = 0;
        a[1] = Integer.MAX_VALUE;
        return a;
    }

    @Override
    public String getUsageInfo() {
        return null;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
