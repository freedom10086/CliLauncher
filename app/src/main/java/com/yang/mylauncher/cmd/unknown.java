package com.yang.mylauncher.cmd;

import com.yang.mylauncher.data.ArgType;


public class unknown extends base {


    @Override
    protected String execCommand() {
        return "command not found !!!";
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
