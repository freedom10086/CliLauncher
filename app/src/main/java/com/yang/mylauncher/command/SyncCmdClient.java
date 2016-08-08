package com.yang.mylauncher.command;



public  class SyncCmdClient {

    public void execCommand(BaseCommand command,ExecHandler handler){
        if(!checkArgs(command)){
            handler.sendMessage(ExecHandler.MSG_FAILURE,"error",OutPutType.ERROR);
        }

        handler.sendMessage(ExecHandler.MSG_START,"Start",OutPutType.INFO);
        String s = null;
        try {
            s = command.exec("hello");
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendMessage(ExecHandler.MSG_FAILURE,e.getMessage(),OutPutType.ERROR);
        }
        handler.sendMessage(ExecHandler.MSG_SUCCESS,s,OutPutType.NORMAL);
    }


    protected boolean checkArgs(BaseCommand command){

        return true;
    }
}
