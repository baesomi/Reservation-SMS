package com.example.lanco.mobile_sms.Activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.lanco.mobile_sms.AlarmReceiver;
import com.example.lanco.mobile_sms.DB.DBRotateManager;
import com.example.lanco.mobile_sms.DB.DBSingleManager;
import com.example.lanco.mobile_sms.R;
import com.example.lanco.mobile_sms.SMSData;
import com.jaredrummler.materialspinner.MaterialSpinner;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

public class WriteActivity extends AppCompatActivity {

    Context mContext;
    ImageButton btn_plus; // 이미지 버튼 - 연락처와 연동
    Button btn_send; // 문자 보내기 버튼
    EditText edit_name; // 이름
    EditText edit_phone; // 번호 입력칸
    EditText edit_message; // 메세지 입력칸
    MaterialSpinner sort_spinner; // 분류를 보여주는 스피너창
    TextView display; // 시간을 보여주는 창
    Switch rotateBtn;
    EditText rotateEdit;
    TextView rotateText;
    boolean rotateCheck = false;
    String smsClass;
    String[] default_message;
    Random random;
    Intent calendarIntent;
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
        setContentView(R.layout.activity_write);

        sms = new SMSData();

        mContext = this;
        btn_plus = (ImageButton) findViewById(R.id.pBtn);
        btn_send = (Button) findViewById(R.id.sBtn);
        edit_name = (EditText) findViewById(R.id.nEdit);
        edit_phone = (EditText) findViewById(R.id.pEdit);
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
        calendarIntent = getIntent();
        popupIntent = getIntent();
        if (calendarIntent != null && calendarIntent.getExtras() != null) {
            Bundle b = calendarIntent.getExtras();
            edit_name.setText(b.getString("name"));
        }
        //
        if (popupIntent != null) {
            if (popupIntent.getParcelableArrayListExtra("HisSMSModify") != null) {
                ArrayList<SMSData> modi_sms = new ArrayList<SMSData>();
                modi_sms = getIntent().getParcelableArrayListExtra("HisSMSModify");
                edit_name.setText(modi_sms.get(0).getRecipientName());
                edit_phone.setText(modi_sms.get(0).getRecipientNumber());
                sort_spinner.setText(modi_sms.get(0).getSort());
                edit_message.setText(modi_sms.get(0).getMessage());
            }

            if (popupIntent.getParcelableArrayListExtra("SMSModify") != null) {
                ArrayList<SMSData> modi_sms = new ArrayList<SMSData>();
                modi_sms = getIntent().getParcelableArrayListExtra("SMSModify");
                edit_name.setText(modi_sms.get(0).getRecipientName());
                edit_phone.setText(modi_sms.get(0).getRecipientNumber());
                sort_spinner.setText(modi_sms.get(0).getSort());
                edit_message.setText(modi_sms.get(0).getMessage());
            }

            if (popupIntent.getParcelableArrayListExtra("SMSDelete") != null) {
                ArrayList<SMSData> del_sms = new ArrayList<SMSData>();
                del_sms = getIntent().getParcelableArrayListExtra("SMSDelete");
                unscheduleAlarm(del_sms.get(0));
                DBSingleManager del_db = new DBSingleManager(this);
                del_db.open();
                del_db.deleteContact(del_sms.get(0).getReportDate());
                del_db.close();
                finish();
            }
        }

        sort_spinner.setHint("분류");

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
    }

    //Processing permission for SDK 23 Version(Android 6.0)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == getPackageManager().PERMISSION_GRANTED) {
                    //Toast.makeText(this, "SMS 권한을 사용자가 승인함.", Toast.LENGTH_SHORT).show();
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

    //
    public void plusClick(View v) {
        Intent myActivity2 = new Intent(Intent.ACTION_PICK);
        myActivity2.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(myActivity2, 22);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Cursor cursor = getContentResolver().query(data.getData(),
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}, null, null, null);
            cursor.moveToFirst();
            String number = cursor.getString(0);
            String name = cursor.getString(1);//0은 번호 / 1은 이름
            edit_name.setText(name);
            edit_phone.setText(number);
            cursor.close();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 시간확인 후 메세지 전송
    public void sendClick(View v) {
        String smsName = edit_name.getText().toString();

        if (mCalendar.getTimeInMillis() < GregorianCalendar.getInstance().getTimeInMillis()) {
            Toast.makeText(getApplicationContext(), "현재 시간보다 낮아요!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (edit_phone.getText().toString().equals("") || edit_phone.getText().toString() == null) {
            Toast.makeText(getApplicationContext(), "번호를 작성해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (edit_message.getText().toString().equals("") || edit_message.getText().toString() == null) {
            Toast.makeText(getApplicationContext(), "내용을 작성해주세요!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (edit_name.getText().toString() == null || edit_name.getText().toString().equals("")) {
            smsName = edit_phone.getText().toString();
        }

        // 빈 공백이 있는지 확인해주는 변수들

        String smsNum = edit_phone.getText().toString();
        String smsText = edit_message.getText().toString();
        long curr_time = Calendar.getInstance().getTimeInMillis();

        sms.setReportDate(curr_time);
        sms.setReservedDate(mCalendar.getTimeInMillis());
        sms.setRecipientName(smsName);
        sms.setRecipientNumber(smsNum);
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
        db.close();

        scheduleAlarm(sms);

        finish();
    }

    private void scheduleAlarm(SMSData sms) {
        if (popupIntent.getParcelableArrayListExtra("SMSModify") != null) {
            ArrayList<SMSData> del_sms = new ArrayList<SMSData>();
            del_sms = getIntent().getParcelableArrayListExtra("SMSModify");
            unscheduleAlarm(del_sms.get(0));
            DBSingleManager del_db = new DBSingleManager(this);
            del_db.open();
            del_db.deleteContact(del_sms.get(0).getReportDate());
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
        return PendingIntent.getBroadcast(
                this,
                sms.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT & Intent.FILL_IN_DATA
        );
    }
}
