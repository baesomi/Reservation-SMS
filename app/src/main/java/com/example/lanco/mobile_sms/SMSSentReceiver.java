package com.example.lanco.mobile_sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class SMSSentReceiver extends WakefulBroadcastReceiver {

    static public final String RESULT_CODE = "resultCode";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, SMSSentService.class);
        service.putExtras(intent.getExtras());
        service.putExtra(RESULT_CODE, getResultCode());
        startWakefulService(context, service);
    }
}
