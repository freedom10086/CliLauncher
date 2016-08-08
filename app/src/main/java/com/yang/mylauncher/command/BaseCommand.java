package com.yang.mylauncher.command;


public interface BaseCommand {

    String exec(String input) throws Exception;

    int minArgs();

    int maxArgs();

    ArgType[] argType();

    String geterror();

    String[] getArgs();

    boolean isAsync();
}
