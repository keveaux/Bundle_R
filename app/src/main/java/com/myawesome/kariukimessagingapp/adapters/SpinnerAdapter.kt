package com.myawesome.kariukimessagingapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.myawesome.kariukimessagingapp.R

class SpinnerAdapter(val context: Context,var listItemsTxt:Array<String>) : BaseAdapter() {

    val mInflator : LayoutInflater= LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view:View
        val vh: ItemRowHolder

        if(convertView==null){
            view=mInflator.inflate(R.layout.view_drop_down_menu,parent,false)
            vh=ItemRowHolder(view)
            view?.tag=vh
        }else{
            view=convertView
            vh=view.tag  as ItemRowHolder
        }

        val params =view.layoutParams
        params.height=100
        view.layoutParams=params

        vh.label.text = listItemsTxt.get(position)

        return view;
    }

    override fun getItem(position: Int): Any? {

        return null
    }

    override fun getItemId(position: Int): Long {

        return 0
    }

    override fun getCount(): Int {

        return listItemsTxt.size
    }

    private class ItemRowHolder(row: View?) {

        val label: TextView

        init {
            this.label = row?.findViewById(R.id.txtDropDownLabel) as TextView
        }
    }
}