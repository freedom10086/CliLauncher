package com.yang.mylauncher.helper;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yang.mylauncher.Config;
import com.yang.mylauncher.SuggestProvider;
import com.yang.mylauncher.cmd.base;
import com.yang.mylauncher.data.ArgType;
import com.yang.mylauncher.data.ExecContext;
import com.yang.mylauncher.data.SuggestItem;


/**
 * 处理提示 和 得到命令
 * 根据输入得到 要执行的命令 和 提示命令和提示参数
 */
public class InputHandler implements TextWatcher,EditText.OnEditorActionListener{

    private EditText editText;
    private LinearLayout suggestContainer;
    private GetSuggestTask task;
    private Context context;
    private ExecContext execContext;
    private boolean canCancle = true;
    private onEnterClickListener onEnterClickListener;

    public InputHandler(ExecContext execContext,
                        EditText editText, LinearLayout suggestContainer) {
        this.editText = editText;
        this.suggestContainer = suggestContainer;
        this.editText.addTextChangedListener(this);
        this.editText.setOnEditorActionListener(this);
        this.context = execContext.context;
        this.execContext = execContext;
    }


    public void setOnEnterClickListener(InputHandler.onEnterClickListener onEnterClickListener) {
        this.onEnterClickListener = onEnterClickListener;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int connt) {
        if(task!=null&&task.getStatus()!= AsyncTask.Status.FINISHED){
            task.cancel(true);
        }
        task = new GetSuggestTask();
        String input =  editText.getText().toString();
        task.execute(input);
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }


    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        boolean handled = false;
        String input = editText.getText().toString().trim();
        if (i == EditorInfo.IME_ACTION_SEND
                &&onEnterClickListener!=null
                &&!TextUtils.isEmpty(input)) {
            if(execContext.command==null){
                new GetCommandTask().execute(input);
            }else{
                onEnterClickListener.onEnterClick(input,execContext);
            }
            handled = true;
        }
        return handled;
    }


    private class GetCommandTask extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            String input = strings[0];
            execContext.command = getCommand(input.toLowerCase());
            execContext.commandStr = input.trim().toLowerCase();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            onEnterClickListener.onEnterClick(
                    editText.getText().toString().trim(),execContext);
        }
    }


    private String recordStr = null;
    private class GetSuggestTask extends AsyncTask<String,Void,SuggestItem[]>{

        @Override
        protected void onCancelled() {
            if(canCancle){
                super.onCancelled();
                Log.e("cancle","===== task cancel=====");
            }
            Log.e("cancle","===== get command task can not cancel=====");
        }

        @Override
        protected SuggestItem[] doInBackground(String... strings) {
            String input =  strings[0];
            SuggestItem[] items = null;
            String[] args = input.trim().split(" +");
            String trimInput = input.trim().toLowerCase();
            int len = args.length;
            execContext.args = null;

            if(input.trim().isEmpty()){
                items = getEmptyCommandSuggest();
            }else if(len==1){
                if(recordStr==null||!recordStr.equals(trimInput)){
                    recordStr = trimInput;
                    if(input.endsWith(" ")){
                        canCancle = false;
                        execContext.command = getCommand(trimInput);
                        execContext.commandStr = trimInput;
                        canCancle = true;
                        items = getArgsSuggest(1);
                    }else{
                        recordStr = null;
                        execContext.command = null;
                        execContext.commandStr = null;
                        items = getCommandSuggest(input);
                    }
                }
            }else{// len >1
                String[] argss = new String[len-1];
                System.arraycopy(args, 1, argss, 0, len - 1);
                int pos =input.endsWith(" ")?len:len-1;
                execContext.args = argss;
                items = getArgsSuggest(pos);
            }
            return items;
        }


        @Override
        protected void onPostExecute(SuggestItem[] items) {
            super.onPostExecute(items);
            notifyDataChange(items);
        }
    }

    //根据输入获得的命令
    private base getCommand(String commandStr){
        try {
            base cmd = (base) Class.forName(getCommandClassName(commandStr))
                    .newInstance();
            Log.e("getCommand","get command"+commandStr);
            return cmd;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获得命令的建议
    private SuggestItem[] getCommandSuggest(String commandinput){
        //从数据库获得命令
        SuggestItem[] items = null;
        String[] colums = new String[]{
                SuggestProvider.COLUM_ID,
                SuggestProvider.COLUM_USE_COUNT,
                SuggestProvider.COLUM_NAME,
                SuggestProvider.COLUM_TYPE,
                SuggestProvider.COLUM_DATA};
        String selections = SuggestProvider.COLUM_NAME +" LIKE ?";

        String[] selectargs = new String[]{"%" +commandinput+"%"};

        String order = SuggestProvider.COLUM_USE_TIME+" DESC, "+SuggestProvider.COLUM_USE_COUNT+" DESC";

        Cursor cur =  context.getContentResolver().query(
                SuggestProvider.CONTENT_URI,
                colums,
                selections,
                selectargs,
                order);
        if(cur!=null){
            items = new SuggestItem[cur.getCount()];
            while (cur.moveToNext()){
                String name = cur.getString(cur.getColumnIndex(SuggestProvider.COLUM_NAME));
                int type =  cur.getInt(cur.getColumnIndex(SuggestProvider.COLUM_TYPE));
                String data = cur.getString(cur.getColumnIndex(SuggestProvider.COLUM_DATA));
                int usecount = cur.getInt(cur.getColumnIndex(SuggestProvider.COLUM_USE_COUNT));
                items[cur.getPosition()] = new SuggestItem(name, ArgType.NORMAL,usecount);
            }
            cur.close();
        }
        return items;
    }

    //获得空输入的建议
    private SuggestItem[] getEmptyCommandSuggest(){
        //从数据库获得命令
        SuggestItem[] items = null;
        String[] colums = new String[]{
                SuggestProvider.COLUM_ID,
                SuggestProvider.COLUM_USE_COUNT,
                SuggestProvider.COLUM_NAME,
                SuggestProvider.COLUM_TYPE,
                SuggestProvider.COLUM_DATA};

        String order = SuggestProvider.COLUM_USE_TIME+" DESC, "+SuggestProvider.COLUM_USE_COUNT+" DESC";
        Cursor cur =  context.getContentResolver().query(SuggestProvider.CONTENT_URI, colums, null, null, order);
        if(cur!=null){
            items = new SuggestItem[cur.getCount()];
            while (cur.moveToNext()){
                String name = cur.getString(cur.getColumnIndex(SuggestProvider.COLUM_NAME));
                int type =  cur.getInt(cur.getColumnIndex(SuggestProvider.COLUM_TYPE));
                String data = cur.getString(cur.getColumnIndex(SuggestProvider.COLUM_DATA));
                int usecount = cur.getInt(cur.getColumnIndex(SuggestProvider.COLUM_USE_COUNT));
                items[cur.getPosition()] = new SuggestItem(name, ArgType.NORMAL,usecount);
                Log.e("db",name+"  "+type+"  "+data);
            }
            cur.close();
        }
        return items;
    }

    //获得参数的建议
    private SuggestItem[] getArgsSuggest(int pos){

        //// TODO: 16-8-10
        return new SuggestItem[]{new SuggestItem("pos"+pos,ArgType.NORMAL,0)};
    }

    private TextView getSingleView(View v, SuggestItem item) {
        TextView textView = (TextView) v;
        if(textView==null){
            textView = new TextView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10,5,10,5);
            textView.setPadding(10,5,10,5);
            textView.setMaxEms(8);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,13);
            textView.setTextColor(Config.colorWhite);
            textView.setLayoutParams(params);
            textView.setMaxLines(1);
        }
        textView.setBackgroundColor(item.color);
        textView.setText(item.name);
        return textView;
    }

    private void notifyDataChange(SuggestItem[] items){
        if(suggestContainer==null)
            return;
        int count = (items==null?0:items.length);
        int preCount = suggestContainer.getChildCount();
        //合理的复用之前的textview
        if(preCount>count){
            suggestContainer.removeViews(count,preCount-count);
        }
        for(int i = 0;i<count;i++){
            View  view = suggestContainer.getChildAt(i);
            if(view==null){
                suggestContainer.addView(getSingleView(null,items[i]));
            }else{
                getSingleView(view,items[i]);
            }
        }
    }


    private static String getCommandClassName(String s){
        final String PACKNAME = base.class.getPackage().getName();
        if(s.equals("ls")||s.equals("netcfg")||s.equals("ping")){
            return PACKNAME+".shell";
        }
        return PACKNAME+"."+s;
    }

    public interface onEnterClickListener{
       void onEnterClick(String input,ExecContext context);
    }
}
