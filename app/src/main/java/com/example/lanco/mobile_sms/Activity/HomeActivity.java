package com.example.lanco.mobile_sms.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.lanco.mobile_sms.DB.DBCalendarManager;
import com.example.lanco.mobile_sms.DB.DBSingleManager;
import com.example.lanco.mobile_sms.MyYAxisValueFormatter;
import com.example.lanco.mobile_sms.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HomeActivity extends Fragment {
    DBCalendarManager db;
    Calendar calendar = Calendar.getInstance();
    protected BarChart mChart;
    int checkChart = 1;
    ToggleButton changeBtn;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);
        mChart = (BarChart) view.findViewById(R.id.chart1);//바차트 생성

        mChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });//그래프 클릭이벤트 없애기
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);


        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(1);

        YAxisValueFormatter custom = new MyYAxisValueFormatter();

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setLabelCount(0, true);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(1f);
        leftAxis.setAxisMinValue(0);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(0, true);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(1f);
        rightAxis.setAxisMinValue(0);

        changeBtn = (ToggleButton) view.findViewById(R.id.toggleButton);
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//날짜 별 통계인지 월 별 통계인지 확인
                if (changeBtn.isChecked()) {
                    checkChart = 0;
                    setData(4);
                } else {
                    checkChart = 1;
                    setData(7);
                }
                mChart.invalidate();
            }
        });

        if(checkChart == 1) {//날짜별 통계
            setData(7);
        }
        else {//월별 통계
            setData(4);
        }
        CalendarView calendarView = (CalendarView) view.findViewById(R.id.calendarView);
        db = new DBCalendarManager(getContext());

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {//선택된 날짜를 달력 팝업에 전달하여 실행
                String date;
                if (month < 9) {//월이 두글자일 경우
                    if (dayOfMonth < 10)
                        date = "0" + String.valueOf(month + 1) + "/" + "0" + String.valueOf(dayOfMonth);
                    else
                        date = "0" + String.valueOf(month + 1) + "/" + String.valueOf(dayOfMonth);
                } else {//월이 두글자 이하일 경우
                    if (dayOfMonth < 10)
                        date = String.valueOf(month + 1) + "/" + "0" + String.valueOf(dayOfMonth);
                    else
                        date = String.valueOf(month + 1) + "/" + String.valueOf(dayOfMonth);
                }
                Intent intent = new Intent(getContext(), CalendarPopupActivity.class);
                Bundle b = new Bundle();
                b.putString("date", date);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        return view;
    }

    private void setData(int count) {//그래프안의 데이터를 넣는 작업
        String[] arrDate = new String[7];
        String[] arrMonth = new String[4];
        SimpleDateFormat sdf, sdfMonth;
        sdf = new SimpleDateFormat("MM/dd");
        sdfMonth = new SimpleDateFormat("MM");

        ArrayList<String> xVals = new ArrayList<String>();

        if (checkChart == 0) {//월별일 경우
            SimpleDateFormat a = new SimpleDateFormat("MM월");
            calendar.add(Calendar.MONTH, -count);
            for (int i = 0; i < count; i++) {
                calendar.add(Calendar.MONTH, 1);
                arrMonth[i] = sdfMonth.format(calendar.getTime());
                xVals.add(a.format(calendar.getTime()));
            }
        } else if (checkChart == 1) {//날짜별일 경우
            calendar.add(Calendar.DATE, -count);
            for (int i = 0; i < count; i++) {
                calendar.add(Calendar.DATE, 1);
                arrDate[i] = sdf.format(calendar.getTime());
                xVals.add(arrDate[i]);
            }
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        String tmp = "";
        long dayMili;
        DBSingleManager db = new DBSingleManager(getContext());

        if (checkChart == 0) {//월별일 경우
            int[] messageCount = new int[count];
            db.open();
            Cursor c = db.getAllContacts();
            if (c.moveToNext()) {
                do {
                    dayMili = Long.valueOf(c.getString(c.getColumnIndex("reportdate")));
                    tmp = checkDate(dayMili, "MM");
                    for (int i = 0; i < count; i++) {
                        if (arrMonth[i].equals(tmp)) {
                            messageCount[i]++;
                        }
                    }
                }
                while (c.moveToNext());
                tmp = "";
            }
            for (int i = 0; i < count; i++) {
                yVals1.add(new BarEntry(messageCount[i], i));
            }
            db.close();
        } else if (checkChart == 1) {//날짜별일 경우
            int[] messageCount = new int[count];
            db.open();
            Cursor c = db.getAllContacts();
            if (c.moveToNext()) {
                do {
                    dayMili = Long.valueOf(c.getString(c.getColumnIndex("reportdate")));
                    tmp = checkDate(dayMili, "MM/dd");
                    for (int i = 0; i < count; i++) {
                        if (arrDate[i].equals(tmp)) {
                            messageCount[i]++;
                        }
                    }
                }
                while (c.moveToNext());
                tmp = "";
            }
            for (int i = 0; i < count; i++) {
                yVals1.add(new BarEntry(messageCount[i], i));
            }
            db.close();
        }

        BarDataSet set1;

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {//모든게 비어있을때 null 그래프생성
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setYVals(yVals1);
            mChart.getData().setXVals(xVals);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {//y축과 x축에 데이터 추가
            set1 = new BarDataSet(yVals1, "보낸문자");
            set1.setBarSpacePercent(30f);
            set1.setColors(new int[]{rgb("#ff8e49")});

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(xVals, dataSets);
            data.setValueTextSize(15f);
            data.setValueTextColor(rgb("#000"));

            mChart.setData(data);
        }
    }

    public String checkDate(long miliSeconds, String dateFormat) {//날짜 변환
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(miliSeconds);
        return format.format(cal.getTime());
    }

    public static int rgb(String hex) {//rgb 변환
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }

}