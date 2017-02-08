package me.yluo.clilauncher.cmd;

import me.yluo.clilauncher.data.ArgType;
import me.yluo.clilauncher.utils.NetworkUtils;


public class wifi extends base{


    @Override
    protected String execCommand() throws Exception{
        if(EXECCONTEXT.getArgsNum()==0){
            return "wifi is connected : "+NetworkUtils.isWifiConnected(EXECCONTEXT.context);
        }else{
            if(EXECCONTEXT.args[0].equals("on")){
                if(NetworkUtils.isWifiConnected(EXECCONTEXT.context)){
                    return "wifi is already connected do not need open!!";
                }else{
                    NetworkUtils.changeWifiState(EXECCONTEXT.context,true);
                    return "opening wifi.....";
                }

            }else if(EXECCONTEXT.args[0].equals("off")){
                if(!NetworkUtils.isWifiConnected(EXECCONTEXT.context)){
                    return "wifi is not connected do not need close!!";
                }else{
                    NetworkUtils.changeWifiState(EXECCONTEXT.context,false);
                    return "closing wifi.....";
                }
            }
        }

        return getUsageInfo();
    }


    @Override
    public int argType(int i) {
        return ArgType.UNDEFINIED;
    }

    @Override
    public int[] getArgsNum() {
        int[] a =  new int[2];
        a[0] = 0;
        a[1] = 1;
        return a;
    }

    @Override
    public String getUsageInfo() {
        return "usage: \nwifi---show wifi state\nwifi on/off---open or close wifi";
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
