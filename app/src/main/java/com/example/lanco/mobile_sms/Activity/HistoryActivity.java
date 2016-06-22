package com.example.lanco.mobile_sms.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lanco.mobile_sms.DB.DBSingleManager;
import com.example.lanco.mobile_sms.SMSData;
import com.example.lanco.mobile_sms.SMSListAdapter;
import com.example.lanco.mobile_sms.R;
import com.example.lanco.mobile_sms.SortingItem;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;

public class HistoryActivity extends Fragment{

    private MaterialSpinner sorting;

    private ListView listview;
    private SMSListAdapter adapter;

    EditText hisText;
    ImageButton hisBtn;

    private Cursor mCursor;
    private DBSingleManager db;
    private ViewGroup v;

    AlertDialog.Builder builder;
    AlertDialog dialog;
    final String[] items = {"생일", "새해", "크리스마스", "추석", "어버이날", "스승의날", "기타"};//다이얼로그 아이템
    String sort;
    int pos=0;
    int type=0;

    //notification을 누르면 history가 뜬다
    int notificationID = 1;
    ArrayList<SMSData> temp = new ArrayList<SMSData>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_history, container, false);
        v = container;

        hisText = (EditText) view.findViewById(R.id.hisText);
        hisBtn = (ImageButton) view.findViewById(R.id.hisBtn);
        sorting = (MaterialSpinner) view.findViewById(R.id.sorting);
        hisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sText = hisText.getText().toString();
                adapter.getListViewItemList().clear();
                showSearchHistory(sText);
            }
        });

        builder = new AlertDialog.Builder(getActivity());//alert dialog(삭제 다이얼로그) 빌더 생성 및 클릭리스너 등록
        builder.setTitle("목록을 선택하세요").setItems(items, new DialogInterface.OnClickListener() {// 목록 클릭시 설정
            public void onClick(DialogInterface dialog, int index) {
                if (index == 0) pos = 0;
                if (index == 1) pos = 1;
                if (index == 2) pos = 2;
                if (index == 3) pos = 3;
                if (index == 4) pos = 4;
                if (index == 5) pos = 5;

               // adapter = new SMSListAdapter();
                type=2;
                sortSettingContact();
                //listview.setAdapter(adapter);
            }
        });
        dialog = builder.create();//alert dialog 생성


        sorting.setItems("최신순", "오래된순", "분류별");

        sorting.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                type=position;
                if (position != 2) {
                    sortSettingContact();
                } else
                    dialog.show();
            }
        });
        sorting.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {


            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
            }
        });

        adapter = new SMSListAdapter();

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) view.findViewById(R.id.smsList);

        db = new DBSingleManager(getActivity());

        showHistory();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View dialogView = View.inflate(getActivity(), R.layout.activity_smspopup, null);
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                builder.setTitle("메세지 전송 내용");
                builder.setIcon(R.drawable.pop_m);
                LinearLayout groupLayout = (LinearLayout) dialogView.findViewById(R.id.groupLayout);
                TextView namePhone = (TextView) dialogView.findViewById(R.id.namePhone);
                TextView report = (TextView) dialogView.findViewById(R.id.pop_report);
                TextView reserved = (TextView) dialogView.findViewById(R.id.pop_reserved);
                TextView sorted = (TextView) dialogView.findViewById(R.id.pop_sort);
                TextView message = (TextView) dialogView.findViewById(R.id.pop_message);
                TextView group_n = (TextView) dialogView.findViewById(R.id.pop_group);
                groupLayout.setVisibility(View.GONE);
                builder.setView(dialogView);
                String group_name = "";
                ArrayList<SMSData> sms = new ArrayList<SMSData>();
                DBSingleManager db = new DBSingleManager(getActivity());
                db.open();
                Cursor data = db.getAllContacts();
                if (data.moveToFirst()) {
                    do {
                        //그룹 리스트뷰일경우
                        if (adapter.getPositionName(position).contains(" - ")) {
                            group_name = adapter.getPositionName(position).substring(0, adapter.getPositionName(position).indexOf(" - "));//값저장
                            // 그룹이 비지않음
                            if (!data.isNull(data.getColumnIndex("groupname"))) {
                                //그룹 이름이 같다
                                if (data.getString(data.getColumnIndex("groupname")).equals(group_name)) {
                                    //메세지 내용도 같다
                                    if (data.getString(data.getColumnIndex("message")).equals(adapter.getPositionMessage(position))) {
                                        //보낸 시간도 같다
                                        if (DateUtils.formatDateTime(getActivity(), data.getLong(data.getColumnIndex("reportdate")), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME).equals(adapter.getPositionDate(position))) {
                                            SMSData a = new SMSData();
                                            //커서값 전부 입력
                                            a.setGroupName(group_name);
                                            a.setRecipientName(data.getString(data.getColumnIndex("name")));
                                            a.setRecipientNumber(data.getString(data.getColumnIndex("phone")));
                                            a.setReportDate(data.getLong(data.getColumnIndex("reportdate")));
                                            a.setReservedDate(data.getLong(data.getColumnIndex("reserveddate")));
                                            a.setMessage(data.getString(data.getColumnIndex("message")));
                                            a.setSort(data.getString(data.getColumnIndex("sort")));
                                            a.setStatus(data.getString(data.getColumnIndex("status")));
                                            a.setResult(data.getString(data.getColumnIndex("result")));
                                            sms.add(a);
                                        }
                                    }
                                }
                            }
                            //그룹이 아닐경우
                        } else {
                            if (data.getString(data.getColumnIndex("name")).equals(adapter.getPositionName(position))) {
                                if (data.getString(data.getColumnIndex("message")).equals(adapter.getPositionMessage(position))) {
                                    if (DateUtils.formatDateTime(getActivity(), data.getLong(data.getColumnIndex("reportdate")), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME).equals(adapter.getPositionDate(position))) {
                                        SMSData a = new SMSData();
                                        a.setRecipientName(data.getString(data.getColumnIndex("name")));
                                        a.setRecipientNumber(data.getString(data.getColumnIndex("phone")));
                                        a.setReportDate(data.getLong(data.getColumnIndex("reportdate")));
                                        a.setReservedDate(data.getLong(data.getColumnIndex("reserveddate")));
                                        a.setMessage(data.getString(data.getColumnIndex("message")));
                                        a.setSort(data.getString(data.getColumnIndex("sort")));
                                        a.setStatus(data.getString(data.getColumnIndex("status")));
                                        a.setResult(data.getString(data.getColumnIndex("result")));
                                        sms.add(a);
                                    }
                                }
                            }
                        }
                    } while (data.moveToNext());
                }
                db.close();

                temp.clear();
                temp.addAll(sms);

                String input_namePhone = "";
                if (!group_name.equals("")) {
                    groupLayout.setVisibility(View.VISIBLE);
                    group_n.setText(group_name);
                    for (int i = 0; i < sms.size(); i++) {
                        if (i != sms.size() - 1)
                            input_namePhone = input_namePhone + sms.get(i).getRecipientName() + "(" + sms.get(i).getRecipientNumber() + ")\n";
                        else
                            input_namePhone = input_namePhone + sms.get(i).getRecipientName() + "(" + sms.get(i).getRecipientNumber() + ")";
                    }
                    namePhone.setText(input_namePhone);
                } else {
                    input_namePhone = sms.get(0).getRecipientName() + "(" + sms.get(0).getRecipientNumber() + ")";
                    namePhone.setText(input_namePhone);
                }
                report.setText(DateUtils.formatDateTime(getActivity(), sms.get(0).getReportDate(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME));
                reserved.setText(DateUtils.formatDateTime(getActivity(), sms.get(0).getReservedDate(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME));
                sorted.setText(sms.get(0).getSort());
                message.setText(sms.get(0).getMessage());

                builder.setPositiveButton("변경/수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //그룹 라이트 액티비티로 보내 알람을 작성을 완료할경우 등록 - 기본값만 주어줌
                        if (temp.size() > 1) {
                            Intent i = new Intent(getActivity(), WriteGroupActivity.class);
                            i.putParcelableArrayListExtra("HisGroupSMSModify", temp);
                            startActivity(i);
                        }
                        // 단일 라이트 액티비티로 보내 알람을 작성완료할경우 등록 - 기본값만 주어짐
                        else {
                            Intent i = new Intent(getActivity(), WriteActivity.class);
                            i.putParcelableArrayListExtra("HisSMSModify", temp);
                            startActivity(i);
                        }
                    }
                });
                builder.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DBSingleManager db = new DBSingleManager(getActivity());
                        db.open();
                        db.deleteContact(temp.get(0).getReportDate());
                        db.close();
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNeutralButton("닫기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                android.support.v7.app.AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });

        listview.setAdapter(adapter);

        hisText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (hisText.getText().toString() == null || hisText.getText().toString().equals("")) {
                    adapter.getListViewItemList().clear();
                    showHistory();
                }
            }
        });

        return view;
    }

    // 개인 내용을 출력하는 부분
    public void DisplayContact(Cursor c) {
        String rsvdate = DateUtils.formatDateTime(getActivity(), c.getLong(c.getColumnIndex("reserveddate")), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
        String date = DateUtils.formatDateTime(getActivity(), c.getLong(c.getColumnIndex("reportdate")), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
        Drawable icon;
        if (!c.getString(c.getColumnIndex("status")).equals("SENT"))
            icon = ContextCompat.getDrawable(this.getActivity(), R.drawable.failed1);
        else
            icon = ContextCompat.getDrawable(this.getActivity(), R.drawable.after);

        adapter.addItem(icon, c.getString(c.getColumnIndex("name")), date, rsvdate, c.getString(c.getColumnIndex("message")));
    }

    // 그룹내용을 출력하는 부분
    public void DisplayGroupContact(String gName, Long date, Long res, String gMessage, String gLeader, int size, boolean st) {
        String groupdate = DateUtils.formatDateTime(getActivity(), date, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
        String grouprsvdate = DateUtils.formatDateTime(getActivity(), res, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
        Drawable icon;
        if (st == false)
            icon = ContextCompat.getDrawable(this.getActivity(), R.drawable.group_failed);
        else
            icon = ContextCompat.getDrawable(this.getActivity(), R.drawable.group_after);

        adapter.addItem(icon, gName + " - " + gLeader + " 외 " + size, groupdate, grouprsvdate, gMessage);
    }


    public void showHistory() {
        db.open();

        if(type==0)
            mCursor = db.getHistoryViewContact();
        else if(type==1)
            mCursor = db.getHistoryLastViewContact();
        else if(type==2)
            mCursor = db.getHistorySortViewContact(items[pos]);

        if (mCursor.moveToFirst()) {
            String groupName = "";
            String groupMessage = "";
            Long groupDate = null;
            Long groupReserve = null;
            String groupLeader = "";
            boolean groupStatus = true;
            int groupSize = 0;

            // 그룹처리를 위한 어레이 리스트와 이름 임시저장
            do {
                //커서에 그룹이름이 없을경우
                if (mCursor.isNull(mCursor.getColumnIndex("groupname"))) {
                    //그룹내용이 없었다면
                    if (groupName.equals("")) {
                        DisplayContact(mCursor);
                    }
                    //그룹내용이 있었다면
                    else {
                        //저장내용을 이미지화시켜주고
                        DisplayGroupContact(groupName, groupDate, groupReserve, groupMessage, groupLeader, groupSize, groupStatus);
                        groupName = "";
                        groupMessage = "";
                        groupDate = null;
                        groupReserve = null;
                        groupLeader = "";
                        groupSize = 0;
                        groupStatus = true;
                        DisplayContact(mCursor);
                    }
                }
                //커서에 그룹이름이 있다.
                else {
                    //이전 그룹내용이 저장된것이 없다
                    if (groupName.equals("")) {
                        groupName = mCursor.getString(mCursor.getColumnIndex("groupname"));
                        groupDate = mCursor.getLong(mCursor.getColumnIndex("reportdate"));
                        groupReserve = mCursor.getLong(mCursor.getColumnIndex("reserveddate"));
                        groupMessage = mCursor.getString(mCursor.getColumnIndex("message"));
                        groupLeader = mCursor.getString(mCursor.getColumnIndex("name"));
                        if (!mCursor.getString(mCursor.getColumnIndex("status")).equals("SENT"))
                            groupStatus = false;
                    }
                    // 이전 그룹내용에 저장된값이 있다.
                    else {
                        // 그룹내용이 이전 저장된 내용과 같다.
                        if (mCursor.getString(mCursor.getColumnIndex("groupname")).equals(groupName)) {
                            if (!mCursor.getString(mCursor.getColumnIndex("status")).equals("SENT"))
                                groupStatus = false;
                            groupSize++;
                        }
                        // 저장된 이름은 있었으나 저장된 내용과는 다르다
                        else {
                            DisplayGroupContact(groupName, groupDate, groupReserve, groupMessage, groupLeader, groupSize, groupStatus);
                            groupName = mCursor.getString(mCursor.getColumnIndex("groupname"));
                            groupDate = mCursor.getLong(mCursor.getColumnIndex("reportdate"));
                            groupReserve = mCursor.getLong(mCursor.getColumnIndex("reserveddate"));
                            groupMessage = mCursor.getString(mCursor.getColumnIndex("message"));
                            groupLeader = mCursor.getString(mCursor.getColumnIndex("name"));
                            groupSize = 0;
                            if (!mCursor.getString(mCursor.getColumnIndex("status")).equals("SENT"))
                                groupStatus = false;
                            else groupStatus = true;
                        }
                    }
                }
            } while (mCursor.moveToNext());
            if (!groupName.equals("")) {
                DisplayGroupContact(groupName, groupDate, groupReserve, groupMessage, groupLeader, groupSize, groupStatus);
            }
        }
        db.close();
        adapter.notifyDataSetChanged();
    }

    public void showSearchHistory(String search) {
        db.open();

        if(type==0)
            mCursor = db.getHistoryViewContact();
        else if(type==1)
            mCursor = db.getHistoryLastViewContact();
        else if(type==2)
            mCursor = db.getHistorySortViewContact(items[pos]);

        if (mCursor.moveToFirst()) {
            String groupName = "";
            String groupMessage = "";
            Long groupDate = null;
            Long groupReserve = null;
            String groupLeader = "";
            int groupSize = 0;
            boolean groupStatus = true;
            boolean find = false;

            // 그룹처리를 위한 어레이 리스트와 이름 임시저장
            do {
                //커서에 그룹이름이 없을경우
                if (mCursor.isNull(mCursor.getColumnIndex("groupname"))) {
                    //그룹내용이 없었다면
                    if (groupName.equals("")) {
                        if (mCursor.getString(mCursor.getColumnIndex("name")).contains(search))
                            DisplayContact(mCursor);
                    }
                    //그룹내용이 있었다면
                    else {
                        //저장내용을 이미지화시켜주고
                        if (find == true)
                            DisplayGroupContact(groupName, groupDate, groupReserve, groupMessage, groupLeader, groupSize, groupStatus);
                        groupName = "";
                        groupMessage = "";
                        groupDate = null;
                        groupReserve = null;
                        groupLeader = "";
                        groupSize = 0;
                        groupStatus = true;
                        find = false;
                        if (mCursor.getString(mCursor.getColumnIndex("name")).contains(search))
                            DisplayContact(mCursor);
                    }
                }
                //커서에 그룹이름이 있다.
                else {
                    //이전 그룹내용이 저장된것이 없다
                    if (groupName.equals("")) {
                        groupName = mCursor.getString(mCursor.getColumnIndex("groupname"));
                        groupDate = mCursor.getLong(mCursor.getColumnIndex("reportdate"));
                        groupReserve = mCursor.getLong(mCursor.getColumnIndex("reserveddate"));
                        groupMessage = mCursor.getString(mCursor.getColumnIndex("message"));
                        groupLeader = mCursor.getString(mCursor.getColumnIndex("name"));
                        if (!mCursor.getString(mCursor.getColumnIndex("status")).equals("SENT"))
                            groupStatus = false;
                        if (mCursor.getString(mCursor.getColumnIndex("name")).contains(search) || mCursor.getString(mCursor.getColumnIndex("groupname")).contains(search))
                            find = true;
                    }
                    // 이전 그룹내용에 저장된값이 있다.
                    else {
                        // 그룹내용이 이전 저장된 내용과 같다.
                        if (mCursor.getString(mCursor.getColumnIndex("groupname")).equals(groupName)) {

                            if (!mCursor.getString(mCursor.getColumnIndex("status")).equals("SENT"))
                                groupStatus = false;
                            if (mCursor.getString(mCursor.getColumnIndex("name")).contains(search))
                                find = true;

                            groupSize++;
                        }
                        // 저장된 이름은 있었으나 저장된 내용과는 다르다
                        else {
                            if (find == true)
                                DisplayGroupContact(groupName, groupDate, groupReserve, groupMessage, groupLeader, groupSize, groupStatus);
                            groupName = mCursor.getString(mCursor.getColumnIndex("groupname"));
                            groupDate = mCursor.getLong(mCursor.getColumnIndex("reportdate"));
                            groupReserve = mCursor.getLong(mCursor.getColumnIndex("reserveddate"));
                            groupMessage = mCursor.getString(mCursor.getColumnIndex("message"));
                            groupLeader = mCursor.getString(mCursor.getColumnIndex("name"));
                            groupSize = 0;
                            if (!mCursor.getString(mCursor.getColumnIndex("status")).equals("SENT"))
                                groupStatus = false;
                            else groupStatus = true;
                            if (mCursor.getString(mCursor.getColumnIndex("name")).contains(search) || mCursor.getString(mCursor.getColumnIndex("groupname")).contains(search))
                                find = true;
                            else find = false;
                        }
                    }
                }
            } while (mCursor.moveToNext());
            if (!groupName.equals("") && find == true) {
                DisplayGroupContact(groupName, groupDate, groupReserve, groupMessage, groupLeader, groupSize, groupStatus);
            }

        }
        db.close();

        adapter.notifyDataSetChanged();
    }

    public void sortSettingContact(){
        db.open();
        adapter.getListViewItemList().clear();
        if(type==0 || type==1 || type==2){
            showHistory();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        adapter.getListViewItemList().clear();
        showHistory();
    }
}
