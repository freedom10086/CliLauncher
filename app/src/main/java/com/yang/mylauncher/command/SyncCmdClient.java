package com.yang.mylauncher.command;

/**
 * Created by yang on 16-8-8.
 */

public  class SyncCmdClient {

    public void execCommand(BaseCommand command,ResponseHandler handler){
        if(!checkArgs(command)){
            handler.sendMessage(ResponseHandler.MSG_FAILURE,"error");
        }

        handler.sendMessage(ResponseHandler.MSG_START,"Start");
        String s =  command.exex();
        handler.sendMessage(ResponseHandler.MSG_SUCCESS,s);
    }


    protected boolean checkArgs(BaseCommand command){

        return true;
    }
}
