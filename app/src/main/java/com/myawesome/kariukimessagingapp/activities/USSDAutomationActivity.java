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


            case "499":
                amount_choice_one = "6";
                amount_choice_two = "2";
                break;

            case "500":
                amount_choice_one = "6";
                amount_choice_two = "1";

                break;

            case "998":
                amount_choice_one = "1";
                amount_choice_two = "1";

                break;

            case "999":
                amount_choice_one = "1";
                amount_choice_two = "2";
                break;

            case "1000":
                amount_choice_one = "1";
                amount_choice_two = "3";

                break;

            case "1998":
                amount_choice_one = "2";
                amount_choice_two = "1";

                break;

            case "1999":
                amount_choice_one = "2";
                amount_choice_two = "2";
                break;

            case "2000":
                amount_choice_one = "2";
                amount_choice_two = "3";

                break;

            case "2998":
                amount_choice_one = "3";
                amount_choice_two = "1";

                break;

            case "2999":
                amount_choice_one = "3";
                amount_choice_two = "2";
                break;

            case "3000":
                amount_choice_one = "3";
                amount_choice_two = "3";

                break;

            case "4999":
                amount_choice_one = "4";
                amount_choice_two = "1";

                break;

            case "5000":
                amount_choice_one = "4";
                amount_choice_two = "2";

                break;

            case "9999":
                amount_choice_one = "5";
                amount_choice_two = "1";

                break;

            case "10000":
                amount_choice_one = "5";
                amount_choice_two = "2";

                break;
        }

        if (Integer.parseInt(amount) == 499 || Integer.parseInt(amount) == 500 || Integer.parseInt(amount) == 998 || Integer.parseInt(amount) == 999 || Integer.parseInt(amount) == 1000 || Integer.parseInt(amount) == 1998
                || Integer.parseInt(amount) == 1999 || Integer.parseInt(amount) == 2000 || Integer.parseInt(amount) == 2998 || Integer.parseInt(amount) == 2999 || Integer.parseInt(amount) == 3000
                || Integer.parseInt(amount) == 4999 || Integer.parseInt(amount) == 5000 || Integer.parseInt(amount) == 9999 || Integer.parseInt(amount) == 10000 || Integer.parseInt(amount) == 99) {

            if(Integer.parseInt(amount) == 99){
                Intent i = new HoverParameters.Builder(USSDAutomationActivity.this)
                        .request("bedf3cb6")
                        .showUserStepDescriptions(false)
                        .extra("MSISDN", last_number) // Only if your action has variables
                        .buildIntent();
                startActivityForResult(i, 0);
            }else {
                Intent i = new HoverParameters.Builder(USSDAutomationActivity.this)
                        .request("7348ec75")
                        .showUserStepDescriptions(false)
                        .extra("number", last_number) // Only if your action has variables
                        .extra("choiceOne", amount_choice_one)
                        .extra("choiceTwo", amount_choice_two)
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

