package com.yang.mylauncher.data;

import com.yang.mylauncher.Config;


public  class SuggestItem {
    public String name;
    public int type;
    public int color;
    public boolean clickRun;
    public String classname;

    public SuggestItem(String name, int type,boolean clickRun) {
        this.name = name;
        this.type = type;
        this.clickRun =clickRun;

        switch (type){
            case ArgType.UNDEFINIED:
            case ArgType.PLANETEXT:
                this.color = Config.SuggestNormal;
                break;
            case ArgType.PEOPLE:
                this.color = Config.SuggestPerson;
                break;
            case ArgType.FILE:
                this.color = Config.SuggestFile;
                break;
            case ArgType.APP:
                this.color = Config.SuggestBgApp;
                break;
            case ArgType.SONG:
                this.color = Config.SuggestSongs;
                break;
            case ArgType.COMMAND:
                this.color = Config.SuggestCommand;
                break;
        }

    }

}
