package com.yang.mylauncher.command.raw;

import com.yang.mylauncher.AppData;
import com.yang.mylauncher.MainActivity;
import com.yang.mylauncher.command.ArgType;

import java.util.List;


public class apps extends base{


    @Override
    protected String execCommand() throws Exception{

        if(EXECCONTEXT.context instanceof MainActivity){
            StringBuilder db  =new StringBuilder();
            List<AppData> apps = ((MainActivity)EXECCONTEXT.context).getApps();
            for (AppData d:apps){
                db.append(d.name).append("\n");
            }

            return db.toString();

        }

        return null;
    }


    @Override
    public ArgType argType(int i) {
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
