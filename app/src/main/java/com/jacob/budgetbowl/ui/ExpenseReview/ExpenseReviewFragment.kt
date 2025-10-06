package com.jacob.budgetbowl.ui.ExpenseReview

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.util.recursiveFetchHashMap
import com.jacob.budgetbowl.CustomRecyclerAdapter
import com.jacob.budgetbowl.ECategory
import com.jacob.budgetbowl.ExpenseDAO
import com.jacob.budgetbowl.ExpenseEntry
import com.jacob.budgetbowl.R
import com.jacob.budgetbowl.databinding.FragmentExpenseReviewBinding
import com.jacob.budgetbowl.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale
import java.util.logging.Filter
import java.util.logging.SimpleFormatter


//https://developer.android.com/develop/ui/views/layout/recyclerview
//Recycler view reference
//Maybe reference chat? I just used it to understand what I want to do so I actually don't know


//And In here is where I get the references to the layout stuff and assign that data
//step 1 get a reference to all our components
//step 2 assign them to their variables and make them observe the relevant data in the view
//step 3 make the buttons filter the expenses by the date
//many more steps to come but let's start here

//The binding let's us access everything on the XML without constantly searching
//Through the binding make the recycler listen to the data
//

/**
 * A simple [Fragment] subclass.
 * Use the [ExpenseReviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExpenseReviewFragment : Fragment() {


    private var viewBinding: ExpenseView? =null

    private val ViewBinding get() =viewBinding!!
    private var binding: FragmentExpenseReviewBinding? =null

    private val Binding get() = binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {






        //This connects us to our view
         viewBinding = ViewModelProvider(this)[ExpenseView::class]
        //This connects us to our xml File
        binding = FragmentExpenseReviewBinding.inflate(inflater, container, false)
        // returns the view for this fragment with everything we added
        return Binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {

        val adapter = ArrayAdapter<ECategory>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            ECategory.entries
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        Binding.spCategory.adapter = adapter

        CoroutineScope(Dispatchers.IO).launch { ViewBinding.ListExpense = ViewBinding.ExpenseDAO.getAllExpensesASList() }

        //FINALLY OK
        //I understand how this works now but it still doesn't make me like android studio

        val emptyList: List<ExpenseEntry> = mutableListOf()
        var newAdapter: CustomRecyclerAdapter = CustomRecyclerAdapter(emptyList,this)


        val recycler : RecyclerView = Binding.Recycler


        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = newAdapter

        //YES OK I GOT IT!
        //Ok I really am not liking android now
        ViewBinding.UserExpenses.observe(viewLifecycleOwner){
                UserExpenses->
            if(newAdapter.itemCount==0){
                Log.d("DEBUG","adding new data")
                newAdapter = CustomRecyclerAdapter(UserExpenses,this)
                recycler.adapter = newAdapter
            }
            else{
                Log.d("DEBUG","Updating list")
                newAdapter.updateAdpater(UserExpenses)
            }

        }
        //I'm gonna crashout


        Binding.filterBTN.setOnClickListener {
            val FilteredList: MutableList<ExpenseEntry> = mutableListOf()
            var totalSpent: Int = 0

            val dateFormatter = SimpleDateFormat("dd-mm-yyyy", Locale.getDefault())
            val startDate = Binding.startDatee.text.toString()
            val endDate = Binding.endDate.text.toString()



            var startObject: Date? = Date()
            var endObject: Date? = Date()

            try {

                 startObject =dateFormatter.parse(startDate)
                 endObject = dateFormatter.parse(endDate)


            }catch (e: ParseException)
            {
                Toast.makeText(context,"Date formatted incorrectly must be : dd-mm-yyyy",Toast.LENGTH_SHORT).show()
            }



            viewBinding?.ListExpense?.forEach TheLoop@{


                val ExpenseDateObject:Date?
                try {
                     ExpenseDateObject = dateFormatter.parse(it.ExpenseDate)
                }catch (e: ParseException)
                {
                    return@TheLoop
                }

                if(ExpenseDateObject?.compareTo(startObject)==1&&ExpenseDateObject.compareTo(endObject)==-1&& it.CatId==Binding.spCategory.selectedItem)
                {
                    FilteredList.add(it)
                }

                if(FilteredList.count()==0)
                {
                    Toast.makeText(context,"No items found widen date range", Toast.LENGTH_SHORT).show()
                }

                newAdapter.updateAdpater(FilteredList)
            }


            FilteredList.forEach {

                totalSpent+=it.ExpenseAmount
            }

            Binding.Spent.text = "Total Spent: "+ totalSpent

        }


    }



    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}