package com.yang.mylauncher.command;

import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;


public abstract class SuggestHandler implements TextWatcher{

    private EditText editText;
    private GetSuggestTask task;

    public void setEditText(EditText editText) {
        this.editText = editText;
        this.editText.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        Log.e("suggest before",charSequence.toString());
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int connt) {
        Log.e("suggest on",charSequence.toString());

        if(task!=null&&task.getStatus()!= AsyncTask.Status.FINISHED){
            task.cancel(true);
        }
        task = new GetSuggestTask();
        task.execute(editText.getText().toString());

    }

    @Override
    public void afterTextChanged(Editable editable) {
        Log.e("suggest after",editable.toString());
    }


    public abstract void  OnGetSuggests(SuggestItem[] items);

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
            OnGetSuggests(items);
            if(items!=null){
                for (int i=0;i<items.length;i++){
                    Log.e("finish suggest is",items[i].name);
                }
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.e("cancel","======");
        }
    }
}
