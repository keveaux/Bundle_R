package com.myawesome.kariukimessagingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hover.sdk.api.Hover;
import com.myawesome.kariukimessagingapp.R;
import com.myawesome.kariukimessagingapp.model.SchedulerList;
import com.myawesome.kariukimessagingapp.services.FetchSuccessSMSService;
import com.myawesome.kariukimessagingapp.services.SmsProcessService;
import com.myawesome.kariukimessagingapp.fragments.LogsFragment;
import com.myawesome.kariukimessagingapp.fragments.MainFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Intent smsServiceIntent;
    Intent sendThankYouMessageIntent;

    NotificationCompat.Builder b;
    NotificationManager nm;
    BottomNavigationView bottomBar;
    private static final String URL_DETAILS = "http://www.patridge.co.ke/database/fetchDates.php";
    ArrayList<SchedulerList> schedulerLists = new ArrayList<>();
    private static final String URL = "http://www.patridge.co.ke/database/updateSentBooleanValue.php";
    private ProgressDialog progressDialog;
    String phone_num_update;
    private static final int PERMISSION_REQUEST_CODE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTheme(R.style.AppTheme);


        Hover.initialize(this);
        bottomBar=findViewById(R.id.bottom_navigation);




        if(!checkIfAlreadyhavePermission()){

            requestForSpecificPermission();

        }

        smsServiceIntent = new Intent(this, SmsProcessService.class);
        sendThankYouMessageIntent = new Intent(this, FetchSuccessSMSService.class);

        setUpNotification();

        openFragment(new MainFragment());


     bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
         @Override
         public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                 switch (item.getItemId()){
                     case R.id.home:
                         openFragment(new MainFragment());
                         item.setChecked(true);

                         break;
                     case R.id.logs:
                         openFragment(new LogsFragment());
                         item.setChecked(true);

                         break;
                 }


             return false;
         }
     });

     getScheduledDates();



    requestSmsPermission();



    }

    private String getDay(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); // Now use today date.
        c.add(Calendar.DATE, 1); // Adding 5 days


        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        String day = "";


        switch (dayOfWeek){
            case 8 :
                day = "Monday";
                break;
            case 9 :
                day = "Tuesday";
                break;
            case 3 :
                day = "Wednesday";
                break;

            case 4 :
                day = "Thursday";
                break;

            case 5 :
                day = "Friday";
                break;
            case 6 :
                day = "Saturday";
                break;

            case 7 :
                day = "Sunday";
                break;
        }

        return day;

    }


    public void openFragment(Fragment fragment) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        fm.popBackStack();
        transaction.addToBackStack(null);
        transaction.add(R.id.main_frame_container, fragment);
        transaction.commit();
    }


    private void setUpNotification(){
        b = new NotificationCompat.Builder(getBaseContext());
        b.setOngoing(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_bold)
                .setContentTitle("We are on!");
        nm = (NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, b.build());
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        nm.cancel(1);

    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_CONTACTS,Manifest.permission.READ_PHONE_STATE}, 101);
    }

    private void requestSmsPermission() {

        // check permission is given
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_REQUEST_CODE);
        } else {
            // permission already granted run sms send
        }


    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
//                    SmsReceiver.bindListener(this);

                } else {
                    //not granted
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void getScheduledDates() {

        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject details = array.getJSONObject(i);


                                //adding the product to product list
                                schedulerLists.add(new SchedulerList(
                                        details.getString("name"),
                                        details.getString("pno"),
                                        details.getString("date_renew")

                                ));

                            }

                            for(SchedulerList myList : schedulerLists){
                                Toast.makeText(MainActivity.this, ""+myList.getName(), Toast.LENGTH_SHORT).show();

                                phone_num_update = myList.getPno();

                                String sms_to_be_sent = loadData(getApplicationContext());


                                if(myList.getName().contains("blank")){
                                    sendSMS(myList.getPno(),sms_to_be_sent +getDay()+".\nRegards,\nKariuki");

                                }else {
                                    sendSMS(myList.getPno(),"Hi "+myList.getName()+ sms_to_be_sent +getDay()+".\nRegards,\nKariuki");

                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    public String loadData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String text  = sharedPreferences.getString("renewKey", "");

        return text;
    }

    public void sendSMS(String phoneNo, String msg) {
        try {

            SmsManager smsMgrVar = SmsManager.getDefault();
            ArrayList<String> parts = smsMgrVar.divideMessage(msg);
            smsMgrVar.sendMultipartTextMessage(phoneNo, null, parts, null, null);

            Toast.makeText(getApplicationContext(), "Message Sent",
                        Toast.LENGTH_LONG).show();

            updateHasSentValue(phone_num_update);
            progressDialog.dismiss();


        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    //storing token to mysql server
    private void updateHasSentValue(String pno) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading......");
        progressDialog.show();


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
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, ""+error.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("errorvolley",error.toString());


                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("pno", pno);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    @Override
    protected void onResume() {

        super.onResume();

        startService(smsServiceIntent);
        startService(sendThankYouMessageIntent);

        openFragment(new MainFragment());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
