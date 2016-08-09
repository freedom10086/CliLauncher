package com.yang.mylauncher;


import android.content.Context;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.yang.mylauncher.command.OutPutType;

public class ConsoletextView extends TextView{

    private Context context;
    private SpannableStringBuilder strBuilder;

    public ConsoletextView(Context context) {
        super(context);
        init(context);
    }

    public ConsoletextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        strBuilder = new SpannableStringBuilder();
        this.context = context;
        setTextSize(TypedValue.COMPLEX_UNIT_SP,13);
    }


    public void append(String input, OutPutType type){
        String str = "\n"+input;
        int start = strBuilder.length();
        strBuilder.append(str);
        int end = start+str.length();
        //设置字体前景色
        int resid = R.color.colorWhite;
        switch (type){
            case NORMAL:
                resid = R.color.colorWhite;
                break;
            case INFO:
                resid = R.color.colorInfo;
                break;
            case INPUT:
                resid = R.color.colorInput;
                break;
            case WORNING:
                resid = R.color.colorWarning;
                break;
            case ERROR:
                resid = R.color.colorError;
                break;
        }
        int color = getColorWrapper(context,resid);
        strBuilder.setSpan(new ForegroundColorSpan(color), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        strBuilder.setSpan(new TypefaceSpan("monospace"), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        setText(strBuilder);
    }


    private int getColorWrapper(Context context, int id) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(id);
        } else {
            //noinspection deprecation
            return context.getResources().getColor(id);
        }
    }

}
