package com.jacob.budgetbowl

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.jacob.budgetbowl.ui.PopUpFragments.ImagePopUpFragment
import kotlinx.coroutines.withContext

//Implement the rest of the methods just test with filling out edit texts on one recycler view then once that works try the nested stuff
//https://developer.android.com/develop/ui/views/layout/recyclerview
//for the whole class
// Ok I do like being able to declare fields in the constructor rather than having to declare them in the class
class CustomRecyclerAdapter(var dataset: List<ExpenseEntry>,val fragment: Fragment):
    RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder>() {




    //This is where we specify which layout each item in the list uses
    //ahhhh ok this method instantiates the layout from the file and gives it to the class we created
    //So that the rest of the recycler can access the components and fill them out
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.expense_recycler_layout,
            parent,
            false)


        return ViewHolder(view)
    }




    //This tells the recycler what to fill in where
    //Change this to not rely on passing in the fragment manager
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.setOnClickListener {

            if(dataset[position].AddedImage == null)
            {
                Toast.makeText(holder.itemView.context,"No optional image added",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val PopUpFragment = ImagePopUpFragment(dataset[position].AddedImage!!)
            PopUpFragment.show(fragment.childFragmentManager,"Popup")
        }
        holder.textView.text = dataset[position].ExpenseDate
    }

    //This tells the recycler how many items we have
    override fun getItemCount(): Int {
        return dataset.count()
    }

    fun updateAdpater(newExpenseList: List<ExpenseEntry>)
    {
        dataset = newExpenseList
        notifyDataSetChanged()
    }

    //we make a private class to make it easy to access the variables in the layout
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        val textView: TextView = view.findViewById(R.id.testerText)?:throw IllegalStateException("Text is null")
        val image: ImageView = view.findViewById(R.id.background)?:throw IllegalStateException("No background Image")
    }

}