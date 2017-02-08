package me.yluo.clilauncher.helper;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.yluo.clilauncher.SuggestProvider;
import me.yluo.clilauncher.cmd.base;
import me.yluo.clilauncher.cmd.unknown;
import me.yluo.clilauncher.data.SuggestItem;


public class CommandUtils {

    //根据输入获得的命令
    public static base getCommand(Context context,String commandStr){
        Log.e("getcmd","==="+commandStr+"===");

        base cmdreturn = new unknown();
        List<SuggestItem> suggests = getCommandSuggest(context,commandStr);
        int len = suggests.size();
        Log.e("sg len","====="+len+"=====");
        if(len==1){
            SuggestItem i = suggests.get(0);
            String className = i.classname;
            int type = i.type;
            Log.e("classname",className);
            cmdreturn =  getCmdByClassNmae(className);
        }else if(len>1){
            //len>1
            for(SuggestItem sg:suggests){
                if(sg.name.equals(commandStr)){
                    String claname = sg.classname;
                    cmdreturn = getCmdByClassNmae(claname);
                }
            }
            if(cmdreturn instanceof unknown){
                cmdreturn = getCmdByClassNmae(suggests.get(0).classname);
            }
        }

        return cmdreturn;
    }

    //获得命令的建议
    public static List<SuggestItem> getCommandSuggest(Context context,String cmdStr){
        Log.e("start get sg","==="+cmdStr+"===");
        List<SuggestItem> cmdsuggests = new ArrayList<>();
        String[] colums = new String[]{
                SuggestProvider.DISPLAY_NAME,
                SuggestProvider.TYPE,
                SuggestProvider.CMD_CLASS_NAME,
                SuggestProvider.CMD_CLICK_RUN
        };
        String selections = SuggestProvider.SEARCH_NAME +" LIKE ?";
        String[] selectargs = new String[]{"%"+cmdStr+"%"};
        String order = SuggestProvider.USE_TIME +" DESC, "+SuggestProvider.USE_COUNT +" DESC";
        Cursor cur =  context.getContentResolver().query(SuggestProvider.URI,colums, selections,selectargs,order);
        if(cur!=null){
            while (cur.moveToNext()){
                String diaplay_name = cur.getString(cur.getColumnIndex(SuggestProvider.DISPLAY_NAME));
                int type =  cur.getInt(cur.getColumnIndex(SuggestProvider.TYPE));
                boolean runable = !(cur.getInt(cur.getColumnIndex(SuggestProvider.CMD_CLICK_RUN))==0);
                SuggestItem item = new SuggestItem(diaplay_name,type,runable);
                item.classname = cur.getString(cur.getColumnIndex(SuggestProvider.CMD_CLASS_NAME));
                cmdsuggests.add(item);
            }
            cur.close();
        }
        return cmdsuggests;
    }



    private static base getCmdByClassNmae(String classname){
        if(TextUtils.isEmpty(classname)){
            return new unknown();
        }
        final String name = base.class.getPackage().getName()+"."+classname;
        try {
            return (base) Class.forName(name).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return new unknown();
        }
    }
}
