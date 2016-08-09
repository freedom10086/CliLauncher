package com.yang.mylauncher.command.raw;


import com.yang.mylauncher.command.ArgType;
import com.yang.mylauncher.command.ExecContext;

public abstract class base {

    protected ExecContext EXECCONTEXT;
    public final String exec(ExecContext execContext) throws Exception{
        this.EXECCONTEXT = execContext;
        int arglen = (execContext.args==null)?0:execContext.args.length;

        if(arglen>getArgsNum()[1]){
            return "Args number error !!! \ncommand \""+execContext.command+"\" max need "+getArgsNum()[1]+" args!!!";
        }else if(arglen<getArgsNum()[0]){
            return "Args number error !!! \ncommand \""+execContext.command+"\" at least need "+getArgsNum()[0]+" args!!!";
        }
        String s = checkArgsType(execContext.args);
        if(s!=null){
            return s;
        }

        return execCommand();
    }


    protected abstract String execCommand();

    //if ok return null
    protected abstract String checkArgsType(String[] args);

    public abstract ArgType argType(int i);

    public abstract int[] getArgsNum();

    public abstract String getUsageInfo();

    public abstract boolean isAsync();
}
