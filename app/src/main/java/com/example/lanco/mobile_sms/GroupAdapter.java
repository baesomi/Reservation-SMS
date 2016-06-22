package com.example.lanco.mobile_sms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Lanco on 2016-06-07.
 */
public class GroupAdapter extends BaseAdapter {
    TextView gName;
    TextView gPhone;
    Button gDel;

    private ArrayList<GroupItem> GroupItemList = new ArrayList<GroupItem>();
    @Override
    public int getCount() {
        return GroupItemList.size();
    }

    @Override
    public GroupItem getItem(int position) {
        return GroupItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //interface
    public interface onButtonClickListener{
        void onDelete(int a);
    }

    private onButtonClickListener adptCallback = null;

    public void setOnButtonClickListener(onButtonClickListener callback){
        adptCallback = callback;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // listlayout을 inflate하여 convertView reference .
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_list, parent, false);
        }

        gName = (TextView)convertView.findViewById(R.id.g_name);
        gPhone = (TextView)convertView.findViewById(R.id.g_phone);
        gDel = (Button)convertView.findViewById(R.id.g_del);

        GroupItem GroupViewItem = GroupItemList.get(position);

        gName.setText(GroupViewItem.getGName());
        gPhone.setText(GroupViewItem.getGPhone());

        gDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adptCallback!=null)
                    adptCallback.onDelete(pos);
            }
        });
        return convertView;
    }

    public void addItem(String name, String phone){
        GroupItem item = new GroupItem();
        item.setGName(name);
        item.setGPhone(phone);
        GroupItemList.add(item);
    }
    public void remove(int position){
        GroupItemList.remove(position);
    }
}
