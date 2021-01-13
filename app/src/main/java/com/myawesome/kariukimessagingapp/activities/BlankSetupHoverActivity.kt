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
                "499" -> {
                    amount_choice_one = "6"
                    amount_choice_two = "2"
                }
                "500" -> {
                    amount_choice_one = "6"
                    amount_choice_two = "1"
                }
                "998" -> {
                    amount_choice_one = "1"
                    amount_choice_two = "1"
                }
                "999" -> {
                    amount_choice_one = "1"
                    amount_choice_two = "2"
                }
                "1000" -> {
                    amount_choice_one = "1"
                    amount_choice_two = "3"
                }
                "1998" -> {
                    amount_choice_one = "2"
                    amount_choice_two = "1"
                }
                "1999" -> {
                    amount_choice_one = "2"
                    amount_choice_two = "2"
                }
                "2000" -> {
                    amount_choice_one = "2"
                    amount_choice_two = "3"
                }
                "2998" -> {
                    amount_choice_one = "3"
                    amount_choice_two = "1"
                }
                "2999" -> {
                    amount_choice_one = "3"
                    amount_choice_two = "2"
                }
                "3000" -> {
                    amount_choice_one = "3"
                    amount_choice_two = "3"
                }
                "4999" -> {
                    amount_choice_one = "4"
                    amount_choice_two = "1"
                }
                "5000" -> {
                    amount_choice_one = "4"
                    amount_choice_two = "2"
                }
                "9999" -> {
                    amount_choice_one = "5"
                    amount_choice_two = "1"
                }
                "10000" -> {
                    amount_choice_one = "5"
                    amount_choice_two = "2"
                }
            }
            setUpHoverSdk()
        }


    private fun setUpHoverSdk() {
        if (amount?.toInt() == 499 || amount?.toInt() == 500 || amount?.toInt() == 998 || amount?.toInt() == 999 || amount?.toInt() == 1000 || amount?.toInt() == 1998 || amount?.toInt() == 1999 || amount?.toInt() == 2000 || amount?.toInt() == 2998 || amount?.toInt() == 2999 || amount?.toInt() == 3000 || amount?.toInt() == 4999 || amount?.toInt() == 5000 || amount?.toInt() == 9999 || amount?.toInt() == 10000 || amount?.toInt() == 99) {
            if (amount?.toInt() == 99) {
                val i = HoverParameters.Builder(Objects.requireNonNull(applicationContext))
                        .request("bedf3cb6")
                        .showUserStepDescriptions(false)
                        .extra("MSISDN", last_number) // Only if your action has variables
                        .buildIntent()
                startActivityForResult(i, 0)
            } else {
                val i = HoverParameters.Builder(Objects.requireNonNull(applicationContext))
                        .request("7348ec75")
                        .showUserStepDescriptions(false)
                        .extra("number", last_number) // Only if your action has variables
                        .extra("choiceOne", amount_choice_one)
                        .extra("choiceTwo", amount_choice_two)
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
