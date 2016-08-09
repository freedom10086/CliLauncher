package com.yang.mylauncher.command.raw;
import com.yang.mylauncher.command.ArgType;
import com.yang.mylauncher.command.ExecContext;


public class about extends base {


    @Override
    protected String execCommand() {
        return "yluo launcher version 1.0\nthis is a linux shell like launcher!";
    }

    @Override
    protected String checkArgsType(String[] args) {
        return null;
    }


    @Override
    public ArgType argType(int i) {
        return null;
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
    public boolean isAsync(){
        return false;
    }

}
