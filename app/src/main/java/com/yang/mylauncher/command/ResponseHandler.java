package com.yang.mylauncher.command;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;


public abstract class ResponseHandler {

    protected static final int MSG_SUCCESS = 0;
    protected static final int MSG_FAILURE = 1;
    protected static final int MSG_START = 2;
    protected static final int MSG_PROGRESS = 3;

    private Handler handler;
    private Looper looper = null;

    public ResponseHandler(){
        this.looper = (looper == null ? Looper.getMainLooper() : looper);
        handler = new MyHandler(this, this.looper);
    }

    private void handleMessage(Message message){
        String res = (String) message.obj;
        switch (message.what){
            case MSG_SUCCESS:
                onSuccess(res);
                break;
            case MSG_FAILURE:
                onFailure(res);
                break;
            case MSG_PROGRESS:
                onProgress(res);
                break;
            case MSG_START:
                onStart(res);
                break;
        }
    }


    public abstract void onSuccess(String res);
    public abstract void onFailure(String res);
    public void onStart(String info){}
    public void onProgress(String info){}


    final protected void sendMessage(int messageWhat,String res){
        Message m =  Message.obtain(handler, messageWhat,res);
        handler.sendMessage(m);
    }


    /**
     * Avoid leaks by using a non-anonymous handler class.
     */
    final private static class MyHandler extends Handler {
        private final ResponseHandler mResponder;

        MyHandler(ResponseHandler mResponder, Looper looper) {
            super(looper);
            this.mResponder = mResponder;
        }

        @Override
        public void handleMessage(Message msg) {
            mResponder.handleMessage(msg);
        }
    }
}
