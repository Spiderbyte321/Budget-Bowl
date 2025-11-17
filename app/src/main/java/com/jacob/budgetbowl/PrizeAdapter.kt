package com.jacob.budgetbowl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PrizeAdapter(private val prizeUrls: List<String>) : RecyclerView.Adapter<PrizeAdapter.PrizeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrizeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_prize, parent, false)
        return PrizeViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrizeViewHolder, position: Int) {
        val prizeUrl = prizeUrls[position]
        holder.bind(prizeUrl)
    }

    override fun getItemCount(): Int {
        return prizeUrls.size
    }

    inner class PrizeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val prizeImageView: ImageView = itemView.findViewById(R.id.prizeImageView)

        fun bind(prizeUrl: String) {
            Glide.with(itemView.context)
                .load(prizeUrl)
                .into(prizeImageView)
        }
    }
}