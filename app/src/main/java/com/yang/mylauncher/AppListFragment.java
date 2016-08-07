package com.yang.mylauncher;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


public class AppListFragment extends Fragment {

    public static final String TAG = "APPS";
    private GridView app_list;
    private List<AppData> apps;
    private MyAppListAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_app_list, container, false);
        app_list = (GridView) v.findViewById(R.id.app_list);
        adapter = new MyAppListAdapter();
        apps = ((MainActivity)getActivity()).getApps();

        app_list.setAdapter(adapter);
        return v;
    }




    private class MyAppListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return apps.size();
        }

        @Override
        public Object getItem(int i) {
            return apps.get(i);
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

    }

    private class AppItemViewHolder{

        ImageView icon;
        TextView name;
        LinearLayout main_item;
        AppItemViewHolder(View itemView) {
            icon = (ImageView) itemView.findViewById(R.id.icon);
            name = (TextView) itemView.findViewById(R.id.name);
            main_item = (LinearLayout) itemView.findViewById(R.id.main_item);
        }

        void setData(final int pos){
            final AppData i = apps.get(pos);
            icon.setImageDrawable(i.icon);
            name.setText(i.name);

            main_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().startActivity(i.intent);
                }
            });
        }
    }
}
