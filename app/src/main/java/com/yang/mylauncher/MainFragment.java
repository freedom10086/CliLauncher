package com.yang.mylauncher;


import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yang.mylauncher.command.ExecCmdClient;
import com.yang.mylauncher.command.ExecContext;
import com.yang.mylauncher.command.raw.base;
import com.yang.mylauncher.command.CommandFactory;
import com.yang.mylauncher.command.ExecResultHandler;
import com.yang.mylauncher.command.OutPutType;
import com.yang.mylauncher.suggest.SuggestHandler;


public class MainFragment extends Fragment implements EditText.OnEditorActionListener{

    public static final String TAG = "MainFragment";
    private ConsoletextView consoletextView;
    private EditText editText;
    private ScrollView scrollView;
    private LinearLayout sgContainer;
    private ExecContext execContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        consoletextView = (ConsoletextView) v.findViewById(R.id.console_text);
        editText = (EditText) v.findViewById(R.id.ed_input);
        scrollView = (ScrollView) v.findViewById(R.id.scroll_view);
        sgContainer = (LinearLayout) v.findViewById(R.id.suggestions_container);
        editText.setOnEditorActionListener(this);
        new SuggestHandler(getActivity(), editText, sgContainer);
        execContext = new ExecContext(getActivity());
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
            appendText(">>"+e,OutPutType.INPUT);
            base command = CommandFactory.getCommand(e,execContext);
            client.exec(execContext,command,exeHandler);
            editText.setText("");
        }
    }


    private ExecCmdClient client =new ExecCmdClient();
    private ExecResultHandler exeHandler = new ExecResultHandler() {
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
