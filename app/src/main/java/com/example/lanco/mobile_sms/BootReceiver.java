package com.example.lanco.mobile_sms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.example.lanco.mobile_sms.DB.DBSingleManager;

import java.util.ArrayList;
import java.util.Iterator;

public class BootReceiver extends BroadcastReceiver {
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            this.context = context;
            ArrayList<SMSData> pendingSms = getPendingSms();
            Iterator<SMSData> i = pendingSms.iterator();
            SMSData sms;
            while (i.hasNext()) {
                sms = i.next();
                scheduleAlarm(sms);
            }
        }
    }

    private ArrayList<SMSData> getPendingSms() {
        DBSingleManager db = new DBSingleManager(context);
        ArrayList<SMSData> result = new ArrayList<SMSData>();
        db.open();
        Cursor c = db.getStatusCursor(SMSData.STATUS_PENDING);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    result.add(db.getSMSData(c));
                } while (c.moveToNext());
            }
            return result;
        }
        return null;
    }

    private void scheduleAlarm(SMSData sms) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AlarmReceiver.INTENT_FILTER);
        intent.putExtra("ReportData", sms.getReportDate());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, sms.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT & Intent.FILL_IN_DATA);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, sms.getReservedDate(), alarmIntent);
    }
}
