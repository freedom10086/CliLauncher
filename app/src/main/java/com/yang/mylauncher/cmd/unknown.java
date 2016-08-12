package com.yang.mylauncher.cmd;

import android.text.TextUtils;

import com.yang.mylauncher.data.ArgType;
import com.yang.mylauncher.utils.DeviceUtils;
import com.yang.mylauncher.utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class unknown extends base {

    private static final String url = "http://www.tuling123.com/openapi/api";
    private static final String key = "84b8e2b92a49f187cffbd27e826e7a02";

    @Override
    protected String execCommand() throws Exception{
        String mac =  DeviceUtils.getMacAddress(EXECCONTEXT.context);
        Map<String,String> params = new HashMap<>();
        params.put("key",key);
        params.put("info",EXECCONTEXT.input);
        params.put("userid",mac);
        StringBuilder finalstr = new StringBuilder();
        String res =  HttpUtils.post(url,params);
        JSONObject root = new JSONObject(res);
        //文本信息里面可能有list 等
        String text = root.getString("text");
        finalstr.append(text);
        if(root.has("list")){
            finalstr.append(":\n");
            JSONArray array = root.getJSONArray("list");
            for (int i = 0;i < array.length();i ++){
                finalstr.append("\n");
                JSONObject b = array.getJSONObject(i);
                Iterator it = b.keys();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    String value = b.getString(key);
                    if(!TextUtils.isEmpty(value)){
                        finalstr.append("\t").append(key).append(":").append(value).append("\n");
                    }
                }
//
            }
        }
        return finalstr.toString();
    }


    @Override
    public int argType(int i) {
        return ArgType.PLANETEXT;
    }

    @Override
    public int[] getArgsNum() {
        return new int[]{0,Integer.MAX_VALUE};
    }

    @Override
    public String getUsageInfo() {
        return null;
    }


    @Override
    public boolean isAsync(){
        return true;
    }

}
