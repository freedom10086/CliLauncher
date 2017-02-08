package me.yluo.clilauncher.cmd;

import me.yluo.clilauncher.data.ArgType;
import me.yluo.clilauncher.utils.DeviceUtils;

public class cpu extends base {

    @Override
    protected String execCommand() throws Exception{
        String s = DeviceUtils.getCpuInfo()+"\n";
        int num = DeviceUtils.getNumCores();
        s = s+"\n=====cpu usage=====\n";
        for(int i=0;i<num;i++){
            String[] a = DeviceUtils.getCpuFreq(i);
            s = s+"\tcpu:"+i+" "+a[0]+"/"+a[1]+"\n";
        }
        return s;
    }


    @Override
    public int argType(int i) {
        return ArgType.PLANETEXT;
    }

    @Override
    public int[] getArgsNum() {
        return new int[]{0,1};
    }

    @Override
    public String getUsageInfo() {
        return "usage:\nshow cpu info and usage.";
    }


    @Override
    public boolean isAsync(){
        return true;
    }

}
