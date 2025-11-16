package com.jacob.budgetbowl.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jacob.budgetbowl.CategoryObject
import com.jacob.budgetbowl.R

class CategoryProgressAdapter(private val categories: List<Pair<String, CategoryObject>>) :
    RecyclerView.Adapter<CategoryProgressAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryIcon: ImageView = view.findViewById(R.id.ivCategoryIcon)
        val categoryName: TextView = view.findViewById(R.id.tvCategoryName)
        val categoryProgress: ProgressBar = view.findViewById(R.id.pbCategoryProgress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_progress, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (categoryName, category) = categories[position]

        holder.categoryName.text = categoryName
        // You can set the icon based on the category name or another property
        // holder.categoryIcon.setImageResource(getIconForCategory(category.categoryName))

        if (category.targetBudget > 0) {
            val progress = (category.categoryTotalExpenditure.toDouble() / category.targetBudget * 100).toInt()
            holder.categoryProgress.progress = progress
        } else {
            holder.categoryProgress.progress = 0
        }
    }

    override fun getItemCount() = categories.size
}
