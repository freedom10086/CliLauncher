package com.yang.mylauncher;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yang.mylauncher.command.AsyncCmdClient;
import com.yang.mylauncher.command.BaseCommand;
import com.yang.mylauncher.command.CommandFactory;
import com.yang.mylauncher.command.ExecHandler;
import com.yang.mylauncher.command.OutPutType;
import com.yang.mylauncher.command.SuggestHandler;
import com.yang.mylauncher.command.SuggestItem;


public class MainFragment extends Fragment implements EditText.OnEditorActionListener{

    public static final String TAG = "MainFragment";
    private ConsoletextView consoletextView;
    private EditText editText;
    private ScrollView scrollView;
    private LinearLayout sgContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        consoletextView = (ConsoletextView) v.findViewById(R.id.console_text);
        editText = (EditText) v.findViewById(R.id.ed_input);
        scrollView = (ScrollView) v.findViewById(R.id.scroll_view);
        sgContainer = (LinearLayout) v.findViewById(R.id.suggestions_container);
        editText.setOnEditorActionListener(this);
        new SuggestHandler(getActivity(), editText, sgContainer);
        return v;
    }


    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        boolean handled = false;
        if (i == EditorInfo.IME_ACTION_SEND) {
            handle_input();
            handled = true;
        }
        return handled;
    }

    private void handle_input() {
        String e = editText.getText().toString().trim();
        if (!TextUtils.isEmpty(e)) {
            appendText(e,OutPutType.INPUT);
            BaseCommand command = CommandFactory.getCommand(e);
            client.asyncExec(command,exeHandler);
            editText.setText("");
        }
    }


    private AsyncCmdClient client =new AsyncCmdClient();
    private ExecHandler exeHandler = new ExecHandler() {
        @Override
        public void onSuccess(OutPutType type, String res) {
            appendText(res,type);
        }

        @Override
        public void onFailure(OutPutType type, String res) {
            appendText(res,type);
        }
    };


    private void appendText(String s, OutPutType type){
        consoletextView.append(s,type);
        scrollView.postDelayed(mScrollRunnable,100);
    }

    private Runnable mScrollRunnable = new Runnable() {
        @Override
        public void run() {
            boolean inputHadFocus = editText.hasFocus();
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            if (inputHadFocus)
                editText.requestFocus();
        }
    };




}
