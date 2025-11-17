package com.jacob.budgetbowl

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PrizeAdapter(private val prizeNames: List<String>) : RecyclerView.Adapter<PrizeAdapter.PrizeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrizeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_prize, parent, false)
        return PrizeViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrizeViewHolder, position: Int) {
        val prizeName = prizeNames[position]
        holder.bind(prizeName)
    }

    override fun getItemCount(): Int {
        return prizeNames.size
    }

    inner class PrizeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val prizeImageView: ImageView = itemView.findViewById(R.id.prizeImageView)
        private val context: Context = itemView.context

        fun bind(prizeName: String) {
            val resourceId = context.resources.getIdentifier(prizeName, "drawable", context.packageName)
            if (resourceId != 0) {
                prizeImageView.setImageResource(resourceId)
            }
        }
    }
}