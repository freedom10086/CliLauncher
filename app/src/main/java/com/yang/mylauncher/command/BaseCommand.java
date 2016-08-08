package com.yang.mylauncher.command;

/**
 * Created by yang on 16-8-8.
 */

public interface BaseCommand {
    int NORMAL_COMMAND = 0;
    int FILE_COMMAND = 1;
    int PEOPLE_COMMAND = 2;
    int APP_COMMAND = 3;

    String exex();
    String[] getArgs();
    int[] getArgsCount();
    String getHelpText();
    int getArgsType();

}
