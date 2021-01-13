package com.myawesome.kariukimessagingapp.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.ContactsContract
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.myawesome.kariukimessagingapp.R
import com.myawesome.kariukimessagingapp.adapters.PhoneNumbersAdapter
import com.myawesome.kariukimessagingapp.model.PhoneNumberListModel
import kotlinx.android.synthetic.main.activity_send_to_many.*


class SendToManyActivity : AppCompatActivity() {

    var amount_selected = ""
    var phone_number = ""
    var amount_choice_one: String? = ""
    var amount_choice_two: String? = ""
    var amount: String? = ""
    val phoneNumberList: ArrayList<PhoneNumberListModel> = ArrayList()
    var pno: String = ""
    var smsServiceIntent: Intent? = null
    lateinit var phoneNumbersAdapter:PhoneNumbersAdapter
    val PICK_CONTACT = 1
    var cNumber:String= ""
    val MY_PERMISSIONS_REQUEST_READ_CONTACTS=7
    lateinit var editText_pnos : EditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_to_many)

        val amount = arrayOf("99", "499", "500", "998", "999", "1000", "1998", "1999", "2000", "2998", "2999", "3000", "4999", "5000", "9999", "10000")

        val spin = findViewById<Spinner>(R.id.spinner_amount) as Spinner
        editText_pnos = findViewById<EditText>(R.id.editText_pnos)
        val phone_numbers_rv = findViewById<RecyclerView>(R.id.phone_numbers_rv)

        //Creating the ArrayAdapter instance having the country list

        //Creating the ArrayAdapter instance having the country list
        val aa: ArrayAdapter<*> = ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, amount)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        //Setting the ArrayAdapter data on the Spinner


        spin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                amount_selected = parent?.getItemAtPosition(position).toString()

            }


        }


        spin.apply {
            adapter = aa
        }



        btn_add.setOnClickListener(View.OnClickListener {



            if (editText_pnos.text.isEmpty()) {
                Toast.makeText(applicationContext, "Please Enter Phone Number", Toast.LENGTH_LONG).show()
            } else {


                phone_number = editText_pnos.text.toString()
                phone_number=phone_number.replace("\\s".toRegex(), "")

                if(phone_number.contains(",")){


                    var phone_numbers_list_commas= phone_number.split(",").toTypedArray()

                    for (item in phone_numbers_list_commas) {
                        phoneNumberList.add(PhoneNumberListModel(item,amount_selected))
                    }

                }else{

                    phoneNumberList.add(PhoneNumberListModel(phone_number, amount_selected))

                }

                phoneNumbersAdapter = PhoneNumbersAdapter(applicationContext, phoneNumberList)

                phone_numbers_rv.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = phoneNumbersAdapter
                }

                editText_pnos.text.clear()


            }

        })

        btn_clear.setOnClickListener(View.OnClickListener {
            phoneNumberList.clear()
            phone_numbers_rv.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = phoneNumbersAdapter
            }
        })

//        smsServiceIntent = Intent(applicationContext, FetchSuccessSMSService::class.java)


        finish_fab.setOnClickListener(View.OnClickListener {
//                buyDataBundles()
            if(phoneNumberList.size>0){
                saveToSharedPrefs(phoneNumberList)
                openBlankActivity()
            }

        })

        var phone_btn =findViewById<ImageButton>(R.id.phone_btn)

        phone_btn.setOnClickListener(View.OnClickListener {

            checkPermissions()
        })


    }

    private fun openBlankActivity() {

        val intent = Intent(applicationContext, BlankSetupHoverActivity::class.java)
        startActivity(intent)
    }


    private fun saveToSharedPrefs(ModelArrayList: ArrayList<PhoneNumberListModel>) {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor: SharedPreferences.Editor = sharedPrefs.edit()
        val gson = Gson()

        val json = gson.toJson(ModelArrayList)

        editor.putString("TAG", json)
        editor.apply()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        val intent= Intent(this,MainActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PICK_CONTACT -> if (resultCode === Activity.RESULT_OK) {
                val contactData: Uri = data!!.data
                val c: Cursor = managedQuery(contactData, null, null, null, null)
                if (c.moveToFirst()) {
                    val id: String = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                    val hasPhone: String = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                    if (hasPhone.equals("1", ignoreCase = true)) {
                        val phones: Cursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                null, null)
                        phones.moveToFirst()
                        cNumber = phones.getString(phones.getColumnIndex("data1"))

                        if (editText_pnos.text.isEmpty()) {

                            editText_pnos.append(cNumber)

                        }else{
                            editText_pnos.append(","+cNumber)
                        }
                        Toast.makeText(applicationContext,cNumber,Toast.LENGTH_LONG).show()
                    }
                    val name: String = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun checkPermissions(){
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            val permissionArrays = arrayOf(Manifest.permission.READ_CONTACTS)

            requestPermissions( permissionArrays,
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant

            return;
        }else{
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            startActivityForResult(intent, PICK_CONTACT)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                if (grantResults[0] === PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! do the
                    // calendar task you need to do.

                    val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                    startActivityForResult(intent, PICK_CONTACT)

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(applicationContext,"Please give contact permissions",Toast.LENGTH_LONG).show()
                }
                return
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}


