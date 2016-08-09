package com.yang.mylauncher.command;

import android.content.Context;


public class ExecContext {
    public ExecContext(Context context) {
        this.context = context;
    }

    public Context context;
    public String currentDir;
    public String[] args;
    public String command;


    public int getArgsNum(){
        return (args==null)?0:args.length;
    }
}
