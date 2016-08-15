package com.yang.mylauncher;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yang.mylauncher.data.AppData;
import com.yang.mylauncher.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;


class MyAppListAdapter extends BaseAdapter implements TextWatcher{

    private List<AppData> filterApps;
    private List<AppData> apps;
    private Context context;

    MyAppListAdapter(Context context, EditText editText, List<AppData> apps) {
        this.apps = apps;
        filterApps = new ArrayList<>();
        filterApps.addAll(apps);
        this.context = context;
        editText.addTextChangedListener(this);
    }


    @Override
    public void notifyDataSetChanged() {
        filterApps.clear();
        filterApps.addAll(apps);
        super.notifyDataSetChanged();
    }

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
        if (view == null || view.getTag() == null) {
            view = LayoutInflater.from(context).inflate(R.layout.app_list_item, null);
            holder = new AppItemViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (AppItemViewHolder) view.getTag();
        }
        holder.setData(i);
        return view;
    }

    private void onFilter(String s) {
        filterApps.clear();
        for (AppData app : apps) {
            s = s.trim().toLowerCase();
            String searchkey = app.searchKey;
            String[] searchs = searchkey.split(",");
            for (String ss : searchs) {
                if (ss.contains(s)) {
                    filterApps.add(app);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String s = charSequence.toString().trim();
        s = s.toLowerCase();
        onFilter(s);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


    private class AppItemViewHolder{

        ImageView icon;
        TextView name;
        View main_item;
        AppItemViewHolder(View itemView) {
            icon = (ImageView) itemView.findViewById(R.id.icon);
            name = (TextView) itemView.findViewById(R.id.name);
            main_item = itemView.findViewById(R.id.main_item);
        }

        void setData(final int pos){
            final AppData i = filterApps.get(pos);
            icon.setImageDrawable(i.icon);
            name.setText(i.name);

            main_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(
                            filterApps.get(pos).intent));
                }
            });

            main_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AppUtils.uninstall(context,filterApps.get(pos).pkg);
                    return false;
                }
            });

        }
    }
}
