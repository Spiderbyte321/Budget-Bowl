package com.jacob.budgetbowl.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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

    // Listeners for real-time updates
    private var userProfileListener: ListenerRegistration? = null
    private var categoriesListener: ListenerRegistration? = null

    private var totalBudget: Int = 0
    private var totalSpent: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(context, AddExpenseActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        setupRealtimeListeners()
    }

    override fun onPause() {
        super.onPause()
        // Detach listeners to prevent memory leaks and unwanted background updates
        userProfileListener?.remove()
        categoriesListener?.remove()
    }

    private fun setupRealtimeListeners() {
        val user = auth.currentUser ?: return
        val uid = user.uid

        // Listener for the user profile document (profile info and total budget)
        userProfileListener = db.collection("users").document(uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(context, "Error loading user profile: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    binding.txtProfileName.text = snapshot.getString("fullName")
                    binding.txtProfileUsername.text = snapshot.getString("userName")
                    val profileIcon = snapshot.getLong("profileIcon")?.toInt()
                    if (profileIcon != null) {
                        binding.ProfilePic.setImageResource(profileIcon)
                    } else {
                        binding.ProfilePic.setImageResource(R.drawable.pfpwithborder__3_)
                    }

                    totalBudget = snapshot.getLong("targetBudget")?.toInt() ?: 0
                    updateOverallBudgetUI()
                } else {
                    Toast.makeText(context, "User profile not found", Toast.LENGTH_SHORT).show()
                }
            }

        // Listener for the categories collection (individual category expenditures)
        categoriesListener = db.collection("users").document(uid).collection("categories")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(context, "Error loading categories: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    totalSpent = snapshot.documents.sumOf { it.toObject(CategoryObject::class.java)?.categoryTotalExpenditure ?: 0 }
                    updateOverallBudgetUI()

                    // Update the categories RecyclerView
                    val allAppCategories = ECategory.values().map { it.name }
                    val existingDbCategories = snapshot.documents.map { it.id to it.toObject(CategoryObject::class.java)!! }.toMap()

                    val finalCategoryList = allAppCategories.map { categoryName ->
                        val categoryObject = existingDbCategories[categoryName] ?: CategoryObject(targetBudget = 1, categoryTotalExpenditure = 0)
                        Pair(categoryName, categoryObject)
                    }

                    binding.rvCategoryProgress.adapter = CategoryProgressAdapter(finalCategoryList)
                    binding.rvCategoryProgress.layoutManager = LinearLayoutManager(context)
                }
            }
    }

    private fun updateOverallBudgetUI() {
        binding.numberOfCalories.text = "$totalSpent / $totalBudget"
        if (totalBudget > 0) {
            val overallProgress = (totalSpent.toDouble() / totalBudget * 100).toInt()
            binding.statsProgressbar.progress = overallProgress
        } else {
            binding.statsProgressbar.progress = 0
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}