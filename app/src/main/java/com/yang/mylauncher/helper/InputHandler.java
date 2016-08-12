package com.yang.mylauncher.helper;

import android.content.Context;
import android.graphics.Color;
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

import com.yang.mylauncher.cmd.base;
import com.yang.mylauncher.data.ArgType;
import com.yang.mylauncher.data.ExecContext;
import com.yang.mylauncher.data.SuggestItem;

import java.util.ArrayList;
import java.util.List;


/**
 * 处理提示 和 得到命令
 * 根据输入得到 要执行的命令 和 提示命令和提示参数
 */
public class InputHandler implements TextWatcher,EditText.OnEditorActionListener,View.OnClickListener{

    private EditText editText;
    private LinearLayout suggestContainer;
    private GetSuggestTask task;
    private Context context;
    private ExecContext execContext;
    private onEnterClickListener onEnterClickListener;

    private int currentpos = 0;
    private List<String> commadArgs = new ArrayList<>();
    private base currentCmd = null;


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
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        if(task!=null&&task.getStatus()!= AsyncTask.Status.FINISHED){
            task.cancel(true);
        }
        task = new GetSuggestTask();
        String input =  editText.getText().toString();
        if(TextUtils.isEmpty(input.trim())){
            currentCmd = null;
            currentpos = 0;
            commadArgs.clear();
        }else{
            //// TODO: 16-8-11  not need always clear
            commadArgs.clear();
            String[] ca = input.split(" +");
            currentpos = (input.endsWith(" "))?ca.length:ca.length-1;
            for(int i=0;i<ca.length;i++){
                commadArgs.add(ca[i]);
            }
        }

        task.execute();
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
            execContext.command = currentCmd;
            execContext.commandStr = commadArgs.get(0);
            execContext.args = new String[commadArgs.size()-1];
            execContext.input = input;

            for(int j=1;j<commadArgs.size();j++){
                execContext.args[j-1] = commadArgs.get(j);
            }

            if(execContext.command==null){
                new GetCommandTask().execute(input);
            }else{
                onEnterClickListener.onEnterClick(input,execContext);
            }
            handled = true;
        }
        return handled;
    }

    @Override
    public void onClick(View view) {
        if(view instanceof TextView){
            int tag = (int) view.getTag()+1;
            String sgText = ((TextView)view).getText().toString()+" ";
            String edtext  = editText.getText().toString().replaceAll(" +"," ");
            if(!(tag>commadArgs.size())){
                if(TextUtils.isEmpty(edtext.trim())){
                    editText.setText(sgText);
                }else if(edtext.endsWith(" ")){
                    editText.append(sgText);
                }else if(edtext.contains(" ")){
                    int pos = edtext.lastIndexOf(' ');
                    edtext = edtext.substring(0,pos)+" "+sgText;
                    editText.setText(edtext);
                }else{
                    editText.setText(sgText);
                }
                editText.setSelection(editText.length());
            }
            Log.e("click",((int)view.getTag())+" click");
        }

    }


    private class GetCommandTask extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... strings) {
            String input = strings[0];
            execContext.command = CommandUtils.getCommand(context,input.toLowerCase());
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

    private class GetSuggestTask extends AsyncTask<Void,Void,List<SuggestItem>>{
        @Override
        protected List<SuggestItem> doInBackground(Void... voids) {
            List<SuggestItem> items =null;
            if(currentpos==0&&commadArgs.size()==0){
                items = getEmptySuggest();
            }else if(currentpos==0){
                items = CommandUtils.getCommandSuggest(context,commadArgs.get(0));
            }else{
                //输入空格 推断下一个参数
                if(currentpos==commadArgs.size()){
                    if(currentpos==1){
                        currentCmd = CommandUtils.getCommand(context,commadArgs.get(0));
                    }
                    items = getArgsSuggest(null,currentpos);
                }else{
                    items = getArgsSuggest(commadArgs.get(commadArgs.size()-1),currentpos);
                }
            }
            return items;
        }

        @Override
        protected void onPostExecute(List<SuggestItem> items) {
            super.onPostExecute(items);
            notifyDataChange(items);
        }
    }


    private List<SuggestItem> getEmptySuggest(){
        return null;
    }

    //获得参数的建议
    private List<SuggestItem> getArgsSuggest(String inputarg, int pos){

        List<SuggestItem> items = new ArrayList<>();
        items.add(new SuggestItem(pos+"|"+inputarg,ArgType.PLANETEXT,false));
        //// TODO: 16-8-11
        return items;
    }

    private TextView getSingleView(View v,SuggestItem item) {
        TextView textView = (TextView) v;
        if(textView==null){
            textView = new TextView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10,5,10,5);
            textView.setPadding(10,5,10,5);
            textView.setMaxEms(5);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,13);
            textView.setTextColor(Color.BLACK);
            textView.setLayoutParams(params);
            textView.setMaxLines(1);
            textView.setTag(currentpos);
            textView.setOnClickListener(this);
            textView.setClickable(true);
        }
        textView.setBackgroundColor(item.color);
        textView.setText(item.name);
        return textView;
    }

    private void notifyDataChange(List<SuggestItem> items){
        if(suggestContainer==null)
            return;
        int count = (items==null?0:items.size());
        int preCount = suggestContainer.getChildCount();
        //合理的复用之前的textview
        if(preCount>count){
            suggestContainer.removeViews(count,preCount-count);
        }
        for(int i = 0;i<count;i++){
            View  view = suggestContainer.getChildAt(i);
            if(view==null){
                suggestContainer.addView(getSingleView(null,items.get(i)));
            }else{
                getSingleView(view,items.get(i));
            }
        }
    }


    public interface onEnterClickListener{
       void onEnterClick(String input,ExecContext context);
    }
}
