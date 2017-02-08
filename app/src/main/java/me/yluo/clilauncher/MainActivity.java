package me.yluo.clilauncher;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.yluo.clilauncher.data.AppData;
import me.yluo.clilauncher.data.ExecContext;
import me.yluo.clilauncher.data.OutPutType;
import me.yluo.clilauncher.helper.ExecCmdClient;
import me.yluo.clilauncher.helper.ExecResultHandler;
import me.yluo.clilauncher.helper.InputHandler;
import me.yluo.clilauncher.helper.LoadDbUtil;
import me.yluo.clilauncher.utils.DeviceUtils;
import me.yluo.clilauncher.utils.NetworkUtils;
import me.yluo.clilauncher.utils.PinyinUtil;

public class MainActivity extends Activity implements InputHandler.onEnterClickListener{

    public List<AppData> apps =new ArrayList<>();
    private ConsoletextView consoletextView;
    private EditText editText_input;
    private ScrollView scrollView;
    private LinearLayout sgContainer;
    private ExecContext execContext;
    private InputHandler inputHandler;
    private ExecCmdClient client =new ExecCmdClient();
    private View main_view,apps_view;
    private MyAppListAdapter adapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView info_text1 = (TextView) findViewById(R.id.info_1);
        TextView info_text2 = (TextView) findViewById(R.id.info_2);
        main_view = findViewById(R.id.content_main);
        apps_view = findViewById(R.id.content_app_list);
        consoletextView = (ConsoletextView)findViewById(R.id.console_text);
        editText_input = (EditText)findViewById(R.id.ed_input);
        scrollView = (ScrollView)findViewById(R.id.scroll_view);
        sgContainer = (LinearLayout)findViewById(R.id.suggestions_container);
        execContext = new ExecContext(this);
        inputHandler =  new InputHandler(execContext, editText_input, sgContainer);
        inputHandler.setOnEnterClickListener(this);

        String model = DeviceUtils.getModel();
        String menufacture = DeviceUtils.getManufacturer();
        String netWork = NetworkUtils.getCurNetworkType(this);
        int memAva = DeviceUtils.getAvailMemory(this);
        int menTotal = DeviceUtils.getTotalMemory(this);
        long[] rom = DeviceUtils.getRomSize();
        DeviceUtils.getBootTime();
        info_text1.setText(menufacture+" "+model+" "+netWork);
        info_text2.setText("RAM:"+memAva+"/"+menTotal+"MB  ROM:"+rom[0]+"/"+rom[1]+"MB");

        GridView app_list = (GridView)findViewById(R.id.app_list);
        EditText editText_search = (EditText) findViewById(R.id.ed_search);
        adapter = new MyAppListAdapter(this, editText_search, apps);
        app_list.setAdapter(adapter);
        new UpdateDbTask().execute();
    }


    @Override
    public void onBackPressed() {
        if(apps_view.getVisibility()==View.VISIBLE){
            apps_view.setVisibility(View.GONE);
            main_view.setVisibility(View.VISIBLE);
        }
    }


    private void appendText(String s, OutPutType type){
        consoletextView.append(s,type);
        scrollView.postDelayed(mScrollRunnable,100);
    }


    private Runnable mScrollRunnable = new Runnable() {
        @Override
        public void run() {
            boolean inputHadFocus = editText_input.hasFocus();
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            if (inputHadFocus)
                editText_input.requestFocus();
        }
    };


    //发送命令事件
    @Override
    public void onEnterClick(String input,ExecContext context) {
        appendText(">>"+input,OutPutType.INPUT);
        client.exec(context,exeHandler);
        editText_input.setText("");
    }


    private class UpdateDbTask extends AsyncTask<Void,Void,List<AppData>>{

        @Override
        protected List<AppData> doInBackground(Void... voids) {
            LoadDbUtil.updateDabase(MainActivity.this);
            List<AppData> apps = LoadDbUtil.updateApp(MainActivity.this);
            for(int i=0;i<apps.size();i++){
                String a = apps.get(i).name.toString().toLowerCase();
                String fullpy = PinyinUtil.getFullPy(a);
                String firstpy = PinyinUtil.getFirstPy(a);
                apps.get(i).searchKey +=","+firstpy+","+fullpy;
            }

            return apps;
        }

        @Override
        protected void onPostExecute(List<AppData> appsD) {
            apps.clear();
            apps.addAll(appsD);
            findViewById(R.id.btn_show_apps).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    apps_view.setVisibility(View.VISIBLE);
                    main_view.setVisibility(View.GONE);
                }
            });
            findViewById(R.id.btn_show_apps).setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

}
