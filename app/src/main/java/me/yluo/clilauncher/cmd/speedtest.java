package me.yluo.clilauncher.cmd;


import java.io.File;

import me.yluo.clilauncher.data.ArgType;
import me.yluo.clilauncher.utils.DeviceUtils;
import me.yluo.clilauncher.utils.ShellUtils;

/**
 * 硬盘测速
 * 1/dev/null：回收站、无底洞。
 * 2/dev/zero：产生字符。
 * 读测试 dd if=filepath of=/dev/null bs=4k
 * 写测试 dd if=/dev/zero of=filepath bs=4k count=100000
 * 读写   dd if=/dev/sdb of=/dev/null bs=4k
 * todo have bug 返回空
 */
public class speedtest extends base {

    @Override
    protected String execCommand() throws Exception {
        String arg = EXECCONTEXT.args[0];
        String tsetFile = DeviceUtils.getCacheFileDir(EXECCONTEXT.context) + "/test.tmp";
        File file = new File(tsetFile);
        if (file.exists()) {
            System.out.println("file is " + file.length());
            file.delete();
        }

        file.createNewFile();

        System.out.println("file is " + file.toString());

        if (arg.equals("r")) {
            return DeviceUtils.getFileDir(EXECCONTEXT.context);
        } else if (arg.equals("w")) {
            //写 40m
            String shell = "dd if=/dev/zero of=" + tsetFile + " bs=4k count=100";
            return ShellUtils.execShell(shell);
        } else {
            return getUsageInfo();
        }
    }


    @Override
    public int argType(int i) {
        return ArgType.PLANETEXT;
    }

    @Override
    public int[] getArgsNum() {
        return new int[]{1, 1};
    }

    @Override
    public String getUsageInfo() {
        return "usage:speedtest [r/w] \ntest the disk read or write speed !!";
    }


    @Override
    public boolean isAsync() {
        return true;
    }
}
