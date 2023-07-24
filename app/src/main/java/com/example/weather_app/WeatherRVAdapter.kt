package com.example.weather_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WeatherRVAdapter(val context: Context,val WeatherRVModalArrayList:ArrayList<WeatherModal>) :RecyclerView.Adapter<WeatherRVAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View= LayoutInflater.from(context).inflate(R.layout.weateher_rv_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modal=WeatherRVModalArrayList[position]
        holder.windspeed.text=(modal.windspeed)
        Picasso.get().load("https:".plus(modal.icon)).into(holder.condition)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = inputFormat.parse(modal.time!!)
        val outputFormat = SimpleDateFormat("hh:mm aa", Locale.getDefault())
        val formattedTime = outputFormat.format(date!!)
        holder.time.text=formattedTime
        holder.temperature.text=modal.temperature+"°C"



    }

    override fun getItemCount(): Int {
        return WeatherRVModalArrayList.size
    }
    class ViewHolder(itmemView: View):RecyclerView.ViewHolder(itmemView){
            val time=itemView.findViewById<TextView>(R.id.idTVTime)
            val temperature=itemView.findViewById<TextView>(R.id.dTVTemperatureRV)
            val windspeed=itemView.findViewById<TextView>(R.id.TVWindSpeedRV)
            val condition=itmemView.findViewById<ImageView>(R.id.idIVConditionRV)
    }
}