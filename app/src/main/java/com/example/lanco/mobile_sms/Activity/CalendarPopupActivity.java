package com.example.lanco.mobile_sms.Activity;
/*
 When calendar date is clicked show list of event data
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.lanco.mobile_sms.CalendarListAdapter;
import com.example.lanco.mobile_sms.DB.DBCalendarManager;
import com.example.lanco.mobile_sms.R;


public class CalendarPopupActivity extends Activity {

    CalendarListAdapter adapter;//리스트 어댑터
    DBCalendarManager db;//달력디비
    Cursor c;
    int check = 0, pos;
    Button add;//추가버튼
    String[] tmp;
    String eventday;//이벤트 날짜 스트링
    AlertDialog.Builder builder;
    AlertDialog dialog;
    final CharSequence[] items = {"새 메세지", "삭제", "취소"};//다이얼로그 아이템

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//액티비티에 윈도우 창 제거 요청하기
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.7f;
        getWindow().setAttributes(layoutParams);//배경 부분을 어둡게 처리하기
        setContentView(R.layout.activity_calendar_popup);
        add = (Button) findViewById(R.id.add);
        db = new DBCalendarManager(getApplicationContext());
        Intent intent = getIntent();//홈엑티비티에서 받아온 인텐드를 가져온다
        Bundle b = intent.getExtras();
        eventday = b.getString("date");

        builder = new AlertDialog.Builder(this);//alert dialog(삭제 다이얼로그) 빌더 생성 및 클릭리스너 등록
        builder.setTitle("목록을 선택하세요").setItems(items, new DialogInterface.OnClickListener() {// 목록 클릭시 설정
            public void onClick(DialogInterface dialog, int index) {
                switch (index) {
                    case 0:
                        //달력 캘린더에 있는 값들을 writeactivity로 intent시켜 실행
                        Intent writeIntent = new Intent(getApplicationContext(),WriteActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", tmp[pos]);
                        writeIntent.putExtras(bundle);
                        startActivity(new Intent(writeIntent));
                        finish();
                        break;
                    case 1:
                        //달력 캘린더 디비 안의 데이터 삭제
                        db.open();
                        c = db.getAllContacts();
                        if (c.moveToFirst()) {
                            do {
                                if (c.getString(1).equals(tmp[pos]) && c.getString(2).equals(eventday))
                                    db.deleteContactName(tmp[pos]);
                            } while (c.moveToNext());
                        }
                        finish();
                        break;
                    case 2:
                        break;
                }
            }
        });

        dialog = builder.create();//alert dialog 생성
        adapter = new CalendarListAdapter();

        db.open();
        c = db.getAllContacts();
        tmp = new String[c.getCount()];
        if (c.moveToFirst()) {
            do {
                if (c.getString(2).equals(eventday))//날짜에 있는 사람들 리스트뷰에 추가
                    DisplayContact(c);
            } while (c.moveToNext());
        }
        db.close();

        final ListView listView = (ListView) findViewById(R.id.calList);
        LinearLayout empty = (LinearLayout)findViewById(R.id.empty);
        listView.setEmptyView(empty);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//리스트뷰 클릭시 다이얼로그 보여주기
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                dialog.show();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {//달력디비에 추가하는 창띄우기
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CalendarInputActivity.class));
                finish();
            }
        });
    }

    public void DisplayContact(Cursor c) {//리스트 뷰에 아이템 추가
        check++;
        tmp[check - 1] = c.getString(1);
        adapter.addItem(c.getString(1), c.getString(2));
    }
}