package com.example.lanco.mobile_sms;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.lanco.mobile_sms.DB.DBSingleManager;

import java.util.ArrayList;

public class SMSSendService extends IntentService {

    public static final String KEY_REPORT_DATE = "reportdate"; // 작성일
    public static final String KEY_RESERVED_DATE = "reserveddate"; // 예약된 날짜
    public static final String KEY_NAME = "name";  // 이름
    public static final String KEY_PHONE = "phone"; // 번호
    public static final String KEY_MESSAGE = "message"; // 내용
    public static final String KEY_SORT = "sort"; // 분류
    public static final String KEY_STATUS = "status"; // 상태
    public static final String KEY_RESULT = "result"; // 결과

    public SMSSendService() {
        super("SMSSendService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SMSData c;
        long smsId = intent.getExtras().getLong("ReportData", 0);
        if(smsId==0){
            throw new RuntimeException("No SMS id provided with intent");
        }

        DBSingleManager db = new DBSingleManager(this);
        db.open();
        Cursor cur = db.getReportContact(smsId);
        if(cur.getCount()!=1)
        {
            do{
                c = db.getSMSData(cur);
                Log.v("알림SendService", c.getRecipientName() + " " + c.getReportDate() + " " + c.getReservedDate());
                sendSMS(c);
            }while(cur.moveToNext());
        }
        else{
            c = db.getSMSData(cur);
            sendSMS(c);
        }

        db.close();

    }

    private void sendSMS(SMSData sms) {


        //-----------------------------------
        int num;
        SharedPreferences sp;
        SharedPreferences.Editor e;
        sp=getSharedPreferences("num",MODE_PRIVATE);
        e = sp.edit();
        num = sp.getInt("num",0);
        //-----------------------------------

        Long smsId = sms.getReportDate();
        String smsN = sms.getRecipientName();

        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPendingIntents = null;

        Intent sentIntent = new Intent(this, SMSSentReceiver.class);
        sentIntent.setAction(smsId.toString());
        sentIntent.putExtra("ReportData", smsId);
        sentIntent.putExtra("ReportName", smsN);
        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, num, sentIntent, 0);
        e.putInt("num",num+1);
        e.commit();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> mSMSMessage = smsManager.divideMessage(sms.getMessage());
        for (int i = 0; i < mSMSMessage.size(); i++) {
            sentPendingIntents.add(i, sentPendingIntent);
        }
        smsManager.sendMultipartTextMessage(sms.getRecipientNumber(), null, mSMSMessage, sentPendingIntents, null);
    }
}
