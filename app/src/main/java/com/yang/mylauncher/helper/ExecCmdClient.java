package com.yang.mylauncher.helper;

import com.yang.mylauncher.cmd.base;
import com.yang.mylauncher.data.ExecContext;
import com.yang.mylauncher.data.OutPutType;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class ExecCmdClient {
    private final ExecutorService threadPool;
    private HashMap<String,Future> tasks;

    public ExecCmdClient() {
        tasks = new HashMap<>();
        int cpuNums = Runtime.getRuntime().availableProcessors();
        //获取当前系统的CPU 数目
        //newFixedThreadPool 固定大小//newCachedThreadPool 自动大小
        this.threadPool = Executors.newFixedThreadPool(cpuNums * 2);
    }

    public String exec(final ExecContext state, final ExecResultHandler handler) {
        final base command = state.command;
        if(command==null){
            handler.sendMessage(ExecResultHandler.MSG_FAILURE,"error", OutPutType.ERROR);
            return "error";
        }
        handler.sendMessage(ExecResultHandler.MSG_START,"Start",OutPutType.INFO);

        if(command.isAsync()){
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try { // 同步阻塞获取信号量
                        String res = command.exec(state);
                        handler.sendMessage(ExecResultHandler.MSG_SUCCESS,res,OutPutType.NORMAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        handler.sendMessage(ExecResultHandler.MSG_FAILURE,"canceled...",OutPutType.ERROR);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };

            Future f =  threadPool.submit(runnable);
            String key = Long.toHexString(System.currentTimeMillis());
            tasks.put(key,f);
            return key;
        }else{
            String s = null;
            try {
                s = command.exec(state);
                handler.sendMessage(ExecResultHandler.MSG_SUCCESS,s,OutPutType.NORMAL);
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendMessage(ExecResultHandler.MSG_FAILURE,e.getMessage(),OutPutType.ERROR);
            }
        }

        return null;
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
