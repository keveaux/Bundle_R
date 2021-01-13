package com.myawesome.kariukimessagingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.HashMap;

public class TransactionReceiver extends BroadcastReceiver {

    public TransactionReceiver() { }

    @Override
    public void onReceive(Context context, Intent intent) {
        String uuid = intent.getStringExtra("uuid");
        String confirmationCode, balance;

        String status=intent.getStringExtra("status");

        Toast.makeText(context, "status "+status, Toast.LENGTH_SHORT).show();

        if (intent.hasExtra("transaction_extras")) {
            HashMap<String, String> t_extras = (HashMap<String, String>) intent.getSerializableExtra("transaction_extras");
            if (t_extras.containsKey("confirmCode"))
                confirmationCode = t_extras.get("confirmCode");
            if (t_extras.containsKey("balance")){
                balance = t_extras.get("balance");
                Toast.makeText(context, ""+balance, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
