package com.example.lanco.mobile_sms.Activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.lanco.mobile_sms.AlarmReceiver;
import com.example.lanco.mobile_sms.DB.DBRotateManager;
import com.example.lanco.mobile_sms.DB.DBSingleManager;
import com.example.lanco.mobile_sms.GroupAdapter;
import com.example.lanco.mobile_sms.R;
import com.example.lanco.mobile_sms.SMSData;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

public class WriteGroupActivity extends AppCompatActivity {

    Context mContext;
    ImageButton btn_plus; // 이미지 버튼 - 연락처와 연동
    EditText group_name;
    Button btn_send; // 문자 보내기 버튼
    ImageButton btn_pBook;
    Button btn_input; // 이름 입력칸
    EditText edit_message; // 메세지 입력칸
    MaterialSpinner sort_spinner; // 분류를 보여주는 스피너창
    TextView display; // 시간을 보여주는 창
    GroupAdapter g_adapter;
    ListView sendList;
    String smsClass;
    String[] default_message;
    Switch rotateBtn;
    EditText rotateEdit;
    TextView rotateText;
    boolean rotateCheck = false;
    Random random;
    Intent popupIntent;

    Button pickDate, pickTime; // 날짜와 시간을 구하는 버튼창
    GregorianCalendar mCalendar; //현재날자, 시간 정보를 구하는 객체

    private SMSData sms;

    //날짜를 변경하는 객체
    DateFormat fmDateAndTime = DateFormat.getDateTimeInstance();//날짜시간정보를 표시하는 형식
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    //시간을 변경하는 객체
    TimePickerDialog.OnTimeSetListener time =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    mCalendar.set(Calendar.MINUTE, minute);
                    updateLabel();
                }
            };//시간을 변경하는 객체

    //날짜,시간을 보여주는 텍스트뷰의 업데이트
    public void updateLabel() {
        display.setText(
                fmDateAndTime.format(mCalendar.getTime()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_group);

        sms = new SMSData();

        g_adapter = new GroupAdapter();
        sendList = (ListView) findViewById(R.id.sendList);

        mContext = this;
        group_name = (EditText)findViewById(R.id.gEdit);
        btn_plus = (ImageButton) findViewById(R.id.pBtn);
        btn_send = (Button) findViewById(R.id.sBtn);
        btn_pBook = (ImageButton) findViewById(R.id.pbookBtn);
        btn_input = (Button) findViewById(R.id.inputBtn);
        edit_message = (EditText) findViewById(R.id.mEdit);
        display = (TextView) findViewById(R.id.display);
        pickDate = (Button) findViewById(R.id.pickDate);
        pickTime = (Button) findViewById(R.id.pickTime);
        sort_spinner = (MaterialSpinner) findViewById(R.id.class1);//기념일 분류
        rotateEdit = (EditText) findViewById(R.id.rotateEdit);
        rotateText = (TextView) findViewById(R.id.rotateText);
        rotateBtn = (Switch) findViewById(R.id.rotateBtn);
        rotateEdit.setVisibility(View.GONE);
        rotateText.setVisibility(View.GONE);

        rotateBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    rotateCheck = true;
                    rotateEdit.setVisibility(View.VISIBLE);
                    rotateText.setVisibility(View.VISIBLE);
                } else {
                    rotateCheck = false;
                    rotateEdit.setVisibility(View.GONE);
                    rotateText.setVisibility(View.GONE);
                }
            }
        });

        random = new Random();
        popupIntent = getIntent();

        sendList.setAdapter(g_adapter);

        if(popupIntent!=null){
            if(popupIntent.getParcelableArrayListExtra("HisGroupSMSModify")!=null){
                ArrayList<SMSData> modi_sms = new ArrayList<SMSData>();
                modi_sms = getIntent().getParcelableArrayListExtra("HisGroupSMSModify");
                group_name.setText(modi_sms.get(0).getGroupName());
                sort_spinner.setText(modi_sms.get(0).getSort());
                edit_message.setText(modi_sms.get(0).getMessage());
                for(int i=0;i<modi_sms.size();i++){
                    g_adapter.addItem(modi_sms.get(i).getRecipientName(), modi_sms.get(i).getRecipientNumber());
                    g_adapter.notifyDataSetChanged();
                    setGroupViewHeightBasedOnItems(sendList);
                }
            }

            if(popupIntent.getParcelableArrayListExtra("groupSMSModify")!=null){
                ArrayList<SMSData> modi_sms = new ArrayList<SMSData>();
                modi_sms = getIntent().getParcelableArrayListExtra("groupSMSModify");
                group_name.setText(modi_sms.get(0).getGroupName());
                sort_spinner.setText(modi_sms.get(0).getSort());
                edit_message.setText(modi_sms.get(0).getMessage());
                for(int i=0;i<modi_sms.size();i++){
                    g_adapter.addItem(modi_sms.get(i).getRecipientName(), modi_sms.get(i).getRecipientNumber());
                }
                g_adapter.notifyDataSetChanged();
                setGroupViewHeightBasedOnItems(sendList);
            }

            if(popupIntent.getParcelableArrayListExtra("groupSMSDelete")!=null){
                ArrayList<SMSData> del_sms = new ArrayList<SMSData>();
                del_sms = getIntent().getParcelableArrayListExtra("groupSMSDelete");
                DBSingleManager del_db = new DBSingleManager(this);
                del_db.open();
                for(int i=0;i<del_sms.size();i++) {
                    unscheduleAlarm(del_sms.get(i));
                    del_db.deleteContact(del_sms.get(i).getReportDate());
                }
                del_db.close();
                finish();
            }
        }

        sort_spinner.setItems("", "생일", "새해", "크리스마스", "추석", "어버이날", "스승의날", "기타");
        //기념일 선택했을 때
        sort_spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
                switch (position) {
                    case 1:
                        default_message = getResources().getStringArray(R.array.default_birthday);
                        edit_message.setText(default_message[random.nextInt(default_message.length)]);
                        break;
                    case 2:
                        default_message = getResources().getStringArray(R.array.default_year);
                        edit_message.setText(default_message[random.nextInt(default_message.length)]);
                        break;
                    case 3:
                        default_message = getResources().getStringArray(R.array.default_xmas);
                        edit_message.setText(default_message[random.nextInt(default_message.length)]);
                        break;
                    case 4:
                        default_message = getResources().getStringArray(R.array.default_chu);
                        edit_message.setText(default_message[random.nextInt(default_message.length)]);
                        break;
                    case 5:
                        default_message = getResources().getStringArray(R.array.default_parent);
                        edit_message.setText(default_message[random.nextInt(default_message.length)]);
                        break;
                    case 6:
                        default_message = getResources().getStringArray(R.array.default_teacher);
                        edit_message.setText(default_message[random.nextInt(default_message.length)]);
                        break;
                }
                smsClass = item;

            }
        });
        sort_spinner.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {


            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
                Snackbar.make(spinner, "Nothing selected", Snackbar.LENGTH_LONG).show();
            }
        });

        g_adapter.setOnButtonClickListener(new GroupAdapter.onButtonClickListener() {
            @Override
            public void onDelete(int a) {
                g_adapter.remove(a);
                g_adapter.notifyDataSetChanged();
                setGroupViewHeightBasedOnItems(sendList);
            }
        });
    }

    //리스트뷰의 사이즈를 동적으로 늘려줌 - 직접 높이에 대해 계산을 통해 처리함
    public void setGroupViewHeightBasedOnItems(ListView v){
        ListAdapter listAdapter = v.getAdapter();
        if(listAdapter==null) return;

        int numOfItems = listAdapter.getCount();
        int totalItemsHeight = 0;
        for(int i=0;i<numOfItems;i++){
            View item = listAdapter.getView(i,null,v);
            item.measure(0,0);
            totalItemsHeight += item.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = v.getLayoutParams();
        params.height = totalItemsHeight;
        v.setLayoutParams(params);
        v.requestLayout();
    }

    public void pBookClick(View v) {
        Intent myActivity2 = new Intent(Intent.ACTION_PICK);
        myActivity2.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(myActivity2, 22);
    }
    public void inputClick(View v){
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_input_popup, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("직접 번호 입력");
        builder.setIcon(R.drawable.pencil);
        builder.setView(dialogView);
        builder.setPositiveButton("입력", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText input_name = (EditText) dialogView.findViewById(R.id.input_name);
                EditText input_phone = (EditText) dialogView.findViewById(R.id.input_phone);
                String pop_name = input_name.getText().toString();
                String pop_phone = input_phone.getText().toString();
                if(pop_name.equals("")||pop_name==null||pop_phone.equals("")||pop_phone==null) {
                    Toast.makeText(getApplicationContext(),"빈칸을 채워주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }
                g_adapter.addItem(pop_name, pop_phone);
                g_adapter.notifyDataSetChanged();
                setGroupViewHeightBasedOnItems(sendList);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    //Processing permission for SDK 23 Version(Android 6.0)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == getPackageManager().PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "SMS 권한 거부됨.", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    public void dateSet(View v) {
        mCalendar = new GregorianCalendar();
        DatePickerDialog dpd = new DatePickerDialog(this, date, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        dpd.show();

    }

    public void timeSet(View v) {
        mCalendar = new GregorianCalendar();
        TimePickerDialog tpd = new TimePickerDialog(this, time, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);
        tpd.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 22:
                if (resultCode == RESULT_OK) {
                    Cursor cursor = getContentResolver().query(data.getData(),
                            new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}, null, null, null);
                    cursor.moveToFirst();
                    String number = cursor.getString(0);
                    String name = cursor.getString(1);//0은 번호 / 1은 이름
                    g_adapter.addItem(name, number);
                    g_adapter.notifyDataSetChanged();
                    setGroupViewHeightBasedOnItems(sendList);
                    cursor.close();
                }
                break;
            case 33:
                if(resultCode==RESULT_OK){
                    Bundle popResult = data.getExtras();
                    String pop_name = popResult.getString("inputName");
                    String pop_phone = popResult.getString("inputPhone");
                    g_adapter.addItem(pop_name, pop_phone);
                    g_adapter.notifyDataSetChanged();
                    setGroupViewHeightBasedOnItems(sendList);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 시간확인 후 메세지 전송
    public void sendClick(View v) {
        ArrayList<String> g_name = new ArrayList<String>();
        ArrayList<String> g_phone = new ArrayList<String>();
        if(g_adapter.getCount()<2){
            Toast.makeText(getApplicationContext(),"2명 이상의 인원을 추가해주세요",Toast.LENGTH_SHORT).show();
            return;
        }
        for(int i=0;i<g_adapter.getCount();i++){
            g_name.add(g_adapter.getItem(i).getGName());
            g_phone.add(g_adapter.getItem(i).getGPhone());
        }

        if(group_name.getText().toString().equals("")|| group_name.getText().toString()==null){
            Toast.makeText(getApplicationContext(), "그룹 이름을 작성해주세요!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mCalendar.getTimeInMillis()<GregorianCalendar.getInstance().getTimeInMillis()){
            Toast.makeText(getApplicationContext(), "현재 시간보다 낮아요!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(edit_message.getText().toString().equals("")|| edit_message.getText().toString()==null){
            Toast.makeText(getApplicationContext(), "내용을 작성해주세요!", Toast.LENGTH_SHORT).show();
            return;
        }

        String smsText = edit_message.getText().toString();
        String smsGroup = group_name.getText().toString();
        long time_stamp = Calendar.getInstance().getTimeInMillis();
        long curr_time = Calendar.getInstance().getTimeInMillis();

        for(int i=0;i<g_adapter.getCount();i++) {
            sms.setReportDate(time_stamp);
            sms.setReservedDate(mCalendar.getTimeInMillis());
            sms.setGroupName(smsGroup);
            sms.setRecipientName(g_name.get(i));
            sms.setRecipientNumber(g_phone.get(i));
            sms.setMessage(smsText);
            sms.setSort(smsClass);
            sms.setStatus(SMSData.STATUS_PENDING);

            // 주기 값에 해당하는 디비를 저장
            if (rotateCheck == true) {
                if (rotateEdit.getText().toString() == null || rotateEdit.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "주기를 설정해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    int val = Integer.parseInt(rotateEdit.getText().toString());
                    DBRotateManager rdb = new DBRotateManager(this);
                    rdb.open();
                    rdb.insertContact(curr_time,val);
                    rdb.close();
                }
            }

            DBSingleManager db = new DBSingleManager(this);
            db.open();
            long a = db.insertContact(sms);
            Log.v("알림", sms.getRecipientName() + " " + sms.getReportDate() + " " + sms.getReservedDate());

            db.close();
        }
        scheduleAlarm(sms);


        finish();
    }

    private void scheduleAlarm(SMSData sms) {
        if(popupIntent.getParcelableArrayListExtra("groupSMSDelete")!=null){
            ArrayList<SMSData> del_sms = new ArrayList<SMSData>();
            del_sms = getIntent().getParcelableArrayListExtra("groupSMSDelete");
            DBSingleManager del_db = new DBSingleManager(this);
            del_db.open();
            for(int i=0;i<del_sms.size();i++) {
                unscheduleAlarm(del_sms.get(i));
                del_db.deleteContact(del_sms.get(i).getReportDate());
            }
            del_db.close();
            finish();
        }
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, sms.getReservedDate(), getAlarmPendingIntent(sms));
    }

    // 예약된 sms의 알람 지울때 쓰는 것
    private void unscheduleAlarm(SMSData sms) {
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(getAlarmPendingIntent(sms));
    }

    //알람리시버를 통해 서비스 등록
    private PendingIntent getAlarmPendingIntent(SMSData sms) {
        Intent intent = new Intent(AlarmReceiver.INTENT_FILTER);
        intent.putExtra("ReportData", sms.getReportDate());
        return PendingIntent.getBroadcast(this, sms.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT & Intent.FILL_IN_DATA);
    }

}
