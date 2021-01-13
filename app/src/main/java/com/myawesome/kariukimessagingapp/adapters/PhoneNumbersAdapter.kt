package com.myawesome.kariukimessagingapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.myawesome.kariukimessagingapp.R
import com.myawesome.kariukimessagingapp.model.PhoneNumberListModel
import kotlinx.android.synthetic.main.list.view.*


class PhoneNumbersAdapter(var context: Context,var messageArrayList: ArrayList<PhoneNumberListModel>):
    RecyclerView.Adapter<PhoneNumbersAdapter.PhoneNumberViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) :PhoneNumberViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.list, parent, false)

        view.setOnClickListener(){
//            val intent= Intent(parent.context,MessageDetailsActivity::class.java)
//            parent.context.startActivity(intent)
        }



        return PhoneNumberViewHolder(view)
    }

    override fun getItemCount(): Int {
       return messageArrayList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: PhoneNumberViewHolder, position: Int) {

        holder.bind(messageArrayList[position])

        holder.remove_btn.setOnClickListener(View.OnClickListener {
            try {
                messageArrayList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position,itemCount)
            }catch (e:Exception){
                e.printStackTrace()
            }
        })

        holder.layout.setOnClickListener(View.OnClickListener {
            Toast.makeText(context,position.toString(),Toast.LENGTH_LONG).show()
        })



    }

    class PhoneNumberViewHolder(view: View):RecyclerView.ViewHolder(view) {

        private val phone_number:TextView = view.phone_tv
        public val remove_btn: ImageButton = view.remove_ibtn
        private val amount_tv:TextView =view.amount_tv
        val layout :ConstraintLayout = view.layout


        fun bind(phoneNumberListModel: PhoneNumberListModel){
            phone_number.text=phoneNumberListModel.phone_number
            amount_tv.text=phoneNumberListModel.amount



        }



    }
}