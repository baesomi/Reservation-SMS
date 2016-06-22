package com.example.lanco.mobile_sms;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kjy79 on 2016-06-08.
 */
public class CalendarListAdapter extends BaseAdapter{

    TextView name;

    private ArrayList<CalendarItem> listViewItemList = new ArrayList<CalendarItem>() ;

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {//리스트 뷰에 아이템들 추가
        Context ctx = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.calendar_list, parent, false);
        }
        name = (TextView)convertView.findViewById(R.id.eventName);

        CalendarItem calendarItem = listViewItemList.get(position);

        name.setText(calendarItem.getName());
        return convertView;
    }

    public void addItem(String name, String date) {//리스트뷰에 아이템 set
        CalendarItem item = new CalendarItem();

        item.setName(name);
        item.setDate(date);

        listViewItemList.add(item);
    }
}
