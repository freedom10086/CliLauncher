package com.yang.mylauncher.command;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;


public abstract class ExecResultHandler {

    protected static final int MSG_SUCCESS = 0;
    protected static final int MSG_FAILURE = 1;
    protected static final int MSG_START = 2;
    protected static final int MSG_PROGRESS = 3;

    private Handler handler;
    private Looper looper = null;
    private OutPutType type = OutPutType.NORMAL;

    public ExecResultHandler(){
        this.looper = (looper == null ? Looper.getMainLooper() : looper);
        handler = new MyHandler(this, this.looper);
    }

    private void handleMessage(Message message){
        String res = (String) message.obj;
        switch (message.what){
            case MSG_SUCCESS:
                onSuccess(type,res);
                break;
            case MSG_FAILURE:
                onFailure(type,res);
                break;
            case MSG_PROGRESS:
                onProgress(type,res);
                break;
            case MSG_START:
                onStart(type,res);
                break;
        }
    }


    public abstract void onSuccess(OutPutType type,String res);
    public abstract void onFailure(OutPutType type,String res);
    public void onStart(OutPutType type,String info){}
    public void onProgress(OutPutType type,String info){}


    final protected void sendMessage(int messageWhat,String res,OutPutType type){
        Message m =  Message.obtain(handler, messageWhat,res);
        handler.sendMessage(m);
        this.type = type;
    }


    /**
     * Avoid leaks by using a non-anonymous handler class.
     */
    final private static class MyHandler extends Handler {
        private final ExecResultHandler mResponder;

        MyHandler(ExecResultHandler mResponder, Looper looper) {
            super(looper);
            this.mResponder = mResponder;
        }

        @Override
        public void handleMessage(Message msg) {
            mResponder.handleMessage(msg);
        }
    }
}
