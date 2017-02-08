package me.yluo.clilauncher.utils;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class ShellUtils {

    //"ping -c 3 -w 100 " + ip
    //String[] commandStr = new String[]{"/system/bin/ls", "-l", "/data" };
    //String[] cmds = new String[]{"/system/bin/cat", "/proc/version"};
    public static String execShell(String[] command) throws Exception {
        String s = TextUtils.join(" ", command);
        return execShell(s);
    }

    public static String execShell(final String command) throws Exception {
        // start the ls command running
        //String[] args =  new String[]{"sh", "-c", command};
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command);
        InputStream inputstream = proc.getInputStream();
        InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
        // read the ls output
        char[] buff = new char[1024];
        StringBuilder sb = new StringBuilder();
        int len = 0;
        while ((len = bufferedreader.read(buff)) != -1) {
            sb.append(buff, 0, len);
        }

        inputstream.close();

        //使用wairFor()可以等待命令执行完成以后才返回
        if (proc.waitFor() != 0) {
            System.err.println("exit value = " + proc.exitValue());
            sb.delete(0, sb.length());
            inputstream = proc.getErrorStream();
            bufferedreader = new BufferedReader(new InputStreamReader(inputstream));
            while ((len = bufferedreader.read(buff)) != -1) {
                sb.append(buff, 0, len);
            }
            inputstream.close();
        }

        return sb.toString();
    }


    /**
     * 执行一个shell命令，并返回字符串值
     * 命令名称&参数组成的数组（例如：{"/system/bin/cat", "/proc/version"}）
     * 命令执行路径（例如："system/bin/"）
     */

    public static synchronized String execShell(String[] cmd, String s) throws IOException {

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
