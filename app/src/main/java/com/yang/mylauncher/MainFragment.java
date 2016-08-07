package com.yang.mylauncher;


import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;


public class MainFragment extends Fragment {

    public static final String TAG = "MainFragment";
    private ConsoletextView consoletextView;
    private EditText editText;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("MainFragment","oncreateView");
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        consoletextView = (ConsoletextView) v.findViewById(R.id.console_text);
        editText = (EditText) v.findViewById(R.id.ed_input);
        String meminfo = DeviceUtils.getMenInfo();
        consoletextView.append(meminfo,R.color.colorWhite);
        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_SEND) {
                    handle_input();
                    handled = true;
                }
                return handled;
            }
        });
        return v;
    }

    private void handle_input(){
        String e =  editText.getText().toString().trim();
        if(!TextUtils.isEmpty(e)){
            consoletextView.append("\n>>"+e,R.color.colorInput);
            editText.setText("");
        }
    }

}
