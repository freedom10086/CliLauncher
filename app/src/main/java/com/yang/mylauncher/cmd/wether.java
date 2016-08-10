package com.yang.mylauncher.cmd;

import com.yang.mylauncher.data.ArgType;
import com.yang.mylauncher.utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;


public class wether extends base{

    private static final String WETHER_NOW_URL = "https://api.thinkpage.cn/v3/weather/now.json?key=swqqluelawaa42jk&location=ip&language=zh-Hans&unit=c";
    private static final String WETHER_CUS = "https://api.thinkpage.cn/v3/weather/now.json?key=swqqluelawaa42jk&language=zh-Hans&unit=c&location=";

    @Override
    protected String execCommand() throws Exception{
        String s = "";
        if(EXECCONTEXT.getArgsNum()==1){
            s =  HttpUtils.get(WETHER_CUS+EXECCONTEXT.args[0]);
        }else{
            s =  HttpUtils.get(WETHER_NOW_URL);
        }
        if(s!=null){
            StringBuilder sb = new StringBuilder();
            JSONObject object = new JSONObject(s);
            if(object.has("results")){
                JSONArray array = object.getJSONArray("results");

                for(int i=0;i<array.length();i++){
                    JSONObject b = array.getJSONObject(i);
                    JSONObject bb = b.getJSONObject("location");
                    sb.append("location:").append(bb.getString("name")).append("\n");
                    sb.append("country:").append(bb.getString("country")).append("\n\n");

                    JSONObject obj = b.getJSONObject("now");
                    Iterator it = obj.keys();
                    while (it.hasNext()) {
                        String key = (String) it.next();
                        String value = obj.getString(key);
                        sb.append(key).append(":").append(value).append("\n");
                    }
                    sb.append("\n");
                    sb.append("last_update:").append(b.getString("last_update")).append("\n");

                }
            }

            return sb.toString();
        }
        return null;
    }

    @Override
    public ArgType argType(int i) {
        return ArgType.UNDEFINIED;
    }

    @Override
    public int[] getArgsNum() {
        int[] a = new int[2];
        a[0] = 0;
        a[1] = 1;
        return a;
    }

    @Override
    public String getUsageInfo() {
        return null;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
