package com.yang.mylauncher.command.raw;

import com.yang.mylauncher.command.ArgType;
import com.yang.mylauncher.command.BaseCommand;
import com.yang.mylauncher.utils.ShellUtils;

/**
 * Created by yang on 16-8-8.
 */

public class ifconfig implements BaseCommand {

    @Override
    public String exec(String input) throws Exception {
        return ShellUtils.execShell("netcfg");
    }

    @Override
    public int minArgs() {
        return 0;
    }

    @Override
    public int maxArgs() {
        return 0;
    }

    @Override
    public ArgType[] argType() {
        return new ArgType[0];
    }

    @Override
    public String geterror() {
        return null;
    }

    @Override
    public String[] getArgs() {
        return new String[0];
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
