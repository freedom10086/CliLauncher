package com.yang.mylauncher.utils;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class ShellUtils {

    //"ping -c 3 -w 100 " + ip
    //String[] command = new String[]{"/system/bin/ls", "-l", "/data" };
    //String[] cmds = new String[]{"/system/bin/cat", "/proc/version"};
    public static String execShell(String[] command) throws Exception{
        String s = TextUtils.join(" ",command);
        return execShell(s);
    }

    public static String execShell(final String command) throws Exception{
        Process p = null;
        p = Runtime.getRuntime().exec(command);
        int status = p.waitFor();
        InputStream input = p.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        StringBuilder buffer = new StringBuilder();
        String line = "";
        while ((line = in.readLine()) != null){
            buffer.append(line).append("\n");
        }

        if (status == 0) {
            return buffer.toString();
        } else {
            return "exec "+command+" faild !!";
        }
    }


    /**

     * 执行一个shell命令，并返回字符串值
     * 命令名称&参数组成的数组（例如：{"/system/bin/cat", "/proc/version"}）
     * 命令执行路径（例如："system/bin/"）
     */

    public static synchronized String execShell(String[] cmd,String s) throws IOException {

        StringBuilder result = new StringBuilder();
        ProcessBuilder builder = new ProcessBuilder(cmd);
        InputStream in = null;
        // 合并标准错误和标准输出
        builder.redirectErrorStream(true);
        // 启动一个新进程
        Process process = builder.start();
        // 读取进程标准输出流
        in = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line = "";
        while ((line = reader.readLine()) != null) {
            result.append(line);
            result.append('\n');
        }
        in.close();
        reader.close();
        return result.toString();
    }


}
