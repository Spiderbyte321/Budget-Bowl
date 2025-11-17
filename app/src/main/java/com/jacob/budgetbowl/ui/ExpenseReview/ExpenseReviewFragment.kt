package com.jacob.budgetbowl.ui.ExpenseReview

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jacob.budgetbowl.CustomRecyclerAdapter
import com.jacob.budgetbowl.DashboardActivity
import com.jacob.budgetbowl.ECategory
import com.jacob.budgetbowl.ExpenseEntry
import com.jacob.budgetbowl.databinding.FragmentExpenseReviewBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpenseReviewFragment : Fragment() {

    private var binding: FragmentExpenseReviewBinding? = null
    private val Binding get() = binding!!

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var expenseList: MutableList<ExpenseEntry>
    private lateinit var newAdapter: CustomRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentFragmentManager.setFragmentResultListener("startDateRequestKey", this) { _, bundle ->
            val result = bundle.getString("selectedDate")
            Binding.startDatee.setText(result)
        }

        parentFragmentManager.setFragmentResultListener("endDateRequestKey", this) { _, bundle ->
            val result = bundle.getString("selectedDate")
            Binding.EndDate.setText(result)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExpenseReviewBinding.inflate(inflater, container, false)
        return Binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = ArrayAdapter<ECategory>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            ECategory.entries
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        Binding.spCategory.adapter = adapter

        expenseList = mutableListOf()
        newAdapter = CustomRecyclerAdapter(expenseList, this)

        val recycler: RecyclerView = Binding.Recycler
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = newAdapter

        fetchExpenses()

        Binding.homeButton.setOnClickListener {
            val intent = Intent(activity, DashboardActivity::class.java)
            startActivity(intent)
        }

        Binding.startDatee.setOnClickListener {
            val datePicker = DatePickerFragment.newInstance("startDateRequestKey")
            datePicker.show(parentFragmentManager, "datePicker")
        }

        Binding.EndDate.setOnClickListener {
            val datePicker = DatePickerFragment.newInstance("endDateRequestKey")
            datePicker.show(parentFragmentManager, "datePicker")
        }

        Binding.filterBTN.setOnClickListener {
            filterExpenses()
        }
    }

    private fun fetchExpenses() {
        if (userId != null) {
            db.collection("users").document(userId).collection("expenses")
                .get()
                .addOnSuccessListener { result ->
                    expenseList.clear()
                    for (document in result) {
                        val expense = document.toObject(ExpenseEntry::class.java)
                        expenseList.add(expense)
                    }
                    newAdapter.updateAdapter(expenseList)
                    updateTotalSpent(expenseList)
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Error getting documents: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun filterExpenses() {
        val filteredList = mutableListOf<ExpenseEntry>()
        val category = Binding.spCategory.selectedItem.toString()
        val startDate = Binding.startDatee.text.toString()
        val endDate = Binding.EndDate.text.toString()
        val dateFormatter = SimpleDateFormat("EEE, d MMM", Locale.getDefault())

        val startObject: Date?
        val endObject: Date?

        try {
            startObject = dateFormatter.parse(startDate)
            endObject = dateFormatter.parse(endDate)
        } catch (e: ParseException) {
            Toast.makeText(context, "Date formatted incorrectly must be : EEE, d MMM", Toast.LENGTH_SHORT).show()
            return
        }

        for (expense in expenseList) {
            val expenseDateObject: Date?
            try {
                expenseDateObject = dateFormatter.parse(expense.expenseDate)
            } catch (e: ParseException) {
                continue
            }

            if (expenseDateObject.after(startObject) && expenseDateObject.before(endObject) && expense.category == category) {
                filteredList.add(expense)
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(context, "No items found widen date range", Toast.LENGTH_SHORT).show()
        }

        newAdapter.updateAdapter(filteredList)
        updateTotalSpent(filteredList)
    }

    private fun updateTotalSpent(list: List<ExpenseEntry>) {
        var totalSpent = 0
        for (expense in list) {
            totalSpent += expense.expenseAmount
        }
        Binding.Spent.text = "Total Spent: $totalSpent"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
