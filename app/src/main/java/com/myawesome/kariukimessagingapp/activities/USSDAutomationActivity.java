package com.myawesome.kariukimessagingapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hover.sdk.api.Hover;
import com.hover.sdk.api.HoverParameters;
import com.myawesome.kariukimessagingapp.R;
import com.myawesome.kariukimessagingapp.services.SmsProcessService;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class USSDAutomationActivity extends AppCompatActivity {

    String number, last_number;
    String amount_choice_one, amount_choice_two;
    String amount;
    String[] parts;
    Intent smsServiceIntent;
    private static final String URL = "http://www.patridge.co.ke/database/addToScheduler.php";
    private ProgressDialog progressDialog;
    Intent intent;
    String strdata;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ussdautomation);

        Hover.initialize(this);


        intent = this.getIntent();
        strdata = intent.getExtras().getString("message");



        smsServiceIntent = new Intent(this, SmsProcessService.class);


        number = strdata.substring(strdata.indexOf("from"), strdata.indexOf(". New"));
        last_number = number.replaceAll("\\D+", "");


        String name = strdata.substring(strdata.indexOf(last_number), strdata.indexOf(". New"));
        name = name.replaceAll("\\d","");


        sendDetailsToServer(name,last_number);


    }




    private void buy_bundles(String data) {

        amount = data.substring(data.indexOf("Ksh") + 3, data.indexOf("from"));

        parts = amount.split("\\.");

        amount = parts[0].replaceAll("[^\\d.]", "");

        Toast.makeText(USSDAutomationActivity.this, "" + amount, Toast.LENGTH_SHORT).show();

        number = data.substring(data.indexOf("from"), data.indexOf(". New"));


        last_number = number.replaceAll("\\D+", "");


        String name = data.substring(data.indexOf(last_number), data.indexOf(". New"));
        name.replaceAll("\\s+", "");


        Toast.makeText(USSDAutomationActivity.this, "name " + name + last_number, Toast.LENGTH_SHORT).show();

        switch (amount) {


            case "550":
                amount_choice_one = "6";
                break;

            case "1050":
                amount_choice_one = "1";

                break;

            case "2100":
                amount_choice_one = "2";

                break;

            case "3150":
                amount_choice_one = "3";
                break;

            case "5200":
                amount_choice_one = "4";

                break;

            case "10430":
                amount_choice_one = "5";

                break;



        }

        if (Integer.parseInt(amount) == 499 || Integer.parseInt(amount) == 550 || Integer.parseInt(amount) == 1050 || Integer.parseInt(amount) == 2100
                || Integer.parseInt(amount) == 3150 || Integer.parseInt(amount) == 5200 || Integer.parseInt(amount) == 99) {

            if(Integer.parseInt(amount) == 99){
                Intent i = new HoverParameters.Builder(USSDAutomationActivity.this)
                        .request("08aa4775")
                        .showUserStepDescriptions(false)
                        .extra("MSISDN", last_number) // Only if your action has variables
                        .buildIntent();
                startActivityForResult(i, 0);
            }
            else if(Integer.parseInt(amount) == 499){
                Intent i = new HoverParameters.Builder(USSDAutomationActivity.this)
                        .request("45bf1f67")
                        .showUserStepDescriptions(false)
                        .extra("MSISDN", last_number) // Only if your action has variables
                        .buildIntent();
                startActivityForResult(i, 0);
            }
            else {
                Intent i = new HoverParameters.Builder(USSDAutomationActivity.this)
                        .request("75d2293d")
                        .showUserStepDescriptions(false)
                        .extra("number", last_number) // Only if your action has variables
                        .extra("choiceOne", amount_choice_one)
                        .buildIntent();
                startActivityForResult(i, 0);
            }

        }

    }


    //storing token to mysql server
    private void sendDetailsToServer(String name,String pno) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering Device...");
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {

                            /* Obtain String from Intent  */
                            if (intent != null) {
                                buy_bundles(strdata);

                            }

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
                        Toast.makeText(USSDAutomationActivity.this, ""+error.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("errorvolley",error.toString());

                        /* Obtain String from Intent  */
                        if (intent != null) {
                            buy_bundles(strdata);

                        }
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



    @Override
    protected void onResume() {

        super.onResume();

        startService(smsServiceIntent);
    }
}

