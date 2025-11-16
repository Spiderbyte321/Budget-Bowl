package com.jacob.budgetbowl

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class IconAdapter(private val context: Context, private val icons: List<Int>) : BaseAdapter() {

    override fun getCount(): Int {
        return icons.size
    }

    override fun getItem(position: Int): Any {
        return icons[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView: ImageView
        if (convertView == null) {
            imageView = ImageView(context)
            imageView.layoutParams = ViewGroup.LayoutParams(150, 150) // Icon size
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(8, 8, 8, 8)
        } else {
            imageView = convertView as ImageView
        }

        imageView.setImageResource(icons[position])
        return imageView
    }
}
