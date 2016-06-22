package com.example.lanco.mobile_sms.Activity;

import android.app.FragmentController;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.lanco.mobile_sms.DB.DBSingleManager;
import com.example.lanco.mobile_sms.R;
import com.example.lanco.mobile_sms.SMSData;
import com.example.lanco.mobile_sms.SMSListAdapter;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;

public class SendActivity extends Fragment{

    private String sort[];
    //
    private MaterialSpinner sorting;
    private ListView listview;
    private SMSListAdapter adapter;

    EditText sText;
    ImageButton sBtn;

    private Cursor mCursor;
    private DBSingleManager db;
    private ViewGroup v;

    ArrayList<SMSData> temp = new ArrayList<SMSData>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_send, container, false);
        v = container;

        sBtn = (ImageButton)view.findViewById(R.id.sBtn);
        sText = (EditText)view.findViewById(R.id.sText);
        sort = getResources().getStringArray(R.array.sort_array);
        sorting = (MaterialSpinner)view.findViewById(R.id.sorting);

        sBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = sText.getText().toString();
                adapter.getListViewItemList().clear();
                showSearchSend(searchText);
            }
        });

        sorting.setItems("최신순","오래된순","이름순");

        sorting.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                int index = sorting.getSelectedIndex();
            }
        });

        sorting.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
            }
        });

        adapter = new SMSListAdapter() ;
        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) view.findViewById(R.id.smsList);


        db = new DBSingleManager(getActivity());

        showSend();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View dialogView = View.inflate(getActivity(),R.layout.activity_smspopup,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

                String group_name="";
                ArrayList<SMSData> sms = new ArrayList<SMSData>();
                DBSingleManager db = new DBSingleManager(getActivity());

                db.open();

                Cursor data = db.getAllContacts();

                if(data.moveToFirst()){
                    do{
                        //그룹 리스트뷰일경우
                        if(adapter.getPositionName(position).contains(" - ")){
                            group_name = adapter.getPositionName(position).substring(0,adapter.getPositionName(position).indexOf(" - "));//값저장
                            // 그룹이 비지않음
                            if(!data.isNull(data.getColumnIndex("groupname"))){
                                //그룹 이름이 같다
                                if(data.getString(data.getColumnIndex("groupname")).equals(group_name)){
                                    //메세지 내용도 같다
                                    if (data.getString(data.getColumnIndex("message")).equals(adapter.getPositionMessage(position))){
                                        //보낸 시간도 같다
                                        if (DateUtils.formatDateTime(getActivity(), data.getLong(data.getColumnIndex("reportdate")), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME).equals(adapter.getPositionDate(position))){
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
                        } else{
                            if(data.getString(data.getColumnIndex("name")).equals(adapter.getPositionName(position))){
                                if(data.getString(data.getColumnIndex("message")).equals(adapter.getPositionMessage(position))){
                                    if (DateUtils.formatDateTime(getActivity(), data.getLong(data.getColumnIndex("reportdate")), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME).equals(adapter.getPositionDate(position))){
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
                    }while(data.moveToNext());
                }
                db.close();

                temp.clear();
                temp.addAll(sms);

                String input_namePhone="";
                if(!group_name.equals("")){
                    groupLayout.setVisibility(View.VISIBLE);
                    group_n.setText(group_name);
                    for(int i=0;i<sms.size();i++){
                        if(i!=sms.size()-1)
                            input_namePhone = input_namePhone+sms.get(i).getRecipientName()+"("+sms.get(i).getRecipientNumber()+")\n";
                        else
                            input_namePhone = input_namePhone+sms.get(i).getRecipientName()+"("+sms.get(i).getRecipientNumber()+")";
                    }
                    namePhone.setText(input_namePhone);
                }else{
                    input_namePhone = sms.get(0).getRecipientName()+"("+sms.get(0).getRecipientNumber()+")";
                    namePhone.setText(input_namePhone);
                }
                report.setText(DateUtils.formatDateTime(getActivity(),sms.get(0).getReportDate(),DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME));
                reserved.setText(DateUtils.formatDateTime(getActivity(), sms.get(0).getReservedDate(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME));
                sorted.setText(sms.get(0).getSort());
                message.setText(sms.get(0).getMessage());

                builder.setPositiveButton("변경/수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //그룹 라이트 액티비티로 보내 알람을 작성을 완료할경우 알람 삭제와 동시에 등록
                        if (temp.size() > 1) {
                            Intent i = new Intent(getActivity(), WriteGroupActivity.class);
                            i.putParcelableArrayListExtra("groupSMSModify", temp);
                            startActivity(i);
                        }
                        // 단일 라이트 액티비티로 보내 알람을 작성완료할경우 알람삭제와 동시에 등록
                        else {
                            Intent i = new Intent(getActivity(), WriteActivity.class);
                            i.putParcelableArrayListExtra("SMSModify", temp);
                            startActivity(i);
                        }
                    }
                });
                builder.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 그룹 라이트 액티비티로 보내 알람과 디비 삭제를 진행한다
                        if (temp.size() > 1) {
                            Intent i = new Intent(getActivity(), WriteGroupActivity.class);
                            i.putParcelableArrayListExtra("groupSMSDelete", temp);
                            startActivity(i);
                        }
                        // 단일 라이트 액티비티로 보내 알람과 디비 삭제를 진행한다.
                        else {
                            Intent i = new Intent(getActivity(), WriteActivity.class);
                            i.putParcelableArrayListExtra("SMSDelete", temp);
                            startActivity(i);
                        }
                    }
                });
                builder.setNeutralButton("닫기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });
        listview.setAdapter(adapter);

        sText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(sText.getText().toString()==null || sText.getText().toString().equals("")){
                    adapter.getListViewItemList().clear();
                    showSend();
                }
            }
        });

        return view;
    }

    public void DisplayContact(Cursor c){
        String rsvdate = DateUtils.formatDateTime(getActivity(), c.getLong(c.getColumnIndex("reserveddate")),DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
        String date = DateUtils.formatDateTime(getActivity(), c.getLong(c.getColumnIndex("reportdate")), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
        adapter.addItem(ContextCompat.getDrawable(this.getActivity(), R.drawable.before), c.getString(c.getColumnIndex("name")), date, rsvdate, c.getString(c.getColumnIndex("message")));
    }

    public void DisplayGroupContact(String gName, Long date,Long res, String gMessage, String gLeader, int size){
        String groupdate = DateUtils.formatDateTime(getActivity(), date, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
        String grouprsvdate = DateUtils.formatDateTime(getActivity(), res, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);

        adapter.addItem(ContextCompat.getDrawable(this.getActivity(), R.drawable.group_before), gName + " - " + gLeader + " 외 " + size, groupdate, grouprsvdate, gMessage);
    }

    //샌드 내용에 들어갈것을 보여주는 부분
    public void showSend(){
        db.open();

        mCursor = db.getSendViewContact();

        if(mCursor.moveToFirst()){
            String groupName="";
            String groupMessage="";
            Long groupDate=null;
            Long groupReserve=null;
            String groupLeader="";
            int groupSize=0;

            // 그룹처리를 위한 어레이 리스트와 이름 임시저장
            do{
                //커서에 그룹이름이 없을경우
                if(mCursor.isNull(mCursor.getColumnIndex("groupname"))) {
                    //그룹내용이 없었다면
                    if(groupName.equals("")) {
                        DisplayContact(mCursor);
                    }
                    //그룹내용이 있었다면
                    else{
                        //저장내용을 이미지화시켜주고
                        DisplayGroupContact(groupName,groupDate,groupReserve,groupMessage,groupLeader,groupSize);
                        groupName="";
                        groupMessage="";
                        groupDate=null;
                        groupReserve=null;
                        groupLeader="";
                        groupSize=0;
                        DisplayContact(mCursor);
                    }
                }
                //커서에 그룹이름이 있다.
                else{
                    //이전 그룹내용이 저장된것이 없다
                    if(groupName.equals("")){
                        groupName=mCursor.getString(mCursor.getColumnIndex("groupname"));
                        groupDate=mCursor.getLong(mCursor.getColumnIndex("reportdate"));
                        groupReserve = mCursor.getLong(mCursor.getColumnIndex("reserveddate"));
                        groupMessage=mCursor.getString(mCursor.getColumnIndex("message"));
                        groupLeader=mCursor.getString(mCursor.getColumnIndex("name"));
                    }
                    // 이전 그룹내용에 저장된값이 있다.
                    else {
                        // 그룹내용이 이전 저장된 내용과 같다.
                        if (mCursor.getString(mCursor.getColumnIndex("groupname")).equals(groupName)) {
                            groupSize++;
                        }
                        // 저장된 이름은 있었으나 저장된 내용과는 다르다
                        else {
                            DisplayGroupContact(groupName,groupDate,groupReserve,groupMessage,groupLeader,groupSize);
                            groupName=mCursor.getString(mCursor.getColumnIndex("groupname"));
                            groupDate=mCursor.getLong(mCursor.getColumnIndex("reportdate"));
                            groupReserve = mCursor.getLong(mCursor.getColumnIndex("reserveddate"));
                            groupMessage=mCursor.getString(mCursor.getColumnIndex("message"));
                            groupLeader=mCursor.getString(mCursor.getColumnIndex("name"));
                            groupSize=0;
                        }
                    }
                }

            } while(mCursor.moveToNext());
            if(!groupName.equals("")){
                DisplayGroupContact(groupName,groupDate,groupReserve,groupMessage,groupLeader,groupSize);
            }
        }
        db.close();
        adapter.notifyDataSetChanged();
    }

    // 검색 부분을 보여주는 창
    public void showSearchSend(String search){
        db.open();

        mCursor = db.getSendViewContact();

        if(mCursor.moveToFirst()){
            String groupName="";
            String groupMessage="";
            Long groupDate=null;
            Long groupReserve=null;
            String groupLeader="";
            int groupSize=0;
            boolean find=false;

            // 그룹처리를 위한 어레이 리스트와 이름 임시저장
            do{
                //커서에 그룹이름이 없을경우
                if(mCursor.isNull(mCursor.getColumnIndex("groupname"))) {
                    //그룹내용이 없었다면
                    if(groupName.equals("")) {
                        if(mCursor.getString(mCursor.getColumnIndex("name")).contains(search))
                            DisplayContact(mCursor);
                    }
                    //그룹내용이 있었다면
                    else{
                        //저장내용을 이미지화시켜주고
                        if(find==true)
                            DisplayGroupContact(groupName,groupDate,groupReserve,groupMessage,groupLeader,groupSize);
                        groupName="";
                        groupMessage="";
                        groupDate=null;
                        groupReserve=null;
                        groupLeader="";
                        groupSize=0;
                        find=false;
                        if(mCursor.getString(mCursor.getColumnIndex("name")).contains(search))
                            DisplayContact(mCursor);
                    }
                }
                //커서에 그룹이름이 있다.
                else{
                    //이전 그룹내용이 저장된것이 없다
                    if(groupName.equals("")){
                        groupName=mCursor.getString(mCursor.getColumnIndex("groupname"));
                        groupDate=mCursor.getLong(mCursor.getColumnIndex("reportdate"));
                        groupReserve = mCursor.getLong(mCursor.getColumnIndex("reserveddate"));
                        groupMessage=mCursor.getString(mCursor.getColumnIndex("message"));
                        groupLeader=mCursor.getString(mCursor.getColumnIndex("name"));
                        if(mCursor.getString(mCursor.getColumnIndex("name")).contains(search)||mCursor.getString(mCursor.getColumnIndex("groupname")).contains(search))
                            find=true;
                    }
                    // 이전 그룹내용에 저장된값이 있다.
                    else {
                        // 그룹내용이 이전 저장된 내용과 같다.
                        if (mCursor.getString(mCursor.getColumnIndex("groupname")).equals(groupName)) {

                            if(mCursor.getString(mCursor.getColumnIndex("name")).contains(search))
                                find=true;

                            groupSize++;
                        }
                        // 저장된 이름은 있었으나 저장된 내용과는 다르다
                        else {
                            if(find==true)
                                DisplayGroupContact(groupName,groupDate, groupReserve, groupMessage, groupLeader,groupSize);
                            groupName=mCursor.getString(mCursor.getColumnIndex("groupname"));
                            groupDate=mCursor.getLong(mCursor.getColumnIndex("reportdate"));
                            groupReserve = mCursor.getLong(mCursor.getColumnIndex("reserveddate"));
                            groupMessage=mCursor.getString(mCursor.getColumnIndex("message"));
                            groupLeader=mCursor.getString(mCursor.getColumnIndex("name"));
                            groupSize=0;
                            if(mCursor.getString(mCursor.getColumnIndex("name")).contains(search) || mCursor.getString(mCursor.getColumnIndex("groupname")).contains(search))
                                find=true;
                            else find=false;
                        }
                    }
                }
            } while(mCursor.moveToNext());
            if(!groupName.equals("") && find==true){
                DisplayGroupContact(groupName,groupDate,groupReserve,groupMessage,groupLeader,groupSize);
            }
        }
        db.close();
        adapter.notifyDataSetChanged();
    }

    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }

    @Override
    public void onResume(){
        super.onResume();
        adapter.getListViewItemList().clear();
        showSend();
    }

}