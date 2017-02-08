package me.yluo.clilauncher.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


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

    public static String getCacheFileDir(Context context) {
        return context.getCacheDir().toString();
    }

    public static String getFileDir(Context context) {
        return context.getFilesDir().toString();
    }


    //获取可用运存大小 return MB
    public static int getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return (int) (mi.availMem / (1024 * 1024));
    }

    //获取总运存大小 return MB
    public static int getTotalMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return (int) (mi.totalMem / (1024 * 1024));
    }


    //CPU个数
    public static int getNumCores() {
        return Runtime.getRuntime().availableProcessors();
    }


    //获得cpu频率信息
    //[0] min [1]now [2]max 单位MHZ
    public static String[] getCpuFreq(int num) throws Exception {
        String[] result = new String[2];
        try {
            //实时获取CPU当前频率（单位MHZ）：
            String nowFreq = "N/A";
            FileReader fr = new FileReader("/sys/devices/system/cpu/cpu" + num + "/cpufreq/scaling_cur_freq");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            nowFreq = text.trim();
            if (nowFreq.length() > 3) {
                nowFreq = nowFreq.substring(0, nowFreq.length() - 3) + "MHZ";
            }
            result[0] = nowFreq;
        } catch (FileNotFoundException e) {
            result[0] = "N/A";
        }

        try {
            //获得cpu最大频率
            String maxFreq = "N/A";
            FileReader fr2 = new FileReader("/sys/devices/system/cpu/cpu" + num + "/cpufreq/cpuinfo_max_freq");
            BufferedReader br2 = new BufferedReader(fr2);
            String text2 = br2.readLine();
            maxFreq = text2.trim();
            if (maxFreq.length() > 3) {
                maxFreq = maxFreq.substring(0, maxFreq.length() - 3) + "MHZ";
            }
            result[1] = maxFreq;
        } catch (FileNotFoundException e) {
            result[1] = "N/A";
        }

        return result;
    }


    //获得cpuxinxi
    public static String getCpuInfo() {
        StringBuilder builder = new StringBuilder();
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = "";
            while ((text = br.readLine()) != null) {
                builder.append(text).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString().substring(0, builder.length() - 1);
    }

    //获得内存xinxi
    public static String getMenInfo() {
        StringBuilder builder = new StringBuilder();
        try {
            FileReader fr = new FileReader("/proc/meminfo");
            BufferedReader br = new BufferedReader(fr);
            String text = "";
            while ((text = br.readLine()) != null) {
                builder.append(text).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString().substring(0, builder.length() - 1);
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
        romInfo[0] = blockSize * availableBlocks / 1024 / 1024;
        romInfo[1] = blockSize * totalblocks / 1024 / 1024;
        return romInfo;
    }

    //获得SD卡大小
    //[0] left [1] total 单位MB
    public static long[] getSDCardMemory() {
        long[] sdCardInfo = new long[2];
        if (isSDCardEnable()) {
            String state = Environment.getExternalStorageState();
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long bSize = sf.getBlockSizeLong();
            long bCount = sf.getBlockCountLong();
            long availBlocks = sf.getAvailableBlocksLong();
            sdCardInfo[0] = bSize * availBlocks / 1024 / 1024;//可用大小
            sdCardInfo[1] = bSize * bCount / 1024 / 1024;//总大小
        }

        return sdCardInfo;
    }


    //uname -a
    public static String getSysInfo() {
        StringBuilder builder = new StringBuilder();
        try {
            FileReader fr = new FileReader("/proc/version");
            BufferedReader br = new BufferedReader(fr);
            String text = "";
            while ((text = br.readLine()) != null) {
                builder.append(text).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString().substring(0, builder.length() - 1);
    }


    //获得开机时间 单位s
    public static float getBootTime() {
        long ut = SystemClock.elapsedRealtime() / 1000;
        if (ut == 0) {
            ut = 1;
        }

        return ut / 1000.0f;
    }

}

