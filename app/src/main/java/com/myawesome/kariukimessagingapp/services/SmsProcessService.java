package com.myawesome.kariukimessagingapp.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;

import androidx.annotation.Nullable;

import com.myawesome.kariukimessagingapp.activities.USSDAutomationActivity;

public class SmsProcessService extends Service {
    SmsReceiver smsReceiver = new SmsReceiver();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

        return START_STICKY;
    }

    private class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String telnr = "", message = "";

            Bundle extras = intent.getExtras();

            if (extras != null) {
                Object[] pdus = (Object[]) extras.get("pdus");
                if (pdus != null) {

                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = getIncomingMessage(pdu, extras);
                        telnr = smsMessage.getDisplayOriginatingAddress();
                        message += smsMessage.getDisplayMessageBody();
                    }

// Here the message content is processed within MainAct
//                    MainActivity.instance().processSMS(telnr.replace("+49", "0").replace(" ", ""), nachricht);

                    if(telnr.matches("MPESA")){
                        if(message.contains("received")&&message.contains("Confirmed.on")) {
                            Intent intent_ussd = new Intent(context, USSDAutomationActivity.class);
                            intent_ussd.putExtra("message", message);
                            startActivity(intent_ussd);
                        }
                    }

                }
            }
        }

        private SmsMessage getIncomingMessage(Object object, Bundle bundle) {
            SmsMessage smsMessage;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String format = bundle.getString("format");
                smsMessage = SmsMessage.createFromPdu((byte[]) object, format);
            } else {
                smsMessage = SmsMessage.createFromPdu((byte[]) object);
            }

            return smsMessage;
        }
    }
}
