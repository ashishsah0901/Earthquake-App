package com.example.earthquake

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.earthquake.databinding.ItemEarthquakeBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.floor


class EarthquakeAdapter(private val mContext: Context, private val listener: RVAdapterListener): RecyclerView.Adapter<EarthquakeAdapter.EarthquakeViewHolder>() {

    private val list: ArrayList<Earthquake> = ArrayList()

    inner class EarthquakeViewHolder(val binding: ItemEarthquakeBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EarthquakeViewHolder {
        return EarthquakeViewHolder(ItemEarthquakeBinding.inflate(LayoutInflater.from(mContext), parent, false))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: EarthquakeViewHolder, position: Int) {
        val place = list[position].place
        val parts = place.split("of")
        if(place.contains("of")) {
            holder.binding.placeET.text = parts[0]
            holder.binding.cityET.text = parts[1]
        }else{
            holder.binding.placeET.visibility = View.INVISIBLE
            holder.binding.cityET.text = parts[0]
        }
        val doubleFormat = DecimalFormat("0.0")
        val magnitude = doubleFormat.format(list[position].magnitude)
        holder.binding.magnitudeET.text = magnitude
        val dateObject = Date(list[position].date)
        val dateFormat = SimpleDateFormat("MMM dd, yyyy")
        val timeFormat = SimpleDateFormat("h:mm a")
        val date = dateFormat.format(dateObject)
        val time = timeFormat.format(dateObject)
        holder.binding.dateET.text = date
        holder.binding.timeET.text = time
        val mag = holder.binding.magnitudeET.background as GradientDrawable
        mag.setColor(getColorID(list[position].magnitude))
        holder.itemView.setOnClickListener {
            listener.onClick(list[position].url)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getColorID(magnitude: Double): Int {
        return when (floor(magnitude).toInt()) {

            0, 1 -> mContext.getColor(R.color.magnitude1)
            2 -> mContext.getColor(R.color.magnitude2)
            3 -> mContext.getColor(R.color.magnitude3)
            4 -> mContext.getColor(R.color.magnitude4)
            5 -> mContext.getColor(R.color.magnitude5)
            6 -> mContext.getColor(R.color.magnitude6)
            7 -> mContext.getColor(R.color.magnitude7)
            8 -> mContext.getColor(R.color.magnitude8)
            9 -> mContext.getColor(R.color.magnitude9)
            else -> mContext.getColor(R.color.magnitude10plus)
        }
    }

    fun updateData(data: ArrayList<Earthquake>){
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }
}
interface RVAdapterListener{
    fun onClick(url: String)
}