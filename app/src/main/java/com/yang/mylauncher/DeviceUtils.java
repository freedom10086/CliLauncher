package com.yang.mylauncher;

import android.app.ActivityManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;



public class DeviceUtils {

    private DeviceUtils() {
        throw new UnsupportedOperationException("u can't fuck me...");
    }

    /**
     * 获取设备MAC地址
     * <p>需添加权限<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
     */
    public static String getMacAddress(Context context) {
        String macAddress;
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        macAddress = info.getMacAddress();
        if (null == macAddress) {
            return "";
        }
        macAddress = macAddress.replace(":", "");
        return macAddress;
    }


    /**
     * 获取设备厂商，如Xiaomi
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取设备型号，如MI2SC
     */
    public static String getModel() {
        String model = Build.MODEL;
        if (model != null) {
            model = model.trim().replaceAll("\\s*", "");
        } else {
            model = "";
        }
        return model;
    }

    /**
     * 获取设备SD卡是否可用
     */
    public static boolean isSDCardEnable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取设备SD卡路径
     * <p>一般是/storage/emulated/0/
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }


    //获取可用运存大小 return MB
    public static int getAvailMemory(Context context){
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return (int) (mi.availMem/(1024*1024));
    }
    //获取总运存大小 return MB
    public static int getTotalMemory(Context context){
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try
        {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }
            initial_memory = Integer.valueOf(arrayOfString[1]) * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (int) (initial_memory/(1024*1024));
    }


    //CPU个数
    public static int getNumCores() {

        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                return Pattern.matches("cpu[0-9]", pathname.getName());
            }
        }
        try {
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles(new CpuFilter());
            return files.length;
        } catch(Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static int getNumCores2(){
        FileReader fr = null;
        int num = 1;
        try {
            fr = new FileReader("/proc/cpufreq/cpufreq_over_max_cpu");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine().trim();
            num =  Integer.parseInt(text);
        } catch (IOException e) {
            e.printStackTrace();
            num = 1;
        }

        return num;
    }


    //获得cpu频率信息
    //[0] min [1]now [2]max 单位MHZ
    public static String[] getCpuFreq(int num){
        String[] result = new String[3];

        ////// 获取CPU最小频率（单位MHZ）：
        String minFreq = "";
        ProcessBuilder cmd;
        try {
            FileReader fr = new FileReader("/sys/devices/system/cpu/cpu"+num+"/cpufreq/cpuinfo_min_freq");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            minFreq = text.trim();
            if(minFreq.length()>3){
                minFreq = minFreq.substring(0,minFreq.length()-3)+"MHZ";
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            minFreq = "N/A";
        }
        result[0] = minFreq.trim();

        //实时获取CPU当前频率（单位MHZ）：
        String nowFreq = "N/A";
        try {
            FileReader fr = new FileReader("/sys/devices/system/cpu/cpu"+num+"/cpufreq/scaling_cur_freq");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            nowFreq = text.trim();
            if(nowFreq.length()>3){
                nowFreq = nowFreq.substring(0,nowFreq.length()-3)+"MHZ";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        result[1] = nowFreq;

        //获得cpu最大频率
        String maxFreq = "N/A";
        try {
            String[] args = { "/system/bin/cat", "/sys/devices/system/cpu/cpu"+num+"/cpufreq/cpuinfo_max_freq" };
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if(line.length()>3){
                maxFreq = line.substring(0,line.length()-3)+"MHZ";
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        result[2] = maxFreq;
        return result;
    }



    //获得cpuxinxi
    public static String getCpuInfo() {
        StringBuilder builder = new StringBuilder();
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = "";
            while ((text = br.readLine())!=null){
                builder.append(text).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString().substring(0,builder.length()-1);
    }

    //获得内存xinxi
    public static String getMenInfo() {
        StringBuilder builder = new StringBuilder();
        try {
            FileReader fr = new FileReader("/proc/meminfo");
            BufferedReader br = new BufferedReader(fr);
            String text = "";
            while ((text = br.readLine())!=null){
                builder.append(text).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString().substring(0,builder.length()-1);
    }

    //获得ROM大小
    //[0] left [1] total 单位MB
    public static long[] getRomSize() {
        long[] romInfo = new long[2];
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        long totalblocks = stat.getBlockCountLong();
        romInfo[0] = blockSize * availableBlocks/1024/1024;
        romInfo[1] = blockSize * totalblocks/1024/1024;
        return romInfo;
    }

    //获得SD卡大小
    //[0] left [1] total 单位MB
    public static long[] getSDCardMemory() {
        long[] sdCardInfo=new long[2];
        if(isSDCardEnable()){
            String state = Environment.getExternalStorageState();
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long bSize = sf.getBlockSizeLong();
            long bCount = sf.getBlockCountLong();
            long availBlocks = sf.getAvailableBlocksLong();
            sdCardInfo[0] = bSize * availBlocks/1024/1024;//可用大小
            sdCardInfo[1] = bSize * bCount/1024/1024;//总大小
        }

        return sdCardInfo;
    }



    //uname -a
    public static String getSysInfo(){
        StringBuilder builder = new StringBuilder();
        try {
            FileReader fr = new FileReader("/proc/version");
            BufferedReader br = new BufferedReader(fr);
            String text = "";
            while ((text = br.readLine())!=null){
                builder.append(text).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString().substring(0,builder.length()-1);
    }


    //获得开机时间 单位s
    public static float getBootTime(){
        long ut = SystemClock.elapsedRealtime() / 1000;
        if (ut == 0) {
            ut = 1;
        }

        return ut/1000.0f;
    }
}

