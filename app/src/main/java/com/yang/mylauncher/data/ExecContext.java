package com.yang.mylauncher.data;

import android.content.ContentResolver;
import android.content.Context;

import com.yang.mylauncher.cmd.base;


public class ExecContext {
    public ExecContext(Context context) {
        this.context = context;
        this.resolver = context.getContentResolver();
    }

    public Context context;
    public String currentDir;
    public String[] args;
    public String commandStr;
    public base command;
    public ContentResolver resolver;


    public int getArgsNum(){
        return (args==null)?0:args.length;
    }

    public void reset(){
        this.command = null;
        this.commandStr =null;
        this.args = null;
    }
}
