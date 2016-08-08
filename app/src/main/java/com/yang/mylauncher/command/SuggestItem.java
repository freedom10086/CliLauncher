package com.yang.mylauncher.command;


public class SuggestItem {
    public String name;
    public ArgType type;
    public int rate;

    public SuggestItem(String name, ArgType type, int rate) {
        this.name = name;
        this.type = type;
        this.rate = rate;
    }
}
