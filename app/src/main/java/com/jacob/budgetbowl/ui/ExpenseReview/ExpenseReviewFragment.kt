package com.jacob.budgetbowl.ui.ExpenseReview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.jacob.budgetbowl.CustomRecyclerAdapter
import com.jacob.budgetbowl.ExpenseEntry
import com.jacob.budgetbowl.R
import com.jacob.budgetbowl.databinding.FragmentExpenseReviewBinding
import com.jacob.budgetbowl.databinding.FragmentHomeBinding


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

    private var binding: FragmentExpenseReviewBinding? =null

    private val Binding get() = binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //This connects us to our view
        val ExpenseReviewFragmentView = ViewModelProvider(this)[ExpenseView::class]
        //This connects us to our xml File
        binding = FragmentExpenseReviewBinding.inflate(inflater, container, false)

        val emptyList: List<ExpenseEntry> = emptyList<ExpenseEntry>()
        val newAdapter: CustomRecyclerAdapter = CustomRecyclerAdapter(emptyList)
        //YES OK I GOT IT!
        ExpenseReviewFragmentView.UserExpenses.observe(viewLifecycleOwner){
            UserExpenses-> ExpenseReviewFragmentView.UserExpenses
            newAdapter.updateAdpater(UserExpenses)
        }
        //FINALLY OK
        //I understand how this works now but it still doesn't make me like android studio

        val root: View = Binding.root

        // returns the layout for this fragment with everything we added
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}