package com.yang.mylauncher.command;


import android.text.TextUtils;
import android.util.Log;

import com.yang.mylauncher.command.raw.base;

public class CommandFactory {
    private static final String PACKNAME = base.class.getPackage().getName();

    public static base getCommand(String input,ExecContext context){
        String[] commandAndArgs =  input.split(" +");
        String command = commandAndArgs[0].toLowerCase();
        context.command = command;

        int argslen = commandAndArgs.length-1;
        if(argslen>0){
            context.args = new String[argslen];
            for(int i = 0;i<argslen;i++){
                context.args[i] = commandAndArgs[i+1];
            }
            Log.e("args", TextUtils.join(" ",commandAndArgs));
            Log.e("args", TextUtils.join(" ",context.args));
        }else{
            context.args = null;
        }
        if(checkCommand(command)){
            try {
                base cmd = (base) Class.forName(getCommandClassName(command))
                        .newInstance();
                return cmd;
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private static boolean checkCommand(String s){
        return true;
    }

    private static String getCommandClassName(String s){
        if(s.equals("ls")||s.equals("netcfg")||s.equals("ping")){
            return PACKNAME+".shell";
        }
        return PACKNAME+"."+s;
    }

}
