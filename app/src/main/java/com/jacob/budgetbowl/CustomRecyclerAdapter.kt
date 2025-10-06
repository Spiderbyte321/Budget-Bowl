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
import com.jacob.budgetbowl.ui.PopUpFragments.ImagePopUpFragment

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
            PopUpFragment.show(fragment.parentFragmentManager,"Popup")
        }
        holder.expenseDate.text = dataset[position].ExpenseDate
        holder.expenseAmount.text = "Amount: "+dataset[position].ExpenseAmount.toString()
        holder.expenseDescription.text = dataset[position].ExpesnseDescription
    }

    //This tells the recycler how many items we have
    override fun getItemCount(): Int {
        return dataset.count()
    }

    fun updateAdpater(newExpenseList: List<ExpenseEntry>)
    {
        Log.d("DEBUG","${newExpenseList.count()}")
        dataset = newExpenseList
        notifyDataSetChanged()
    }

    //we make a private class to make it easy to access the variables in the layout
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        val expenseAmount : TextView = view.findViewById(R.id.expenseAmt)?: throw IllegalStateException("no text view")


        val expenseDate : TextView = view.findViewById(R.id.expenseDate)?: throw IllegalStateException("Text view not found")

        val expenseDescription: TextView = view.findViewById(R.id.expenseDesc)?:throw IllegalStateException("Text is null")
        val image: ImageView = view.findViewById(R.id.background)?:throw IllegalStateException("No background Image")
    }

    ///References
    //Create dynamic lists with RecyclerView
    //Android Open Source Project. 10 February 2025.Create dynamic lists with RecyclerView .[Online] Avaliable at://https://developer.android.com/develop/ui/views/layout/recyclerview [ Accessed on: 2 October 2025]

}