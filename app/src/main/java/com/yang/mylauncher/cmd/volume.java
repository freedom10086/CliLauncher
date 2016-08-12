package com.yang.mylauncher.cmd;

import android.content.Context;
import android.media.AudioManager;

import com.yang.mylauncher.data.ArgType;

public class volume extends base {

    @Override
    protected String execCommand() {
        AudioManager mAudioManager = (AudioManager)EXECCONTEXT.context.getSystemService(Context.AUDIO_SERVICE);
        int valuemax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //当前音量
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//          r.setStreamVolume(AudioManager.STREAM_MUSIC, tempVolume, 0); //tempVolume:音量绝对值
        //以一步步长控制音量的增减，并弹出系统默认音量控制条：
        if(EXECCONTEXT.args==null||EXECCONTEXT.args.length==0){
            return "current volume is "+currentVolume+"/"+valuemax;
        }else{
            String arg = EXECCONTEXT.args[0];
            if(arg.equals("+")||arg.equals("up")){
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,
                        AudioManager.FX_FOCUS_NAVIGATION_UP);
                return "volume + current is:"+mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)+"/"+valuemax;
            }else if(arg.equals("-")||arg.equals("down")){
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,
                        AudioManager.FX_FOCUS_NAVIGATION_UP);
                return "volume - current is:"+mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)+"/"+valuemax;
            }else{
                return getUsageInfo();
            }

        }
    }


    @Override
    public int argType(int i) {
        return ArgType.PLANETEXT;
    }

    @Override
    public int[] getArgsNum() {
        return new int[]{0,1};
    }

    @Override
    public String getUsageInfo() {
        return "usage:volume [+/-] \nshow or setting the volume !!";
    }


    @Override
    public boolean isAsync(){
        return false;
    }

}
