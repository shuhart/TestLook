package com.shuhart.testlook.modules.flight.search.airport

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.shuhart.testlook.R
import com.shuhart.testlook.api.model.City

class SearchAirportAdapter : RecyclerView.Adapter<SearchAirportAdapter.CityViewHolder>() {
    var items = mutableListOf<City>()

    var listener: CityItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_search_aiport_item, parent, false)
        return CityViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city = items[position]
        val text = city.toString()
        holder.textView.text = text
        holder.textView.setOnClickListener {
            listener?.onCityItemClicked(items[holder.adapterPosition])
        }
    }

    class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView as TextView
    }

    interface CityItemClickListener {
        fun onCityItemClicked(city: City)
    }
}