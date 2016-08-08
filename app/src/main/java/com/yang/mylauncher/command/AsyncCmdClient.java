package com.yang.mylauncher.command;

import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class AsyncCmdClient extends SyncCmdClient{
    private final ExecutorService threadPool;
    private HashMap<String,Future> tasks;

    public AsyncCmdClient() {
        tasks = new HashMap<>();
        int cpuNums = Runtime.getRuntime().availableProcessors();
        //获取当前系统的CPU 数目
        //ExecutorService通常根据系统资源情况灵活定义线程池大小
        //newFixedThreadPool 固定大小
        //newCachedThreadPool 自动大小
        this.threadPool = Executors.newFixedThreadPool(cpuNums * 2);
    }


    public String asyncExec(final BaseCommand command, final ExecHandler handler) {
        if(command==null||!super.checkArgs(command)){
            handler.sendMessage(ExecHandler.MSG_FAILURE,"error",OutPutType.ERROR);
            return "error";
        }

        handler.sendMessage(ExecHandler.MSG_START,"Start",OutPutType.INFO);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try { // 同步阻塞获取信号量
                    for(int i =0;i<10;i++){
                        Log.e("thread","runing....."+i);
                        Thread.sleep(1000);
                    }
                    String res = command.exec("");
                    handler.sendMessage(ExecHandler.MSG_SUCCESS,res,OutPutType.NORMAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    handler.sendMessage(ExecHandler.MSG_FAILURE,"canceled...",OutPutType.ERROR);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        Future f =  threadPool.submit(runnable);
        String key = Long.toHexString(System.currentTimeMillis());
        tasks.put(key,f);
        return key;
    }

    public boolean cancel(String k){
        if(tasks.containsKey(k)){
            Future f = tasks.get(k);
            if(f==null){
                return false;
            }
            if (!f.isDone()&&!f.isCancelled()) {
                f.cancel(true);
                tasks.remove(k);
                return true;
            }
        }
        return false;
    }
}
