package com.jacob.budgetbowl

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

// Data class to hold the information for each new category
data class NewCategory(var name: String = "", var budget: String = "", var icon: Int? = null)

class CreateCategories : AppCompatActivity() {

    private lateinit var rvCategories: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var btnSaveCategories: Button
    private lateinit var homeButton: ImageView
    private lateinit var categoryAdapter: CategoryCreationAdapter
    private val categoryList = mutableListOf<NewCategory>()
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_categories)

        rvCategories = findViewById(R.id.rvCategories)
        fab = findViewById(R.id.floatingActionButton)
        btnSaveCategories = findViewById(R.id.btnSaveCategories)
        homeButton = findViewById(R.id.homeButton)

        setupRecyclerView()
        loadUserCategories()

        fab.setOnClickListener {
            categoryList.add(NewCategory())
            categoryAdapter.notifyItemInserted(categoryList.size - 1)
        }

        btnSaveCategories.setOnClickListener {
            saveCategoriesToFirebase()
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadUserCategories() {
        if (userId == null) {
            Toast.makeText(this, "You must be logged in to manage categories.", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").document(userId).collection("categories").get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // No categories found for the user, set up the defaults.
                    setupDefaultCategories()
                } else {
                    // User has existing categories, load them into the RecyclerView.
                    categoryList.clear()
                    val loadedCategories = documents.map { doc ->
                        val categoryObject = doc.toObject(CategoryObject::class.java)
                        NewCategory(
                            name = doc.id,
                            budget = categoryObject.targetBudget.toString(),
                            icon = categoryObject.icon
                        )
                    }
                    categoryList.addAll(loadedCategories)
                    categoryAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading categories: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupDefaultCategories() {
        val defaults = listOf(
            NewCategory("Groceries", "", R.drawable.catfood),
            NewCategory("Utilities", "", R.drawable.catutilities),
            NewCategory("Rent", "", R.drawable.cathome)
        )
        categoryList.clear()
        categoryList.addAll(defaults)
        categoryAdapter.notifyDataSetChanged() // Update adapter with defaults
        saveCategoriesToFirebase(defaults, showToast = false) // Save defaults for the user
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryCreationAdapter(categoryList) { position ->
            showIconPickerDialog { iconResId ->
                categoryList[position].icon = iconResId
                categoryAdapter.notifyItemChanged(position)
            }
        }
        rvCategories.adapter = categoryAdapter
        rvCategories.layoutManager = LinearLayoutManager(this)
    }

    private fun showIconPickerDialog(onIconSelected: (Int) -> Unit) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_icon_picker, null)
        val gridView = dialogView.findViewById<GridView>(R.id.gvIconGrid)

        val iconList = R.drawable::class.java.fields.filter {
            it.name.startsWith("cat")
        }.map { it.getInt(null) }

        val adapter = CategoryIconAdapter(this, iconList)
        gridView.adapter = adapter

        val dialog = AlertDialog.Builder(this)
            .setTitle("Select an Icon")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .create()

        gridView.setOnItemClickListener { _, _, position, _ ->
            onIconSelected(iconList[position])
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveCategoriesToFirebase(categoriesToSave: List<NewCategory> = categoryList, showToast: Boolean = true) {
        if (userId == null) {
            if(showToast) Toast.makeText(this, "You must be logged in to save.", Toast.LENGTH_SHORT).show()
            return
        }

        // Duplicate name check
        val names = categoriesToSave.map { it.name.trim() }.filter { it.isNotEmpty() }
        if (names.size != names.toSet().size) {
            AlertDialog.Builder(this)
                .setTitle("Duplicate Names")
                .setMessage("Two or more categories cannot have the same name. Please ensure all category names are unique.")
                .setPositiveButton("OK", null)
                .show()
            return
        }

        val batch = db.batch()

        for (category in categoriesToSave) {
            if (category.name.isNotBlank()) {
                val budget = category.budget.toIntOrNull() ?: 0
                // Using a map to only update specific fields, preserving the expenditure.
                val categoryData = mapOf(
                    "targetBudget" to budget,
                    "icon" to category.icon
                )
                val docRef = db.collection("users").document(userId).collection("categories").document(category.name.trim())
                // Set with merge option to update existing documents without overwriting other fields.
                batch.set(docRef, categoryData, SetOptions.merge())
            }
        }

        batch.commit()
            .addOnSuccessListener {
                if (showToast) {
                    Toast.makeText(this, "Categories saved successfully!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                }
            }
            .addOnFailureListener { e ->
                if (showToast) {
                    Toast.makeText(this, "Error saving categories: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

class CategoryCreationAdapter(
    private val categories: MutableList<NewCategory>,
    private val onIconClick: (Int) -> Unit
) : RecyclerView.Adapter<CategoryCreationAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View, val nameWatcher: NameTextWatcher, val budgetWatcher: BudgetTextWatcher) : RecyclerView.ViewHolder(itemView) {
        val categoryName: EditText = itemView.findViewById(R.id.etCategoryName)
        val categoryBudget: EditText = itemView.findViewById(R.id.etCategoryBudget)
        val selectIconButton: Button = itemView.findViewById(R.id.btnSelectIcon)
        val categoryIcon: ImageView = itemView.findViewById(R.id.ivCategoryIcon)

        init {
            categoryName.addTextChangedListener(nameWatcher)
            categoryBudget.addTextChangedListener(budgetWatcher)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_create, parent, false)
        return CategoryViewHolder(view, NameTextWatcher(categories), BudgetTextWatcher(categories))
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]

        holder.nameWatcher.updatePosition(holder.adapterPosition)
        holder.budgetWatcher.updatePosition(holder.adapterPosition)

        holder.categoryName.setText(category.name)
        holder.categoryBudget.setText(category.budget)
        category.icon?.let { holder.categoryIcon.setImageResource(it) }

        holder.selectIconButton.setOnClickListener {
            onIconClick(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}

// Custom TextWatchers to update the data model
class NameTextWatcher(private val categories: MutableList<NewCategory>) : TextWatcher {
    private var position = 0
    fun updatePosition(position: Int) {
        this.position = position
    }
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (position < categories.size) {
            categories[position].name = s.toString()
        }
    }
    override fun afterTextChanged(s: Editable?) {}
}

class BudgetTextWatcher(private val categories: MutableList<NewCategory>) : TextWatcher {
    private var position = 0
    fun updatePosition(position: Int) {
        this.position = position
    }
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (position < categories.size) {
            categories[position].budget = s.toString()
        }
    }
    override fun afterTextChanged(s: Editable?) {}
}

class CategoryIconAdapter(private val context: Context, private val icons: List<Int>) : BaseAdapter() {

    override fun getCount(): Int = icons.size

    override fun getItem(position: Int): Any = icons[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_icon, parent, false)
        val imageView = view.findViewById<ImageView>(R.id.ivIcon)
        imageView.setImageResource(icons[position])
        return view
    }
}