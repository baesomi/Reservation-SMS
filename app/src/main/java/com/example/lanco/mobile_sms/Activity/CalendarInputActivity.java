package com.example.lanco.mobile_sms.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lanco.mobile_sms.DB.DBCalendarManager;
import com.example.lanco.mobile_sms.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
/*
 Add event date into Calendar
 */

public class CalendarInputActivity extends AppCompatActivity {
    GregorianCalendar calendar;//그레고리안 달력
    String eventDate = "", name;//디비에 저장할 데이터
    TextView tb;
    DatePickerDialog.OnDateSetListener date;//날짜선택
    EditText nameText;
    Button addBtn;
    DBCalendarManager db;//달력 디비
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_input);
        tb = (TextView)findViewById(R.id.tb);
        nameText = (EditText)findViewById(R.id.nameInput);
        addBtn = (Button)findViewById(R.id.addBtn);
        db = new DBCalendarManager(getApplicationContext());

        date = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {//데이트픽커 선택시
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH,monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if(monthOfYear < 8) {//월이 두자리 숫자가 아닐경우
                    if (dayOfMonth < 10)
                        eventDate = "0" + (monthOfYear + 1) + "/" + "0" + dayOfMonth;
                    else
                        eventDate = "0" + (monthOfYear + 1) + "/" + dayOfMonth;
                }
                else {//월이 두자리 숫자 이하일 경우
                    if(monthOfYear < 8)
                        eventDate = (monthOfYear + 1) + "/" + "0" + dayOfMonth;
                    else
                        eventDate = (monthOfYear + 1) + "/" + dayOfMonth;
                }
                Toast.makeText(getApplicationContext(),eventDate,Toast.LENGTH_SHORT).show();
            }
        };
        //add event date to Calendar DB
        addBtn.setOnClickListener(new View.OnClickListener() {//추가 버튼 클릭시 데이터 베이스에 저장
            @Override
            public void onClick(View v) {
                if(nameText != null && eventDate != "") {
                    name = nameText.getText().toString();
                    db.open();
                    db.insertContact(name, eventDate);
                    db.close();
                    eventDate = "";
                    Toast.makeText(getApplicationContext(),"추가완료",Toast.LENGTH_SHORT).show();
                    finish();
                }
                else//날짜 선택이 안돼 있을시
                    Toast.makeText(getApplicationContext(),"모두 입력해 주세요",Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void setDate(View view) {//데이터 픽커 다이얼로그 생성창
        calendar = new GregorianCalendar();
        DatePickerDialog dpd = new DatePickerDialog(this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }
}
