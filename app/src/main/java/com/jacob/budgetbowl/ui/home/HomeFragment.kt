package com.jacob.budgetbowl.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jacob.budgetbowl.AddExpenseActivity
import com.jacob.budgetbowl.CategoryObject
import com.jacob.budgetbowl.ExpenseObject
import com.jacob.budgetbowl.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    //ok I need to setup a spinner to fetch a category
    //That category must have total spent total budget
    //use those values to populate the donut graph when a button is pressed
    //I need to setup firebase then
    //fuck

    private lateinit var donutFill: ProgressBar
    private lateinit var donutMax: ProgressBar
    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root



        donutFill = binding.statsProgressbar
        donutMax = binding.backgroundProgressbar



        val allCategories = mutableMapOf<String, CategoryObject>()
        val chosenCategories = mutableListOf<CategoryObject>()
        val categoryExpenditures = mutableMapOf<CategoryObject, Int>()




        homeViewModel.userId?.let { homeViewModel.db.collection(homeViewModel.userId).get().addOnSuccessListener {
        results ->
        for(document in results) {
           val docObject = document.toObject(CategoryObject::class.java)
            allCategories.put(docObject.toString(),docObject)
        } }



            for(category in allCategories)
            {
                homeViewModel.userId?.let { homeViewModel.db.collection(homeViewModel.userId).document(category.key)
                    .collection("expenses").get().addOnSuccessListener {
                        results ->
                        var categoryExpense = 0
                        for (expense in results)
                        {
                            val expenseObject = expense.toObject(ExpenseObject::class.java)
                            categoryExpense +=expenseObject.ExpenseAmount
                        }

                    }

                }

            }



        }







        val fabExpense: FloatingActionButton = binding.floatingActionButton

        fabExpense.setOnClickListener {
            val intent = Intent(context, AddExpenseActivity::class.java)
            startActivity(intent)
        }
        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}