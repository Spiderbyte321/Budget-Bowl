package com.jacob.budgetbowl.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jacob.budgetbowl.CategoryObject
import com.jacob.budgetbowl.DashboardActivity
import com.jacob.budgetbowl.ECategory
import com.jacob.budgetbowl.ExpenseEntry
import com.jacob.budgetbowl.R
import com.jacob.budgetbowl.databinding.ActivityCategoryGraphBinding
import com.jacob.budgetbowl.ui.ExpenseReview.DatePickerFragment
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CategoryGraph : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryGraphBinding
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var expenseList: MutableList<ExpenseEntry>
    private lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pieChart = binding.pieChart
        setupPieChart()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            ECategory.values()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCategory.adapter = adapter

        expenseList = mutableListOf()
        fetchExpenses()

        binding.homeButton.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        binding.startDatee.setOnClickListener {
            val datePicker = DatePickerFragment.newInstance("startDateRequestKey")
            datePicker.show(supportFragmentManager, "datePicker")
        }

        binding.EndDate.setOnClickListener {
            val datePicker = DatePickerFragment.newInstance("endDateRequestKey")
            datePicker.show(supportFragmentManager, "datePicker")
        }

        binding.filterBTN.setOnClickListener {
            filterExpenses()
        }

        supportFragmentManager.setFragmentResultListener("startDateRequestKey", this) { _, bundle ->
            val result = bundle.getString("selectedDate")
            binding.startDatee.setText(result)
        }

        supportFragmentManager.setFragmentResultListener("endDateRequestKey", this) { _, bundle ->
            val result = bundle.getString("selectedDate")
            binding.EndDate.setText(result)
        }

        binding.spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                updateCategorySpending()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun setupPieChart() {
        pieChart.isDrawHoleEnabled = true
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.centerText = "Expenses by Category"
        pieChart.setCenterTextSize(24f)
        pieChart.description.isEnabled = false
        pieChart.legend.textSize = 16f
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
                    updatePieChart(expenseList)
                    updateCategorySpending() // Initial update
                    updateTotalBudgetProgressBar(expenseList)
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error getting documents: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun filterExpenses() {
        val filteredList = mutableListOf<ExpenseEntry>()
        val startDate = binding.startDatee.text.toString()
        val endDate = binding.EndDate.text.toString()

        if (startDate.isEmpty() || endDate.isEmpty()) {
            updatePieChart(expenseList)
            updateCategorySpending() // Update with full list
            updateTotalBudgetProgressBar(expenseList)
            return
        }

        val dateFormatter = SimpleDateFormat("EEE, d MMM", Locale.getDefault())

        val startObject: Date?
        val endObject: Date?

        try {
            startObject = dateFormatter.parse(startDate)
            endObject = dateFormatter.parse(endDate)
        } catch (e: ParseException) {
            Toast.makeText(this, "Date formatted incorrectly must be : EEE, d MMM", Toast.LENGTH_SHORT).show()
            return
        }

        for (expense in expenseList) {
            val expenseDateObject: Date?
            try {
                expenseDateObject = dateFormatter.parse(expense.expenseDate)
            } catch (e: ParseException) {
                continue
            }

            if (expenseDateObject.after(startObject) && expenseDateObject.before(endObject)) {
                filteredList.add(expense)
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No items found widen date range", Toast.LENGTH_SHORT).show()
        }

        updatePieChart(filteredList)
        updateCategorySpending(filteredList) // Update with filtered list
        updateTotalBudgetProgressBar(filteredList)
    }

    private fun updatePieChart(list: List<ExpenseEntry>) {
        val entries = ArrayList<PieEntry>()
        val categoryMap = ECategory.values().associate { it.name to 0f }.toMutableMap()

        for (expense in list) {
            val amount = expense.expenseAmount.toFloat()
            categoryMap[expense.category] = (categoryMap[expense.category] ?: 0f) + amount
        }

        for ((category, total) in categoryMap) {
            entries.add(PieEntry(total, category))
        }

        val dataSet = PieDataSet(entries, "Expense Categories")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        val data = PieData(dataSet)
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.BLACK)

        pieChart.data = data
        pieChart.invalidate()
    }

    private fun updateCategorySpending(list: List<ExpenseEntry> = expenseList) {
        val selectedCategory = binding.spCategory.selectedItem.toString()

        if (userId != null) {
            db.collection("users").document(userId).collection("categories").document(selectedCategory)
                .get()
                .addOnSuccessListener { document ->
                    val categoryObject = document.toObject(CategoryObject::class.java)
                    val budget = categoryObject?.targetBudget ?: 0
                    var totalSpent = 0
                    for (expense in list) {
                        if (expense.category == selectedCategory) {
                            totalSpent += expense.expenseAmount
                        }
                    }
                    binding.txtCategorySpending.text = "$totalSpent / $budget"
                }
                .addOnFailureListener { exception ->
                    binding.txtCategorySpending.text = "0 / 0"
                    Toast.makeText(this, "Error getting category budget: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateTotalBudgetProgressBar(list: List<ExpenseEntry> = expenseList) {
        if (userId != null) {
            db.collection("users").document(userId).collection("categories")
                .get()
                .addOnSuccessListener { result ->
                    var totalBudget = 0
                    for (document in result) {
                        val category = document.toObject(CategoryObject::class.java)
                        totalBudget += category.targetBudget
                    }

                    var totalSpent = 0
                    for (expense in list) {
                        totalSpent += expense.expenseAmount
                    }

                    val percentage = if (totalBudget > 0) {
                        (totalSpent.toFloat() / totalBudget.toFloat() * 100).toInt()
                    } else {
                        0
                    }

                    binding.totalBudgetProgressBar.progress = percentage
                    binding.txtTotalBudgetPercentage.text = "$percentage%"
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error getting total budget: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
