package com.example.lanco.mobile_sms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Lanco on 2016-05-29.
 */
public class SMSListAdapter extends BaseAdapter{
    ImageView icon;
    TextView name;
    TextView date;
    TextView rdate;
    TextView content;
    private ArrayList<SortingItem> listViewItemList = new ArrayList<>();
    //constructor
    public SMSListAdapter(){}

    // Adapter's the number of data. : essential implement
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // 해당 position에 있는 data를 screen output에 사용될 View를 return : essential implement
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // listlayout을 inflate하여 convertView reference .
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.sorting_list, parent, false);
        }

        icon = (ImageView) convertView.findViewById(R.id.icon) ;
        name = (TextView) convertView.findViewById(R.id.name) ;
        name.setSelected(true);
        date = (TextView) convertView.findViewById(R.id.date) ;
        rdate = (TextView) convertView.findViewById(R.id.rdate);
        content = (TextView) convertView.findViewById(R.id.content) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        SortingItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        icon.setImageDrawable(listViewItem.getIcon());
        name.setText(listViewItem.getName());
        date.setText(listViewItem.getDate());
        rdate.setText(listViewItem.getDate2());
        content.setText(listViewItem.getContent());

        return convertView;
    }

    public ArrayList<SortingItem> getListViewItemList(){
        return listViewItemList;
    }
    // 지정한 position에 있는 data와 relative한 item(row)의 ID를 return.
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정 position에 있는 data return
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    public String getPositionDate(int position){
        return listViewItemList.get(position).getDate();
    }

    public String getPositionDate2(int position){return listViewItemList.get(position).getDate2();}

    public String getPositionName(int position){
        return listViewItemList.get(position).getName();
    }

    public String getPositionMessage(int position){
        return listViewItemList.get(position).getContent();
    }

    // ADD Item data function
    public void addItem(Drawable icon, String name, String date,String rdate, String content) {
        SortingItem item = new SortingItem();

        item.setIcon(icon);
        item.setName(name);
        item.setDate(date);
        item.setDate2(rdate);
        item.setContent(content);

        listViewItemList.add(item);
    }

    public void addReplace(Drawable icon, String name, String date,String rdate, String content) {
        SortingItem item = new SortingItem();

        item.setIcon(icon);
        item.setName(name);
        item.setDate(date);
        item.setDate2(rdate);
        item.setContent(content);

        listViewItemList.add(item);
    }
}