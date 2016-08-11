package com.yang.mylauncher.cmd;

import com.yang.mylauncher.data.ArgType;


public class people extends base {


    @Override
    protected String execCommand() {

        return "people";
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
    public boolean isAsync(){
        return false;
    }

}
