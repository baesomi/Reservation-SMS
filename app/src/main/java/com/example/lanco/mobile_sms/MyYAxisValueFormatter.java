package com.example.lanco.mobile_sms;

/**
 * Created by kjy79 on 2016-05-30.
 */
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.text.DecimalFormat;

public class MyYAxisValueFormatter implements YAxisValueFormatter {

    private DecimalFormat mFormat;

    public MyYAxisValueFormatter() {
        mFormat = new DecimalFormat("###,###,###,##0.0");
    }//y축 숫자 모양

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return mFormat.format(value);
    }//y축 형태 반환
}
