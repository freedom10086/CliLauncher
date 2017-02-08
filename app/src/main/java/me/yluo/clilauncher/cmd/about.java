package me.yluo.clilauncher.cmd;

import me.yluo.clilauncher.data.ArgType;


public class about extends base {


    @Override
    protected String execCommand() {
        return "yluo launcher version 1.0\nthis is a linux shell like launcher!";
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
