package com.jacob.budgetbowl.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jacob.budgetbowl.AddExpenseActivity
import com.jacob.budgetbowl.CategoryObject
import com.jacob.budgetbowl.ECategory
import com.jacob.budgetbowl.R
import com.jacob.budgetbowl.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        loadUserProfileAndBudgetData()
        setupCategoryRecyclerView()

        val fabExpense: FloatingActionButton = binding.floatingActionButton

        fabExpense.setOnClickListener {
            val intent = Intent(context, AddExpenseActivity::class.java)
            startActivity(intent)
        }
        return root
    }

    private fun loadUserProfileAndBudgetData() {
        val user = auth.currentUser
        if (user != null) {
            val uid = user.uid
            // 1. Fetch User Profile from /users/{uid}
            db.collection("users").document(uid).get()
                .addOnSuccessListener { userProfileDoc ->
                    if (userProfileDoc != null && userProfileDoc.exists()) {
                        // Update profile UI
                        binding.txtProfileName.text = userProfileDoc.getString("fullName")
                        binding.txtProfileUsername.text = userProfileDoc.getString("userName")
                        val profileIcon = userProfileDoc.getLong("profileIcon")?.toInt()
                        if (profileIcon != null) {
                            binding.ProfilePic.setImageResource(profileIcon)
                        } else {
                            binding.ProfilePic.setImageResource(R.drawable.pfpwithborder__3_)
                        }

                        // 2. Fetch Overall Budget from /{uid}/overallbudget
                        db.collection(uid).document("overallbudget").get()
                            .addOnSuccessListener { budgetDoc ->
                                val totalBudget = if (budgetDoc != null && budgetDoc.exists()) {
                                    // Field from SetInitialBudgetActivity: "targetbudget" (String)
                                    budgetDoc.getString("targetbudget")?.toInt() ?: 0
                                } else {
                                    0
                                }

                                // 3. Fetch categories to calculate total spent
                                db.collection("users").document(uid).collection("categories").get()
                                    .addOnSuccessListener { categoryDocs ->
                                        val totalSpent = categoryDocs.sumOf { it.toObject(CategoryObject::class.java).categoryTotalExpenditure }
                                        
                                        // 4. Update Budget UI
                                        binding.numberOfCalories.text = "$totalSpent / $totalBudget"
                                        
                                        if (totalBudget > 0) {
                                            val overallProgress = (totalSpent.toDouble() / totalBudget * 100).toInt()
                                            binding.statsProgressbar.progress = overallProgress
                                        } else {
                                            binding.statsProgressbar.progress = 0
                                        }
                                    }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Failed to load budget data: ${e.message}", Toast.LENGTH_LONG).show()
                            }

                    } else {
                        Toast.makeText(context, "User profile not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to load user data: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun setupCategoryRecyclerView() {
        val user = auth.currentUser
        if (user != null) {
            val uid = user.uid
            db.collection("users").document(uid).collection("categories").get()
                .addOnSuccessListener { documents ->
                    val allAppCategories = ECategory.values().map { it.name }
                    val existingDbCategories = documents.map { it.id to it.toObject(CategoryObject::class.java) }.toMap()

                    val finalCategoryList = allAppCategories.map {
                        categoryName ->
                        val categoryObject = existingDbCategories[categoryName] ?: CategoryObject(targetBudget = 1, categoryTotalExpenditure = 0)
                        Pair(categoryName, categoryObject)
                    }

                    binding.rvCategoryProgress.adapter = CategoryProgressAdapter(finalCategoryList)
                    binding.rvCategoryProgress.layoutManager = LinearLayoutManager(context)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to load categories: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}