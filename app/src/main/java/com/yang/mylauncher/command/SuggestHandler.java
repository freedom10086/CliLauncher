package com.yang.mylauncher.command;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yang.mylauncher.Config;


public class SuggestHandler implements TextWatcher{

    private EditText editText;
    private LinearLayout suggestContainer;
    private GetSuggestTask task;
    private Context context;

    public SuggestHandler(Context context,EditText editText, LinearLayout suggestContainer) {
        this.editText = editText;
        this.suggestContainer = suggestContainer;
        this.editText.addTextChangedListener(this);
        this.context = context;
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
        task.execute(editText.getText().toString());

    }

    @Override
    public void afterTextChanged(Editable editable) {
    }


    private class GetSuggestTask extends AsyncTask<String,Void,SuggestItem[]>{

        @Override
        protected SuggestItem[] doInBackground(String... strings) {
            Log.e("doing......","======");
            String input =  strings[0];
            SuggestItem[] items;
            if(input.contains(" ")){
                String[] args = input.split(" ");
                int count = args.length;
                items = new SuggestItem[count];
                for(int i=0;i<count;i++){
                    items[i] = new SuggestItem(args[i],ArgType.NORMAL,10);
                }
            }else{
                items = new SuggestItem[1];
                items[0] = new SuggestItem(input,ArgType.NORMAL,5);
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return items;
        }

        @Override
        protected void onPostExecute(SuggestItem[] items) {
            super.onPostExecute(items);
            notifyDataChange(items);
            if(items!=null){
                for (SuggestItem item : items) {
                    Log.e("finish suggest is", item.name);
                }
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.e("cancel","======");
        }
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
}
