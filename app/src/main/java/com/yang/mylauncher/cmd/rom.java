package com.yang.mylauncher.cmd;

import com.yang.mylauncher.data.ArgType;
import com.yang.mylauncher.utils.DeviceUtils;


public class rom extends base{


    @Override
    protected String execCommand() throws Exception{
        String s = "";
        long[] roms = DeviceUtils.getRomSize();
        s ="\t\t=====ROM=====\n";
        s += "rom available:\t"+roms[0]+"MB\t\t"+(roms[0]/1024.0f)+"GB\n";
        s += "rom total:\t"+roms[1]+"MB\t\t"+(roms[1]/1024.0f)+"GB\n";

        if(DeviceUtils.isSDCardEnable()){
            s+="\n\t\t=====SD CARD=====\n";
            s+= "SD CARD PATH : "+DeviceUtils.getSDCardPath()+"\n";
            long[] sds = DeviceUtils.getSDCardMemory();
            s += "SD CARD available : "+sds[0]+"MB\n";
            s += "SD CARD total : "+sds[1]+"MB\n";
        }
        return s;
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
        return true;
    }
}
