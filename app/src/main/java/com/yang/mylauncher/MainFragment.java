package com.yang.mylauncher;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.yang.mylauncher.data.ExecContext;
import com.yang.mylauncher.data.OutPutType;
import com.yang.mylauncher.helper.ExecCmdClient;
import com.yang.mylauncher.helper.ExecResultHandler;
import com.yang.mylauncher.helper.InputHandler;
import com.yang.mylauncher.view.ConsoletextView;


public class MainFragment extends Fragment implements InputHandler.onEnterClickListener {

    public static final String TAG = "MainFragment";
    private ConsoletextView consoletextView;
    private EditText editText;
    private ScrollView scrollView;
    private LinearLayout sgContainer;
    private ExecContext execContext;
    private InputHandler inputHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        consoletextView = (ConsoletextView) v.findViewById(R.id.console_text);
        editText = (EditText) v.findViewById(R.id.ed_input);
        scrollView = (ScrollView) v.findViewById(R.id.scroll_view);
        sgContainer = (LinearLayout) v.findViewById(R.id.suggestions_container);
        execContext = new ExecContext(getActivity());
        inputHandler =  new InputHandler(execContext, editText, sgContainer);
        inputHandler.setOnEnterClickListener(this);
        return v;
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


    //发送命令事件
    @Override
    public void onEnterClick(String input,ExecContext context) {
        appendText(">>"+input,OutPutType.INPUT);
        client.exec(context,exeHandler);
        editText.setText("");
    }
}
