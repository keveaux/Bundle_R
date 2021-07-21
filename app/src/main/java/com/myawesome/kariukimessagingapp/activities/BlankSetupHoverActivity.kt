package com.myawesome.kariukimessagingapp.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hover.sdk.api.HoverParameters
import com.myawesome.kariukimessagingapp.R
import com.myawesome.kariukimessagingapp.model.PhoneNumberListModel
import com.myawesome.kariukimessagingapp.services.FetchSuccessSMSService
import java.util.*


class BlankSetupHoverActivity : Activity() {

    var amount_choice_one: String? = null
    var amount_choice_two:kotlin.String? = null
    var amount: String? = null
    var last_number=""
    var smsServiceIntent: Intent? = null
    var count = 0
    lateinit var arrayList: MutableList<PhoneNumberListModel>



    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Transparent)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blank_setup_hover)

//        smsServiceIntent= Intent(applicationContext, FetchSuccessSMSService::class.java)
    }



    private fun checkNumbers(){
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val gson = Gson()
        val json = sharedPrefs.getString("TAG", "")
        val type = object : TypeToken<List<PhoneNumberListModel?>?>() {}.type
        arrayList  = gson.fromJson<List<PhoneNumberListModel>>(json, type) as MutableList<PhoneNumberListModel>

        Toast.makeText(applicationContext,"count "+ count,Toast.LENGTH_LONG).show()


        if (Objects.requireNonNull(arrayList).size > 0 ) {

            Toast.makeText(applicationContext,"size"+arrayList.size,Toast.LENGTH_LONG).show()


            amount=arrayList.get(0).amount
            last_number=arrayList.get(0).phone_number



            Log.println(1, "first", Objects.requireNonNull(arrayList)[0].amount)

            arrayList.removeAt(0)

            saveToSharedPrefs(arrayList as ArrayList<PhoneNumberListModel>)


            buyDataBundles()



        }
    }




    private fun saveToSharedPrefs(ModelArrayList:ArrayList<PhoneNumberListModel>){
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor: SharedPreferences.Editor = sharedPrefs.edit()
        val gson = Gson()

        val json = gson.toJson(ModelArrayList)

        editor.putString("TAG", json)
        editor.apply()
    }

    private fun buyDataBundles() {


            when (amount) {
                "550" -> {
                    amount_choice_one = "6"
                }
                "1050" -> {
                    amount_choice_one = "1"
                }
                "2100" -> {
                    amount_choice_one = "2"
                }
                "3150" -> {
                    amount_choice_one = "3"
                }
                "5200" -> {
                    amount_choice_one = "4"
                }
                "10430" -> {
                    amount_choice_one = "5"
                }


            }
            setUpHoverSdk()
        }


    private fun setUpHoverSdk() {
        if (amount?.toInt() == 499 || amount?.toInt() == 550 || amount?.toInt() == 1050 || amount?.toInt() == 2100 || amount?.toInt() == 3150 || amount?.toInt() == 5200 || amount?.toInt() == 10430 || amount?.toInt() == 99) {
            if (amount?.toInt() == 99) {
                val i = HoverParameters.Builder(Objects.requireNonNull(applicationContext))
                        .request("08aa4775")
                        .showUserStepDescriptions(false)
                        .extra("MSISDN", last_number) // Only if your action has variables
                        .buildIntent()
                startActivityForResult(i, 0)
            }
            else if(amount?.toInt() == 499){
                val i = HoverParameters.Builder(Objects.requireNonNull(applicationContext))
                        .request("45bf1f67")
                        .showUserStepDescriptions(false)
                        .extra("MSISDN", last_number) // Only if your action has variables
                        .buildIntent()
                startActivityForResult(i, 0)
            }
            else {
                val i = HoverParameters.Builder(Objects.requireNonNull(applicationContext))
                        .request("75d2293d")
                        .showUserStepDescriptions(false)
                        .extra("number", last_number) // Only if your action has variables
                        .extra("choiceOne", amount_choice_one)
                        .buildIntent()
                startActivityForResult(i, 0)
            }
        }

    }



    override fun onResume() {
        super.onResume()

        count++


        if (count == 1 || count == 3 || count == 5 || count == 7 || count == 9 || count == 11 || count == 13 || count == 15 || count == 17 || count == 19 ||
                count == 21 || count == 23 || count == 25 || count == 27 || count == 29 || count == 31 || count == 33 || count == 35 || count == 37 || count == 39)
           checkNumbers()

        if (Objects.requireNonNull(arrayList).size == 0){
            val pDialog = SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Success! Finished sending "
            pDialog.setCancelable(true)
            pDialog.setCancelClickListener {

            }
            pDialog.setConfirmClickListener {
                val intent= Intent(this,SendToManyActivity::class.java)
                startActivity(intent)
            }
            pDialog.show()
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent= Intent(this,SendToManyActivity::class.java)
        startActivity(intent)
    }

}
