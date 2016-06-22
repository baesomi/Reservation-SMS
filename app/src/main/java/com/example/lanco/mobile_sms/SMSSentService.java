package com.example.lanco.mobile_sms;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.lanco.mobile_sms.Activity.MainActivity;
import com.example.lanco.mobile_sms.DB.DBRotateManager;
import com.example.lanco.mobile_sms.DB.DBSingleManager;

import java.util.ArrayList;
import java.util.Calendar;

public class SMSSentService extends IntentService {

    Context mContext = this;
    SharedPreferences sharedpreferences ;
    SharedPreferences sharedpreferences2;
    public SMSSentService() {
        super("SMSSentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sharedpreferences =getSharedPreferences("getPosition", MODE_PRIVATE);
        SMSData sms = new SMSData();
        long smsId = intent.getExtras().getLong("ReportData",0);
        String smsN = intent.getExtras().getString("ReportName");
        if (smsId == 0) {
            throw new RuntimeException("No SMS id provided with intent");
        }

        DBSingleManager db = new DBSingleManager(this);
        db.open();
        Cursor c = db.getReportContact(smsId);

        if(c.moveToFirst() && c.getCount()!=1){
            do{
                if(c.getString(c.getColumnIndex("name")).equals(smsN)){
                    sms = db.getSMSData(c);
                    Log.v("알림SentService2-1", smsId + " " + smsN + " " + sms.getStatus());
                }
            }while(c.moveToNext());
        }
        else{
            sms = db.getSMSData(c);
        }

        String errorId = "";
        String errorString = "";
        String title = "메세지 전송에 실패했습니다.";
        String message = "";
        sms.setStatus(SMSData.STATUS_FAILED);

        switch (intent.getExtras().getInt(SMSSentReceiver.RESULT_CODE, 0)) {
            case Activity.RESULT_OK:
                title = "메세지 전송완료!";
                message = sms.getRecipientName()+" 님에게 전송을 완료하였습니다.";
                sms.setStatus(SMSData.STATUS_SENT);
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                errorId = SMSData.ERROR_GENERIC;
                errorString = "내부적 전송 실패";
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                errorId = SMSData.ERROR_NO_SERVICE;
                errorString = "서비스 지역이 아닙니다.";
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                errorId = SMSData.ERROR_NULL_PDU;
                errorString = "PDU 실패";
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                errorId = SMSData.ERROR_RADIO_OFF;
                errorString = "무선(Radio)가 꺼져있습니다.";
                break;
            default:
                errorId = SMSData.ERROR_UNKNOWN;
                errorString = "알수없는 오류";
                break;
        }
        if (errorId.length() > 0) {
            sms.setResult(errorId);
            message = sms.getRecipientName()+" 님에게 전송을 실패했습니다 : "+errorString;
        }
        Log.v("알림SentService2", smsId + " " + smsN + " " + sms.getStatus());

        db.updateContact(sms);

        if(sharedpreferences.getInt("num",0)==1) {
            sendNotification(title, message);
        }
        if(sharedpreferences.getInt("num",0)==0){}
        db.close();
////////////////////////
        int find = 0;
        long now_date = Calendar.getInstance().getTimeInMillis();

        DBRotateManager rdb = new DBRotateManager(this);

        rdb.open();
        Cursor r_c = rdb.getAllContacts();
        int val=0;
        long changedDate=0;
        if(r_c.moveToFirst() && r_c.getCount()>0){
            do{
                if(r_c.getLong(r_c.getColumnIndex("rreportdate"))==sms.getReportDate()){
                    val = r_c.getInt(r_c.getColumnIndex("period"));
                    find=1;
                }
            }while(c.moveToNext());
        }

        if(find==1) {
            changedDate = sms.getReservedDate() + (val * 1000 * 60 * 60 * 24);
            rdb.insertContact(now_date, val);
        }
        rdb.close();
        if(find==1) {
            db.open();
            sms.setReportDate(now_date);
            sms.setReservedDate(changedDate);
            sms.setResult(null);
            sms.setStatus("PENDING");
            db.insertContact(sms);
            db.close();
        }
    }

    //////////////////////
    private void scheduleAlarm(SMSData sms) {
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
////////////////////////////

    public void sendNotification(String title, String message) {
        //When user click the notification, go to the History activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("MotificationIntent", 1);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

/////////////////

//class definition for screen on
        WakeUpScreen ws = new WakeUpScreen();

//In setting, detail alarm setting data
        // keyname ="set"
        // 1 = sound+ vibrator
        // 2 = vibrator
        // 3 = no sound, no vibrator

        sharedpreferences2 = getSharedPreferences("getP",MODE_PRIVATE);
        NotificationCompat.Builder notificationBuilder = notificationBuilder= null;
        if(sharedpreferences2.getInt("set",1)==1) {
            //When received notification , Turn on the screen during 10000 ms
            ws.acquire(mContext, 10000);
            //forced turn on sound mode
            AudioManager audioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.sms_icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setTicker(message)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE| Notification.DEFAULT_LIGHTS);

        }
//Only viberate
        if(sharedpreferences2.getInt("set",2)==2) {
            AudioManager audioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            //When received notification , Turn on the screen during 10000 ms
            ws.acquire(mContext, 10000);
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.sms_icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setTicker(message)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE);;

        }

//No sound but alarm


        if(sharedpreferences2.getInt("set", 3)==3) {

            //When received notification , Turn on the screen during 10000 ms
            ws.acquire(mContext, 10000);
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.sms_icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setTicker(message)
                    .setContentIntent(pendingIntent);
        }


        /////
        NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    ///////////////////////////////////////////////////////////Notification with screen on class
    protected class WakeUpScreen {

        private PowerManager.WakeLock wakeLock;

        /**
         * timeout을 설정하면, 자동으로 릴리즈됨
         * @param context
         * @param timeout
         */
        public void acquire(Context context, long timeout) {

            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP  |
                            PowerManager.FULL_WAKE_LOCK         |
                            PowerManager.ON_AFTER_RELEASE
                    , context.getClass().getName());

            if(timeout > 0)
                wakeLock.acquire(timeout);
            else
                wakeLock.acquire();

        }

    }
///////////////////////////////////////////////////
}