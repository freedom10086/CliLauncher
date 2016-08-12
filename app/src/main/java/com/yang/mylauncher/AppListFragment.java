package com.yang.mylauncher;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yang.mylauncher.data.AppData;
import com.yang.mylauncher.utils.AppUtils;
import com.yang.mylauncher.utils.ImeUtil;
import com.yang.mylauncher.utils.PinyinUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


public class AppListFragment extends Fragment implements TextWatcher,AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    public static final String TAG = "APPS";
    private GridView app_list;
    private List<AppData> apps;
    private MyAppListAdapter adapter;
    private List<AppData> filterApps;
    private EditText editText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_app_list, container, false);
        app_list = (GridView) v.findViewById(R.id.app_list);
        adapter = new MyAppListAdapter();
        apps = ((MainActivity)getActivity()).apps;
        filterApps = new ArrayList<>();
        filterApps.addAll(apps);
        app_list.setAdapter(adapter);
        editText = (EditText) v.findViewById(R.id.ed_search);
        editText.addTextChangedListener(this);
        app_list.setOnItemClickListener(this);
        app_list.setOnItemLongClickListener(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                setSerachKey();
            }
        }).start();
        return v;
    }


    private void setSerachKey(){
        for(int i=0;i<apps.size();i++){
            String a = apps.get(i).name.toString().toLowerCase();
            String fullpy = PinyinUtil.getFullPy(a);
            String firstpy = PinyinUtil.getFirstPy(a);
            apps.get(i).searchKey +=","+firstpy+","+fullpy;
            Log.e("serachkey",apps.get(i).searchKey);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String s = charSequence.toString().trim();
        s = s.toLowerCase();
        adapter.onFilter(s);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        getActivity().startActivity(new Intent(filterApps.get(i).intent));
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        AppUtils.uninstall(getActivity(),filterApps.get(i).pkg);
        return false;
    }


    private class MyAppListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return filterApps.size();
        }

        @Override
        public Object getItem(int i) {
            return filterApps.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            //观察convertView随ListView滚动情况
            final AppItemViewHolder holder;
            if (view == null||view.getTag()==null) {
                view = LayoutInflater.from(getActivity()).inflate(R.layout.app_list_item, null);
                holder = new AppItemViewHolder(view);
                view.setTag(holder);
            }else{
                holder = (AppItemViewHolder)view.getTag();
            }
            holder.setData(i);
            return view;
        }

        public void onFilter(String s){
            filterApps.clear();
            for (AppData app : apps) {
                s = s.trim().toLowerCase();
                String searchkey = app.searchKey;
                String[] searchs = searchkey.split(",");
                for (String ss:searchs){
                    if(ss.contains(s)){
                        filterApps.add(app);
                        break;
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    private class AppItemViewHolder{

        ImageView icon;
        TextView name;
        AppItemViewHolder(View itemView) {
            icon = (ImageView) itemView.findViewById(R.id.icon);
            name = (TextView) itemView.findViewById(R.id.name);
        }

        void setData(final int pos){
            final AppData i = filterApps.get(pos);
            icon.setImageDrawable(i.icon);
            name.setText(i.name);
        }
    }
}
