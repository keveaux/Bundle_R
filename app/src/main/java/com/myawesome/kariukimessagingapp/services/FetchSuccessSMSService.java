package com.myawesome.kariukimessagingapp.services;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.myawesome.kariukimessagingapp.activities.BlankSetupHoverActivity;
import com.myawesome.kariukimessagingapp.activities.USSDAutomationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FetchSuccessSMSService extends Service {
    SmsReceiver smsReceiver = new SmsReceiver();

    String amount_choice_one,amount_choice_two;
    String amount;

    Context context2;

    private static final String URL = "http://www.patridge.co.ke/database/addToScheduler.php";

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
            context2=context;

            Bundle extras = intent.getExtras();


            if (extras != null) {
                Object[] pdus = (Object[]) extras.get("pdus");
                if (pdus != null) {

                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = getIncomingMessage(pdu, extras);
                        telnr = smsMessage.getDisplayOriginatingAddress();
                        message += smsMessage.getDisplayMessageBody();
                    }

//                    Here the message content is processed within MainAct
//                    MainActivity.instance().processSMS(telnr.replace("+49", "0").replace(" ", ""), nachricht);

                    SharedPreferences shref;
                    shref = context.getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);

                    if(telnr.matches("Safaricom")){
                        if(message.contains("You have purchased")) {
//                           Intent intent1=new Intent(context,  BlankSetupHoverActivity.class);
//                           startActivity(intent1);



                           String number = message.substring(message.indexOf("for"), message.indexOf("."));
                           number = number.replaceAll("[^0-9]","");
                           String name = getContactName(context,number);

                           String sms_to_be_sent = loadData(context);

                           if(name.contains("nothing")){
                               sendSMS(number,sms_to_be_sent);
                               sendDetailsToServer("blank",number);

                           }else {
                               sendSMS(number,"Dear "+ name+ sms_to_be_sent);
                               sendDetailsToServer(name,number);


                            }

                        }
                    }

                }
            }
        }

        public String loadData(Context context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
            String text  = sharedPreferences.getString("thankYouKey", "");

            return text;
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

        private String getContactName(Context context, String number) {

            String name = null;

            // define the columns I want the query to return
            String[] projection = new String[] {
                    ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup._ID};

            // encode the phone number and build the filter URI
            Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

            // query time
            Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);

            if(cursor != null) {
                if (cursor.moveToFirst()) {
                    name =      cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                    Log.v("contact", "Started uploadcontactphoto: Contact Found @ " + number);
                    Log.v("contact", "Started uploadcontactphoto: Contact name  = " + name);
                } else {
                    Log.v("contact", "Contact Not Found @ " + number);
                    name = "nothing";
                }
                cursor.close();
            }
            return name;
        }

        public void sendSMS(String phoneNo, String msg) {

                try
                {
                    SmsManager smsMgrVar = SmsManager.getDefault();
                    ArrayList<String> parts = smsMgrVar.divideMessage(msg);
                    smsMgrVar.sendMultipartTextMessage(phoneNo, null, parts, null, null);
                    Toast.makeText(getApplicationContext(), "Message Sent",
                            Toast.LENGTH_LONG).show();
                }
                catch (Exception ErrVar)
                {
                    Toast.makeText(getApplicationContext(),ErrVar.getMessage().toString(),
                            Toast.LENGTH_LONG).show();
                    ErrVar.printStackTrace();
                }


        }

    }

    //storing token to mysql server
    private void sendDetailsToServer(String name,String pno) {



        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {


                            JSONObject obj = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context2, ""+error.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("errorvolley",error.toString());

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("pno", pno);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }



}
