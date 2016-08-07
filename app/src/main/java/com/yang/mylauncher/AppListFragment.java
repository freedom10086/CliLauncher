package com.yang.mylauncher;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


public class AppListFragment extends Fragment {

    public static final String TAG = "APPS";
    private RecyclerView recyclerView;
    private List<AppData> apps;
    private MyAppListAdapter adapter;


    public AppListFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_app_list, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        adapter = new MyAppListAdapter();
        recyclerView.setAdapter(adapter);
        apps = ((MainActivity)getActivity()).getApps();
        return v;
    }


    private class MyAppListAdapter extends RecyclerView.Adapter<AppItemViewHolder>{

        @Override
        public AppItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new AppItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list_item,parent,false));
        }

        @Override
        public void onBindViewHolder(AppItemViewHolder holder, int position) {
            holder.setData(position);
        }

        @Override
        public int getItemCount() {
            return apps.size();
        }
    }

    private class AppItemViewHolder extends RecyclerView.ViewHolder{

        ImageView icon;
        TextView name;
        LinearLayout main_item;
        AppItemViewHolder(View itemView) {
            super(itemView);
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
