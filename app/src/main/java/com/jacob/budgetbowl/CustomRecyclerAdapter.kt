package com.jacob.budgetbowl

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jacob.budgetbowl.ui.PopUpFragments.ImagePopUpFragment

class CustomRecyclerAdapter(var dataset: List<ExpenseEntry>, val fragment: Fragment) :
    RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.expense_recycler_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expense = dataset[position]
        holder.expenseDate.text = expense.expenseDate
        holder.expenseAmount.text = "Amount: " + expense.expenseAmount.toString()
        holder.expenseDescription.text = expense.expenseDescription

        if (expense.imageUrl != null) {
            holder.image.setOnClickListener {
                val popUpFragment = ImagePopUpFragment.newInstance(expense.imageUrl)
                popUpFragment.show(fragment.parentFragmentManager, "Popup")
            }
            Glide.with(holder.itemView.context)
                .load(expense.imageUrl)
                .into(holder.image)
        } else {
            holder.image.setOnClickListener {
                Toast.makeText(holder.itemView.context, "No optional image added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return dataset.count()
    }

    fun updateAdapter(newExpenseList: List<ExpenseEntry>) {
        dataset = newExpenseList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val expenseAmount: TextView = view.findViewById(R.id.expenseAmt)
        val expenseDate: TextView = view.findViewById(R.id.expenseDate)
        val expenseDescription: TextView = view.findViewById(R.id.expenseDesc)
        val image: ImageView = view.findViewById(R.id.background)
    }
}
