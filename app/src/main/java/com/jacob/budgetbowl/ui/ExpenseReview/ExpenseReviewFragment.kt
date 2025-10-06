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
//Google is android developers
//Module Manual also referenced
//Which we reference as Rochelle I think?

//https://developer.android.com/topic/libraries/architecture/coroutines
//Getting the user data in the view

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
        //(Google,S.A)

        //FINALLY OK
        //I understand how this works now but it still doesn't make me like android studio

        val emptyList: List<ExpenseEntry> = mutableListOf()
        var newAdapter: CustomRecyclerAdapter = CustomRecyclerAdapter(emptyList,this)


        val recycler : RecyclerView = Binding.Recycler


        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = newAdapter//(Google,S.A)

        //YES OK I GOT IT!
        ViewBinding.UserExpenses.observe(viewLifecycleOwner){
                UserExpenses-> newAdapter = CustomRecyclerAdapter(UserExpenses,this)
                recycler.adapter = newAdapter
        }


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
            }//(Rochelle,2025)

            Binding.Spent.text = "Total Spent: "+ totalSpent

        }


    }



    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}