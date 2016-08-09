package com.yang.mylauncher.suggest;


import android.graphics.Color;

import com.yang.mylauncher.Config;
import com.yang.mylauncher.command.ArgType;

public class SuggestItem {
    public String name;
    public ArgType type;
    public int rate;
    public int color;

    public SuggestItem(String name, ArgType type, int rate) {
        this.name = name;
        this.type = type;
        this.rate = rate;
        switch (type){
            case UNDEFINIED:
            case NORMAL:
            case PLAIN_TEXT:
                this.color = Config.SuggestNormal;
                break;
            case PEOPLE:
                this.color = Config.SuggestPerson;
                break;
            case FILE:
                this.color = Config.SuggestFile;
                break;
            case APPS:
                this.color = Config.SuggestBgApp;
                break;
            case SONG:
                this.color = Config.SuggestSongs;
                break;
            case COMMAND:
                this.color = Config.SuggestCommand;
                break;
        }

    }
}
