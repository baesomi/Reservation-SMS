package com.example.lanco.mobile_sms.Activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lanco.mobile_sms.DB.DBSingleManager;
import com.example.lanco.mobile_sms.R;

import java.util.ArrayList;

public class SMSPopupActivity extends AlertDialog.Builder {

    String d, n, m;
    Context c;
    View dialogView;
    LinearLayout groupLayout;
    TextView namePhone;
    TextView report;
    TextView reserved;
    TextView sort;
    TextView message;
    TextView group_n;
    String status;
    Long date;
    // ArrayList<Integer> pos;

    public SMSPopupActivity(Context context) {
        super(context);
        buildPopUp();
    }

    public SMSPopupActivity(Context context, String d, String n, String m, ViewGroup v) {
        super(context);
        this.d = d;//date
        this.n = n;//name
        this.m = m;//message
        this.c = context;
        dialogView = View.inflate(c, R.layout.activity_smspopup, null);
        groupLayout = (LinearLayout) dialogView.findViewById(R.id.groupLayout);
        namePhone = (TextView) dialogView.findViewById(R.id.namePhone);
        report = (TextView) dialogView.findViewById(R.id.pop_report);
        reserved = (TextView) dialogView.findViewById(R.id.pop_reserved);
        sort = (TextView) dialogView.findViewById(R.id.pop_sort);
        message = (TextView) dialogView.findViewById(R.id.pop_message);
        group_n = (TextView) dialogView.findViewById(R.id.pop_group);
        group_n.setText("");
        //pos = new ArrayList<Integer>();

        setView(dialogView);
        groupLayout.setVisibility(View.GONE);
        buildPopUp();
    }

    public void buildPopUp() {
        String t_name = null, t_phone = null, t_report = null, t_reserved = null, t_sort = null, t_message = null;
        String group_name = "";
        ArrayList<String> name_g = new ArrayList<String>();
        ArrayList<String> phone_g = new ArrayList<String>();
        this.setTitle("메세지 내용");
        this.setIcon(R.drawable.pop_m);
        DBSingleManager db = new DBSingleManager(c);
        db.open();
        Cursor data = db.getAllContacts();
        if (data.moveToFirst()) {
            do {
                if (n.contains(" - ")) {
                    group_name = n.substring(0, n.indexOf(" - "));
                    if (!data.isNull(data.getColumnIndex("groupname"))) {
                        if (data.getString(data.getColumnIndex("groupname")).equals(group_name)) {
                            if (data.getString(data.getColumnIndex("message")).equals(m)) {
                                if (DateUtils.formatDateTime(c, data.getLong(data.getColumnIndex("reportdate")), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME).equals(d)) {
                                    name_g.add(data.getString(data.getColumnIndex("name")));
                                    phone_g.add(data.getString(data.getColumnIndex("phone")));
                                    // pos.add(data.getInt(data.getColumnIndex("_id")));
                                    date = data.getLong(data.getColumnIndex("reportdate"));
                                    t_report = DateUtils.formatDateTime(c, data.getLong(data.getColumnIndex("reportdate")), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
                                    t_reserved = DateUtils.formatDateTime(c, data.getLong(data.getColumnIndex("reserveddate")), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
                                    t_sort = data.getString(data.getColumnIndex("sort"));
                                    t_message = data.getString(data.getColumnIndex("message"));
                                    status = data.getString(data.getColumnIndex("status"));
                                }
                            }
                        }
                    }
                } else {
                    if (data.getString(data.getColumnIndex("name")).equals(n)) {
                        if (data.getString(data.getColumnIndex("message")).equals(m)) {
                            if (DateUtils.formatDateTime(c, data.getLong(data.getColumnIndex("reportdate")), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME).equals(d)) {
                                t_name = data.getString(data.getColumnIndex("name"));
                                t_phone = data.getString(data.getColumnIndex("phone"));
                                date = data.getLong(data.getColumnIndex("reportdate"));
                                t_report = DateUtils.formatDateTime(c, data.getLong(data.getColumnIndex("reportdate")), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
                                t_reserved = DateUtils.formatDateTime(c, data.getLong(data.getColumnIndex("reserveddate")), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
                                t_sort = data.getString(data.getColumnIndex("sort"));
                                t_message = data.getString(data.getColumnIndex("message"));
                                status = data.getString(data.getColumnIndex("status"));
                                //pos.add(data.getInt(data.getColumnIndex("_id")));
                            }
                        }
                    }
                }
            } while (data.moveToNext());
        }
        db.close();

        String input_namePhone = "";

        if (!group_name.equals("")) {
            groupLayout.setVisibility(View.VISIBLE);
            group_n.setText(group_name);
            for (int i = 0; i < name_g.size(); i++) {
                if (i != name_g.size() - 1)
                    input_namePhone = input_namePhone + name_g.get(i) + "(" + phone_g.get(i) + ")\n";
                else
                    input_namePhone = input_namePhone + name_g.get(i) + "(" + phone_g.get(i) + ")";
            }
            namePhone.setText(input_namePhone);
        } else {
            input_namePhone = t_name + "(" + t_phone + ")";
            namePhone.setText(input_namePhone);
        }
        report.setText(t_report);
        reserved.setText(t_reserved);
        sort.setText(t_sort);
        message.setText(t_message);

        this.setPositiveButton("변경/수정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        // 히스토리, 샌드 구분 - 도착시간을 통해 처리 가능 or status
        // 그룹인지 한명인지 처리
        this.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //샌드의 내용
                if (status.equals("PENDING")) {
                    //혼자일경우
                    if (group_n.getText().equals("")) {
                    }
                    //아닐경우
                    else {

                    }

                    DBSingleManager db = new DBSingleManager(c);
                    db.open();
                    db.deleteContact(date);
                    db.close();
                }
                //히스토리 내용
                else {
                    DBSingleManager db = new DBSingleManager(c);
                    db.open();
                    db.deleteContact(date);
                    db.close();
                }
            }
        });

    }
}
