package me.yluo.clilauncher.data;

import android.content.ContentResolver;
import android.content.Context;

import me.yluo.clilauncher.cmd.base;


public class ExecContext {
    public ExecContext(Context context) {
        this.context = context;
        this.resolver = context.getContentResolver();
    }

    public String input;
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
