package com.jacob.budgetbowl

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//Implement the rest of the methods just test with filling out edit texts on one recycler view then once that works try the nested stuff
class CustomRecyclerAdapter(val dataset: List<ExpenseEntry>): RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return dataset.count()
    }
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val textView: TextView = view.findViewById(R.id.textView)
    }

}