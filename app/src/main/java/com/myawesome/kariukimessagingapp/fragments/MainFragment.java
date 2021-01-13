package com.myawesome.kariukimessagingapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hover.sdk.api.HoverParameters;
import com.myawesome.kariukimessagingapp.R;
import com.myawesome.kariukimessagingapp.activities.MainActivity;
import com.myawesome.kariukimessagingapp.activities.SendToManyActivity;

import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {


    View view;
    EditText message_et;

    String number,last_number;
    String amount_choice_one,amount_choice_two;
    String amount;
    String[] parts;
    TextView good_morning_txt;
    ImageView time_iv;
    TextView message_txt;
    static Boolean checked = false;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String KEY_THANK_YOU = "thankYouKey";
    private static final String KEY_RENEW = "renewKey";

    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_main, container, false);

        Button save_btn=view.findViewById(R.id.btn);
        message_et=view.findViewById(R.id.et);

        CardView copy_paste_cv=view.findViewById(R.id.copy_paste_cv);
        CardView send_to_many_cv=view.findViewById(R.id.send_to_many_cv);
        RelativeLayout copy_paste_layout_hidden=view.findViewById(R.id.copy_paste_layout_hidden);
        SwitchCompat message_switch_button = view.findViewById(R.id.message_switch_button);
        message_txt = view.findViewById(R.id.message_txt);
        good_morning_txt=view.findViewById(R.id.good_morning_txt);
        time_iv=view.findViewById(R.id.time_img);

        copy_paste_cv.setOnClickListener(v->{
            copy_paste_layout_hidden.setVisibility(View.VISIBLE);
        });


        save_btn.setOnClickListener(v -> saveMessageToSharedPreference(message_et.getText().toString()));

        send_to_many_cv.setOnClickListener(v->{
            Intent intent=new Intent(getContext(), SendToManyActivity.class);
            startActivity(intent);
        });

        getTimeOfTheDay();

        if (message_switch_button != null) {
            message_switch_button.setOnCheckedChangeListener(this);
        }


        String message_displayed = loadData(getContext());

        message_et.setText(message_displayed);

        // Inflate the layout for this fragment
        return view;
    }

    private void saveMessageToSharedPreference(String msg) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(checked){
            editor.putString(KEY_THANK_YOU, msg);

        }else {
            editor.putString(KEY_RENEW, msg);

        }
        editor.apply();
        Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
    }

    public static String loadData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String text = "";
        if(checked) {
            text  = sharedPreferences.getString(KEY_THANK_YOU, "");
        }else {
            text  = sharedPreferences.getString(KEY_RENEW, "");
        }
        return text;
    }


    private void getTimeOfTheDay(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("EAT"));
        int hour = cal.get(Calendar.HOUR_OF_DAY)+3;


       if(hour==12||hour==13|| hour==14 ||hour==15||hour==16|| hour==17||hour==18 ){
           Glide.with(getContext()).load(R.drawable.afternoon).into(time_iv);
           good_morning_txt.setText("Good Afternoon Kariuki");
       }else if(hour==19||hour==20|| hour==21 ||hour==22||hour==23|| hour==0||hour==1||hour==2||hour==3|| hour==4){
           Glide.with(getContext()).load(R.drawable.evening).into(time_iv);
           good_morning_txt.setText("Good Evening Kariuki");
       }else {
           Glide.with(getContext()).load(R.drawable.day_time).into(time_iv);
           good_morning_txt.setText("Good Morning Kariuki");
       }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            String[] sessionTextArr = data.getStringArrayExtra("session_messages");
            String uuid = data.getStringExtra("uuid");
        } else if (requestCode == 0 && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getContext(), "Error: " + data.getStringExtra("error"), Toast.LENGTH_LONG).show();
        }

        Toast.makeText(getContext(), "result code "+resultCode, Toast.LENGTH_SHORT).show();

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        checked = isChecked;

        if(isChecked) {
            //do stuff when Switch is ON
            message_txt.setText("Thank you message");

            String message_displayed = loadData(getContext());

            message_et.setText(message_displayed);

        } else {
            //do stuff when Switch if OFF
            message_txt.setText("Please Renew message");

            String message_displayed = loadData(getContext());

            message_et.setText(message_displayed);

        }
    }
}
